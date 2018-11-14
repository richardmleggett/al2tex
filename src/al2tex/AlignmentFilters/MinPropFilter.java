/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package al2tex.AlignmentFilters;

import al2tex.AlignmentFIles.Alignment;
import al2tex.AlignmentFIles.DetailedAlignment;
import java.util.ArrayList;

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
        
        return filteredAlignments;
    }   
}
