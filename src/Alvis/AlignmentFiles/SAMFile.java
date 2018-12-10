// Alvis
//
// Alignment Diagrams in LaTeX and SVG
//
// Copyright 2018 Richard Leggett, Samuel Martin
// samuel.martin@earlham.ac.uk
// 
// This is free software, supplied without warranty.

package Alvis.AlignmentFiles;

import java.util.*;
import java.io.*;
import java.lang.*;

public class SAMFile implements AlignmentFile {
    private ArrayList<SAMAlignment> alignments = new ArrayList();
    private HashMap<String,Integer> targetHits = new HashMap();
    private HashMap<String,Integer> targetSizes = new HashMap();
    
    public SAMFile(String filename, String tSizes) 
    {
        try
        {        
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line = br.readLine();
            while (line != null && line.startsWith("@")) 
            {
                // this is a header line. Look for target lengths.
                if(line.subSequence(1, 3).equals("SQ"))
                {
                    String[] fields = line.split("\\t");
                    String targetName = "";
                    int targetSize = -1;
                    for(int i = 0; i < fields.length; i++)
                    {
                        String field = fields[i].trim();
                        if(field.contains("SN:"))
                        {
                            targetName = field.substring(3);
                        }
                        else if(field.contains("LN:"))
                        {
                            targetSize = Integer.parseInt(field.substring(3));
                        }
                    }
                    assert(!targetName.isEmpty());
                    assert(targetSize != -1);
                    targetSizes.put(targetName, targetSize);
                    System.out.println("Found target: " + targetName + ", " + targetSize);
                }
                line = br.readLine();
            }
            
            String[] sizes = tSizes.split(" ");
            System.out.println("Looking for targets with sizes:");
            for(int i = 0; i < sizes.length; i+=2)
            {
                String targetName = sizes[i];
                int targetSize = Integer.parseInt(sizes[i+1]);
                if(targetSizes.get(targetName) == null)
                {
                    targetSizes.put(targetName, targetSize);
                    System.out.println("Found target:" + targetName + ", " + Integer.toString(targetSize));
                }
            }
            
            if(targetSizes.isEmpty())
            {
                System.out.println("Could not find target sizes in SAM file header section. Please specify -tsizes");
                return;
            }
                     
            while (line != null) 
            {
                String[] fields = line.split("\\t");
                if (fields.length >= 11) 
                {
                    String target = fields[2];
                    if(targetSizes.containsKey(target))
                    {
                        int size = targetSizes.get(target);
                        SAMAlignment a = new SAMAlignment(line, size, false);
                        alignments.add(a);

                        Integer count = targetHits.get(a.getTargetName());

                        if (count == null) 
                        {
                            count = 1;
                        } 
                        else 
                        {
                            count += 1;
                        }
                        targetHits.put(a.getTargetName(), count);
                    }
                    else
                    {
                        System.out.println("Did not recognise target " + target + ".");
                    }
                } 
                else 
                {
                    System.out.println("Line not recognised: "+line);
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
    
    public int getNumberOfAlignments() {
        return alignments.size();
    }
    
    public SAMAlignment getAlignment(int i) {
        return alignments.get(i);
    }
    
    public HashMap getTargetHits() {
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
    
       
    public void sort(Comparator<? super Alignment> comparator)
    {
         Collections.sort(alignments, comparator);
    }
}