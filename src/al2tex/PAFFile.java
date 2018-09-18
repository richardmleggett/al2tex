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
    
    public PAFFile(String filename) 
    {
        String line;
                
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            
            line = br.readLine();
            while (line != null) {
                String[] fields = line.split("\\t");

                if (fields.length >= 11) {                
                    PAFAlignment a = new PAFAlignment(line);
                    if (a != null) {
                        alignments.add(a);
                    }
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
    
    public int getNumberOfAlignments() 
    {
        return alignments.size();
    }
    
    public PAFAlignment getAlignment(int i) 
    {
        return alignments.get(i);
    }
    
}
