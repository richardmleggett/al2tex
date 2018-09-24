/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package al2tex;

import java.util.*;
import java.io.*;
import java.lang.*;
/**
 *
 * @author martins
 */
public class PAFFile implements DetailedAlignmentFile 
{
    private ArrayList<PAFAlignment> alignments = new ArrayList();
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

                if (fields.length >= 11) 
                {                
                    PAFAlignment a = new PAFAlignment(line);
                    if (a != null) 
                    {
                        // add the alignment to the list
                        alignments.add(a);
                        
                        // increment hit count for alignments target
                        Integer count = targetHits.get(a.getTargetName());
                        if (count == null) 
                        {
                            count = new Integer(1);
                        }
                        else 
                        {
                            count = new Integer(count.intValue() + 1);
                        }
                        targetHits.put(a.getTargetName(), count);
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
    
    public int getNumberOfAlignments() 
    {
        return alignments.size();
    }
    
    public PAFAlignment getAlignment(int i) 
    {
        return alignments.get(i);
    }
    
    public void sortAlignmentsByTargetName() {
        Collections.sort(alignments, compareByTargetName);
    }
    
    public Hashtable getTargetHits() {
        return targetHits;
    }
    
    public int getTargetHitCount(String target) {
        Integer a = targetHits.get(target);      
        if (a == null) 
        {
            System.out.println("Something went wrong - unknown target.");
            System.exit(-1);
        }
        return a.intValue();
    }  
    
}
