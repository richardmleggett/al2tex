// Alvis
//
// Alignment Diagrams in LaTeX and SVG
//
// Copyright 2018 Richard Leggett, Samuel Martin
// samuel.martin@earlham.ac.uk
// 
// This is free software, supplied without warranty.

package Alvis.AlignmentFilters;

import Alvis.AlignmentFiles.DetailedAlignment;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 *
 * @author martins
 */
public class ChimeraFilter implements AlignmentFilter
{
    private float m_minCoverageForTarget;
    private float m_minTotalCoverage;
    private float m_maxQueryGapDistance;
    private float m_minTargetGapDistance;
    private ArrayList<DetailedAlignment> m_chimeras;
    
    public ChimeraFilter(float minCoverageForTarget, float minTotalCoverage)
    {
        m_minCoverageForTarget = minCoverageForTarget;
        m_minTotalCoverage = minTotalCoverage;
        m_chimeras = new ArrayList();
        m_maxQueryGapDistance = 0.01f;
        m_minTargetGapDistance = 0.5f;
    }
    
    public ArrayList<DetailedAlignment> filterAlignments(ArrayList<DetailedAlignment> alignments)
    {
        System.out.println("Filtering for possible chimeras, removing everything else.");
        if(m_chimeras.isEmpty())
        {
            m_chimeras = findChimeras(alignments);
        }
        return m_chimeras;
    }
    
    private ArrayList<DetailedAlignment> findChimeras(ArrayList<DetailedAlignment> alignments)
    {
        //first, sort the alignments by query name
        Collections.sort(alignments, DetailedAlignment.compareByQueryName);
        ArrayList<DetailedAlignment> filteredAlignments = new ArrayList();
        
        // group alignments into arrays by query name
        int i = 0;
        while(i < alignments.size())
        {
            String lastQueryName = alignments.get(i).getQueryName();
            ArrayList<DetailedAlignment> alignmentsForQuery = new ArrayList();
            alignmentsForQuery.add(alignments.get(i));
            i++;
            while(i < alignments.size() && alignments.get(i).getQueryName().equals(lastQueryName))
            {
                alignmentsForQuery.add(alignments.get(i));
                i++;
            }
        
            // identify the chimeras
            if(isChimera2(alignmentsForQuery))
            {
                filteredAlignments.addAll(alignmentsForQuery);
            }
        }
        return filteredAlignments;
    }
    
    // TODO: Look for chimera's where all alignments have the same target. Currently these will be ignored.
    // what about circular genomes?
    private boolean isChimera(ArrayList<DetailedAlignment> alignments)
    {
        HashMap<String, Float> coveragesByTarget = new HashMap();
        float totalCoverage = 0.0f;
        String queryName = alignments.get(0).getQueryName();
        for(DetailedAlignment alignment : alignments)
        {
            assert(alignment.getQueryName().equals(queryName));
            
            String targetName = alignment.getTargetName();
            float coverage = Math.abs(alignment.getQueryEnd() - alignment.getQueryStart()) / (float)alignment.getQuerySize();
            totalCoverage += coverage;
            Float oldCoverageValue = coveragesByTarget.get(targetName);
            if(oldCoverageValue == null)
            {
                coveragesByTarget.put(targetName, coverage);
            }
            else
            {
                Float newCoverage = oldCoverageValue + coverage;
                coveragesByTarget.put(targetName, newCoverage);
            }
        }
        
        if(totalCoverage > m_minTotalCoverage)
        {
            Collection<Float> coverages = coveragesByTarget.values();
            int count = 0;
            for(Float coverage : coverages)
            {
                if(coverage > m_minCoverageForTarget)
                {
                    count += 1;
                    if(count ==2)
                    {
                        return true;
                    }
                }
            }
        }       
        return false;
    }
    
