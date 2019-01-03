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
        double min_pc = m_minProp*100;
        System.out.println("Filtering out alignments that are less than " + Double.toString(min_pc) + "% of the target read.");
        ArrayList<DetailedAlignment> filteredAlignments = new ArrayList();
        for(DetailedAlignment alignment : alignments)
        {
            int queryLength = Math.abs(alignment.getQueryEnd() - alignment.getQueryStart());
            double prop = (double)queryLength / alignment.getTargetSize();
            if(prop > m_minProp)
            {
                filteredAlignments.add(alignment);
            }
        }
        return filteredAlignments;
    }   
}
