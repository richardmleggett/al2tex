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
import java.util.ArrayList;
import java.io.*;

/**
 *
 * @author martins
 */
public class MinPropFilter implements AlignmentFilter
{
    private double m_minProp;
    public MinPropFilter(double min_prop)
    {
        m_minProp = min_prop;
    }
    
    public ArrayList<DetailedAlignment> filterAlignments(ArrayList<DetailedAlignment> alignments)
    {
        ArrayList<DetailedAlignment> filteredAlignments = new ArrayList();
        for(DetailedAlignment alignment : alignments)
        {
            int queryLength = Math.abs(alignment.getQueryEnd() - alignment.getQueryStart());
            float prop = (float)queryLength / alignment.getTargetSize();
            if(prop > m_minProp)
            {
                filteredAlignments.add(alignment);
            }
        }
        
//        try 
//        {
//            PrintWriter writer = new PrintWriter("filtered_alignments.txt", "UTF-8");
//            for(DetailedAlignment a : filteredAlignments)
//            {
//                writer.println( a.getQueryName() + "\t" + 
//                                Integer.toString(a.getQuerySize()) + "\t" + 
//                                Integer.toString(a.getQueryStart()) + "\t" +
//                                Integer.toString(a.getQueryEnd()) + "\t"+
//                                a.getTargetName() + "\t" + 
//                                Integer.toString(a.getTargetSize()) + "\t" + 
//                                Integer.toString(a.getTargetStart()) + "\t" +
//                                Integer.toString(a.getTargetEnd()));
//            }
//            writer.close();
//        }
//        catch(IOException e)
//        {
//            
//        }
            
        return filteredAlignments;
    }   
}
