/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package al2tex.AlignmentFIles;

import al2tex.AlignmentFilters.AlignmentFilter;
import al2tex.DiagramOptions;
import static al2tex.AlignmentFIles.AlignmentFile.compareByTargetName;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

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
                System.out.println("Please provide the format string as given to blastn.");
                System.out.println("Terminating");
                System.exit(0);
            }
            
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
            
            String line = br.readLine();
            while (line != null) 
            {
                String[] fields = line.split("\\t");
             
                BlastAlignment a = new BlastAlignment(line, formatString);

                // add the alignment to the list
                m_alignments.add(a);

                // increment hit count for alignments target
                Integer count = m_targetHits.get(a.getTargetName());
                if(count == null) 
                {
                    count = 1;
                }
                else 
                {
                    count = count + 1;
                }
                m_targetHits.put(a.getTargetName(), count);

                
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
    }
    
    public void sortAlignments(Comparator<? super Alignment> comparator)
    {
        Collections.sort(m_alignments, comparator);
    }
}
