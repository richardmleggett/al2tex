// Alvis
//
// Alignment Diagrams in LaTeX and SVG
//
// Copyright 2018 Richard Leggett, Samuel Martin
// samuel.martin@earlham.ac.uk
// 
// This is free software, supplied without warranty.

package Alvis.AlignmentFiles;

import Alvis.AlignmentFilters.*;
import Alvis.DiagramOptions;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.LinkedHashSet;

/**
 *
 * @author martins
 */
public class BlastFile implements DetailedAlignmentFile 
{

    private ArrayList<DetailedAlignment> m_alignments = new ArrayList();
    private Hashtable<String,Integer> m_targetHits = new Hashtable();
    
    public BlastFile(DiagramOptions options) 
    {        
        try
        {
            String filename = options.getInputFilename();
            BufferedReader br = new BufferedReader(new FileReader(filename));          
            String formatString = options.getBlastFormatString();
            if(formatString == null || formatString.isEmpty())
            {
                System.out.println("No format string provided for blast tabular file.");
                System.out.println("Assuming default outfmt 6 was used.");
                formatString = "6 qseqid sseqid pident length mismatch gapopen qstart qend sstart send evalue bitscore";
            }
            else
            {          
                if(!formatString.split(" ")[0].equals("6"))
                {
                    System.out.println("Blast file must be in tabular format, i.e. -outfmt '6 ...'");
                    System.out.println("Terminating");
                    System.exit(0);
                }

                if( !formatString.contains("qseqid") || !formatString.contains("qlen") || !formatString.contains("qstart") || !formatString.contains("qend") ||
                    !formatString.contains("sseqid") || !formatString.contains("slen") || !formatString.contains("sstart") || !formatString.contains("send"))
                {
                    System.out.println("Blast tabular must contain the fields qseqid, qlen, qstart, qend, sseqid, slen, sstart, send.");
                    System.out.println("Terminating");
                    System.exit(0);
                }
            }
            
            String line = br.readLine();
            while (line != null) 
            {
                String[] fields = line.split("\\t");
             
                BlastAlignment a = new BlastAlignment(line, formatString);

                // add the alignment to the list
                m_alignments.add(a);

                // increment hit count for alignments target
                addTargetHit(a.getTargetName());
              
                line = br.readLine();
            } 
            br.close();
        } 
        catch (Exception ioe) 
        {
            System.out.println("Exception:");
            System.out.println(ioe);
        }
        
        // why is this here?
        Collections.sort(m_alignments);        
    }
    
    public int getNumberOfAlignments() 
    {
        return m_alignments.size();
    }
    
    public DetailedAlignment getAlignment(int i) 
    {
        return m_alignments.get(i);
    }
       
    public Hashtable getTargetHits() {
        return m_targetHits;
    }
    
    public int getTargetHitCount(String target) {
        Integer a = m_targetHits.get(target);      
        if (a == null) 
        {
            System.out.println("Something went wrong - unknown target.");
            System.exit(-1);
        }
        return a.intValue();
    }
      
    public void filterAlignments(AlignmentFilter filter)
    {
        m_alignments = filter.filterAlignments(m_alignments);
        
        //update targetHits:
        m_targetHits = new Hashtable();
        for(Alignment a : m_alignments)
        {
            addTargetHit(a.getTargetName());
        }
    }
    
    public void sort(Comparator<? super Alignment> comparator)
    {
        Collections.sort(m_alignments, comparator);
    }
    
    public void sort(AlignmentSorter sorter)
    {
        m_alignments = sorter.sort(m_alignments);
    }
    
    public LinkedHashSet<String> getChimeras(ChimeraFilter filter)
    {
        return filter.getChimericContigs(m_alignments);
    }
    
    private void addTargetHit(String targetName)
    {
        // increment hit count for alignments target
       Integer count = m_targetHits.get(targetName);
       if (count == null) 
       {
           count = new Integer(1);
       }
       else 
       {
           count = new Integer(count.intValue() + 1);
       }
       m_targetHits.put(targetName, count);       
    }
}
