// AlDiTex
//
// Alignment Diagrams in LaTeX
//
// Copyright 2012 Richard Leggett
// richard.leggett@tgac.ac.uk
// 
// This is free software, supplied without warranty.

package al2tex;

import java.util.*;
import java.io.*;
import java.lang.*;

public class MummerFile {
    private ArrayList<MummerAlignment> alignments = new ArrayList();
    private Hashtable<String,Integer> targetHits = new Hashtable();
    
    public MummerFile(String filename, int type) {
        String line;
                
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            
            // Skip header
            //do {
            //    line = br.readLine();
            //}
            //while ((line != null) && (!line.startsWith("=========")));

            // Now read entries
            line = br.readLine();
            while (line != null) {
                MummerAlignment a = new MummerAlignment(line, type);
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
    
    public MummerAlignment getAlignment(int i) {
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
}