    public LinkedHashSet<String> getChimericContigs(ArrayList<DetailedAlignment> alignments)
    {
        if(m_chimeras.isEmpty())
        {
            m_chimeras = findChimeras(alignments);
        }               
        LinkedHashSet<String> names = new LinkedHashSet();
        for(DetailedAlignment alignment : m_chimeras)
        {
            names.add(alignment.getQueryName());
        }
        return names;
    }
    
    public void writeChimeraFile(String filename)
    {
        if(m_chimeras.isEmpty())
        {
            System.out.println("No chimeras to report");
            return;
        }
        // order alignments by query and then by query start
        m_chimeras.sort(chimeraComparator);
        
        // output format: queryName \t approx location of chimera on query \t ref1 \t ref2 \n
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            String lastRef = m_chimeras.get(0).getTargetName();
            String lastQuery = m_chimeras.get(0).getQueryName();
            int lastQueryEnd = m_chimeras.get(0).getQueryEnd();
            for(DetailedAlignment alignment : m_chimeras)
            {
                String queryName = alignment.getQueryName();
                String refName = alignment.getTargetName();
                if(queryName.equals(lastQuery))
                {
                    if(!refName.equals(lastRef))
                    {
                        //write the Chimera
                        int chimeraPos = (lastQueryEnd + alignment.getQueryStart()) / 2;
                        writer.write(queryName + "\t" + chimeraPos + "\t" + lastRef + "\t" + refName + "\n");
                        lastRef = alignment.getTargetName();
                    }
                }
                else
                {
                    // move on to the next query
                    lastQuery = queryName;
                }
                lastQueryEnd = alignment.getQueryEnd();
            }
            
            writer.flush();
            writer.close();
        }
        catch(IOException e)
        {
            System.out.println("Could not open " + filename + " for writing chimeras");
        }
        
    }
    
    public static Comparator chimeraComparator = new Comparator<DetailedAlignment>()
    {
        public int compare(DetailedAlignment alignment1, DetailedAlignment alignment2) 
        {
            int result = alignment1.getTargetName().compareTo(alignment2.getTargetName());
            if(result == 0)
            {
                result = alignment1.getQueryName().compareTo(alignment2.getQueryName());
                if(result == 0)
                {
                    result = alignment1.getQueryStart() - alignment2.getQueryStart();
                }
            }
            return result;
        }
    };
    
    private boolean isChimera2(ArrayList<DetailedAlignment> alignments)
    {
        alignments.sort(DetailedAlignment.compareByQueryStart);
        DetailedAlignment firstAlignment = alignments.get(0);
        String lastTarget = firstAlignment.getTargetName();
        int lastQueryEnd = firstAlignment.getQueryEnd();
        int lastQueryStart = firstAlignment.getQueryStart();
        int lastTargetEnd = firstAlignment.getTargetEnd();
        boolean lastOrientation = firstAlignment.isReverseAlignment();
        
        int maxQueryDistBP = (int)(m_maxQueryGapDistance * alignments.get(0).getQuerySize());
        for(int i = 1; i < alignments.size(); i++)
        {
            DetailedAlignment alignment = alignments.get(i);
            int maxTargetDistBP = (int)(m_minTargetGapDistance * alignment.getTargetSize());
            
            int queryStart = alignment.getQueryStart();
            assert(queryStart > lastQueryStart);
            if(queryStart > lastQueryEnd && queryStart - lastQueryEnd < maxQueryDistBP)
            {
//                if(alignment.isReverseAlignment() != lastOrientation)
//                {
//                    return true;
//                }
                if(!lastTarget.equals(alignment.getTargetName()))
                {
                    return true;
                }
                else
                {
                    if(Math.abs(lastTargetEnd - alignment.getTargetStart()) % alignment.getTargetSize() > maxTargetDistBP)
                    {
                        return true;
                    }
                }
            }
            lastTarget = alignment.getTargetName();
            lastQueryStart = alignment.getQueryStart();
            lastQueryEnd = alignment.getQueryEnd();
            lastTargetEnd = alignment.getTargetEnd();
            lastOrientation = alignment.isReverseAlignment();
        }
        return false;
    }
}
