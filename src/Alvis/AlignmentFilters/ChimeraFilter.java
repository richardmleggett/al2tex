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
import java.util.*;

/**
 *
 * @author martins
 */
public class ChimeraFilter implements AlignmentFilter
{
    private float m_minCoverageForTarget;
    private float m_minTotalCoverage;
    
    public ChimeraFilter(float minCoverageForTarget, float minTotalCoverage)
    {
        m_minCoverageForTarget = minCoverageForTarget;
        m_minTotalCoverage = minTotalCoverage;
    }
    
    public ArrayList<DetailedAlignment> filterAlignments(ArrayList<DetailedAlignment> alignments)
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
            if(isChimera(alignmentsForQuery))
            {
                filteredAlignments.addAll(alignmentsForQuery);
            }
        }
        return filteredAlignments;
    }
    
    private boolean isChimera(ArrayList<DetailedAlignment> alignments)
    {
        HashMap<String, Float> coveragesByTarget = new HashMap();
        float totalCoverage = 0.0f;
        
        for(DetailedAlignment alignment : alignments)
        {
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
                System.out.println(alignment.getQueryName() + "-" + alignment.getTargetName() + ": " + Float.toString(newCoverage));
            }
        }
        System.out.println("Total: " + Float.toString(totalCoverage));
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
}
