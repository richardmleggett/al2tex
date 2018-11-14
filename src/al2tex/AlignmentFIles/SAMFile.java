// AlDiTex
//
// Alignment Diagrams in LaTeX
//
// Copyright 2012 Richard Leggett
// richard.leggett@tgac.ac.uk
// 
// This is free software, supplied without warranty.

package al2tex.AlignmentFIles;

import java.util.*;
import java.io.*;
import java.lang.*;

public class SAMFile implements AlignmentFile {
    private ArrayList<SAMAlignment> alignments = new ArrayList();
    private Hashtable<String,Integer> targetHits = new Hashtable();
    
    public SAMFile(String filename, int tSize) {
        String line;
                
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            
            line = br.readLine();
            while (line != null) {
                String[] fields = line.split("\\t");

                if (fields.length >= 11) {                
                    SAMAlignment a = new SAMAlignment(line, tSize, false);
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
                } else {
                    System.out.println("Line not recognised: "+line);
                }

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
    
    public SAMAlignment getAlignment(int i) {
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
     
    public void filterAlignments()
    {
        // how will this be done?
        return;
    }
    
       
    public void sortAlignments(Comparator<? super Alignment> comparator)
    {
         Collections.sort(alignments, comparator);
    }
}