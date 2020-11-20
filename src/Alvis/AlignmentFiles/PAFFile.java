// Alvis
//
// Alignment Diagrams in LaTeX and SVG
//
// Copyright 2018 Richard Leggett, Samuel Martin
// samuel.martin@earlham.ac.uk
// 
// This is free software, supplied without warranty.

package Alvis.AlignmentFiles;

import Alvis.AlignmentFilters.AlignmentFilter;
import Alvis.AlignmentFilters.ChimeraFilter;
import java.util.*;
import java.io.*;
/**
 *
 * @author martins
 */
public class PAFFile implements DetailedAlignmentFile 
{
    private ArrayList<DetailedAlignment> alignments = new ArrayList();
    private Hashtable<String,Integer> targetHits = new Hashtable();
    
    public PAFFile(String filename) 
    {        
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(filename));          
            String line = br.readLine();
            while (line != null) 
            {
                String[] fields = line.split("\\t");

                if (fields.length >= 12) 
                {                
                    PAFAlignment a = new PAFAlignment(line);
                    if (a != null) 
                    {
                        // add the alignment to the list
                        alignments.add(a);
                        
                        // increment hit count for alignments target
                        addTargetHit(a.getTargetName());
                    }
                } 
                else 
                {
                    System.out.println("Error: line does not appear to be in PAF format, too few fields.");
                    System.out.println("Line not recognised: "+line);
                    System.out.println("Terminating...");
                    System.exit(0);
                }

                line = br.readLine();
            } 
            br.close();
        } 
        catch (Exception ioe) 
        {
            System.out.println("Exception:");
            System.out.println(ioe);
        }
        
        Collections.sort(alignments);        
    }
    
    public int getNumberOfAlignments() 
    {
        return alignments.size();
    }
    
    public DetailedAlignment getAlignment(int i) 
    {
        return alignments.get(i);
    }
    
    public Hashtable getTargetHits() {
        return targetHits;
    }
    
    public int getTargetHitCount(String target) 
    {
        Integer a = targetHits.get(target);      
        if (a == null) 
        {
            System.out.println("Something went wrong - unknown target.");
            System.exit(-1);
        }
        return a.intValue();
    }  
    
    public void filterAlignments(AlignmentFilter filter)
    {
        alignments = filter.filterAlignments(alignments);
        
        //update targetHits:
        targetHits = new Hashtable();
        for(Alignment a : alignments)
        {
            addTargetHit(a.getTargetName());
        }
    }
    
    public void sort(Comparator<? super Alignment> comparator)
    {
         Collections.sort(alignments, comparator);
    }
    
    public void sort(AlignmentSorter sorter)
    {
        alignments = sorter.sort(alignments);
    }
    
    public LinkedHashSet<String> getChimeras(ChimeraFilter filter)
    {
        return filter.getChimericContigs(alignments);
    }
    
    private void addTargetHit(String targetName)
    {
        // increment hit count for alignments target
       Integer count = targetHits.get(targetName);
       if (count == null) 
       {
           count = new Integer(1);
       }
       else 
       {
           count = new Integer(count.intValue() + 1);
       }
       targetHits.put(targetName, count);       
    }
}
