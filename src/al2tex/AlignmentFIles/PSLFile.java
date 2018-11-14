// Al2Tex
//
// Alignment Diagrams in LaTeX
//
// Copyright 2012 Richard Leggett
// richard.leggett@tgac.ac.uk
// 
// This is free software, supplied without warranty.

package al2tex.AlignmentFiles;

import al2tex.AlignmentFilters.AlignmentFilter;
import java.util.*;
import java.io.*;
import java.lang.*;

public class PSLFile implements DetailedAlignmentFile {
    private ArrayList<DetailedAlignment> alignments = new ArrayList();
    private Hashtable<String,Integer> targetHits = new Hashtable();
    
    public PSLFile(String filename) {
        String line;
                
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(filename));

            line = br.readLine();
            
            // Skip header
            if (line.startsWith("psLayout")) {
                br.readLine();
                br.readLine();
                br.readLine();
                br.readLine();
                line = br.readLine();
            }
            
            while (line != null) {
                PSLAlignment a = new PSLAlignment(line);
                if (a != null) {
                    alignments.add(a);
                }
                
                Integer count = targetHits.get(a.getTargetName());
                
                if (count == null) {
                    count = new Integer(1);
                } else {
                    count = new Integer(count.intValue() + 1);
                }
                
                targetHits.put(a.getTargetName(), count);
                
                line = br.readLine();
            }

            br.close();
        } catch (Exception ioe) {
            System.out.println("Exception:");
            System.out.println(ioe);
        }
        
        Collections.sort(alignments);        
    }
    
    public int getNumberOfAlignments() {
        return alignments.size();
    }
    
    public DetailedAlignment getAlignment(int i) {
        return alignments.get(i);
    }
      
    public Hashtable getTargetHits() {
        return targetHits;
    }
    
    public int getTargetHitCount(String target) {
        Integer a = targetHits.get(target);
        
        if (a == null) {
            System.out.println("Something went wrong - unknown target.");
            System.exit(-1);
        }

        return a.intValue();
    }
    
    public void filterAlignments(AlignmentFilter filter)
    {
        alignments = filter.filterAlignments(alignments);
    }
    
    public void sortAlignments(Comparator<? super Alignment> comparator)
    {
         Collections.sort(alignments, comparator);
    }
}