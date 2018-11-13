/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package al2tex;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author martins
 */
public class AlignmentFilter 
{
    static private final float EPSILON_MAX = 0.1f;
    static public ArrayList<DetailedAlignment> filterAlignments(ArrayList<DetailedAlignment> alignments)
    {
        //first, sort the alignments by query name
        Collections.sort(alignments, DetailedAlignment.compareByQueryName);
        ArrayList<DetailedAlignment> filteredAlignments = new ArrayList();
        
        // group alignments into arrays by query name
        int i = 0;
        while(i < alignments.size())
        {
            String lastQueryName = alignments.get(i).getQueryName();
            ArrayList<DetailedAlignment> alignmentsForQuery = new ArrayList();
            alignmentsForQuery.add(alignments.get(i));
            i++;
            while(i < alignments.size() && alignments.get(i).getQueryName().equals(lastQueryName))
            {
                alignmentsForQuery.add(alignments.get(i));
                i++;
            }
        
            // identify the "best" alignments. Probably longest
            filteredAlignments.addAll(findBestAlignmentsForQuery(alignmentsForQuery));
        }
        return filteredAlignments;
    }
    
    static private ArrayList<DetailedAlignment> findBestAlignmentsForQuery(ArrayList<DetailedAlignment> alignments)
    {
        //find the longest alignment
        Collections.sort(alignments, DetailedAlignment.compareByQueryAlignmentLength);
        
        DetailedAlignment bestAlignment = alignments.get(0);
        String targetName = bestAlignment.getTargetName();
        ArrayList<DetailedAlignment> bestAlignments = new ArrayList();
        bestAlignments.add(bestAlignment);
        float bestAlignmentValue = getAlignmentCrossingPos(bestAlignment);
        
        // compare the longest alignment to the rest
        for(int i = 1; i < alignments.size(); i++)
        {
            DetailedAlignment alignment = alignments.get(i);
            // Do the alignments have the same target?
            if(alignment.getTargetName().equals(targetName))
            {
                // and the same orientation?
                if(alignment.isReverseAlignment() == bestAlignment.isReverseAlignment())
                {
                    // and they don't overlap
                    boolean doesOverlap = false;
                    for(DetailedAlignment alignment2 : bestAlignments)
                    {
                        if(DetailedAlignment.overlapByQuery(alignment, alignment2))
                        {
                            doesOverlap = true;
                            break;
                        }
                    }
                    if(!doesOverlap)
                    {
                        // and they are more or less on the same line (as in a dot plot)
                        float alignmentValue = getAlignmentCrossingPos(alignment);
                        float epsilon = Math.abs(alignmentValue - bestAlignmentValue) / (float)bestAlignment.getQuerySize();
                        if(epsilon < EPSILON_MAX)
                        {
                            bestAlignments.add(alignment);
                        }
                    }
                }
            }
        }
        System.out.println( "Alignments for query: " + bestAlignment.getQueryName() + ", " + 
                            Integer.toString(bestAlignments.size()) + "/" + Integer.toString(alignments.size()));
        for(DetailedAlignment a : bestAlignments)
        {
            printAlignment(a);
        }
        return bestAlignments;
    }
    
    static private float getAlignmentCrossingPos(DetailedAlignment alignment)
    {
        //TODO: what about reverse alignments?
        float gradient = (alignment.getTargetEnd() - alignment.getTargetStart()) / (float)(alignment.getQueryEnd() - alignment.getQueryStart());
        return alignment.getTargetStart() - (gradient * alignment.getQueryStart());
    }
    
    static private void printAlignment(DetailedAlignment alignment)
    {
        System.out.println( alignment.getQueryName() + "\t" + 
                            Integer.toString(alignment.getQuerySize()) + "\t" + 
                            Integer.toString(alignment.getQueryStart()) + "\t" + 
                            Integer.toString(alignment.getQueryEnd()) + "\t" + 
                            alignment.getTargetName() + "\t" + 
                            Integer.toString(alignment.getTargetSize()) + "\t" + 
                            Integer.toString(alignment.getTargetStart()) + "\t" + 
                            Integer.toString(alignment.getTargetEnd())  );
    }
    
    static public ArrayList<DetailedAlignment> basicFilter(ArrayList<DetailedAlignment> alignments)
    {
        ArrayList<DetailedAlignment> filteredAlignments = new ArrayList();
        for(DetailedAlignment alignment : alignments)
        {
            int queryLength = Math.abs(alignment.getQueryEnd() - alignment.getQueryStart());
            float prop = (float)queryLength / alignment.getTargetSize();
            if(prop > 0.005)
            {
                filteredAlignments.add(alignment);
            }
        }
        
        return filteredAlignments;
    }
}
