// Alvis
//
// Alignment Diagrams in LaTeX and SVG
//
// Copyright 2018 Richard Leggett, Samuel Martin
// samuel.martin@earlham.ac.uk
// 
// This is free software, supplied without warranty.

package Alvis.AlignmentFiles;

import java.util.Comparator;

/**
 *
 * @author martins
 */
public interface DetailedAlignment extends Comparable, Alignment
{    
    public abstract int     getQueryStart();
    public abstract int     getQueryEnd();
    public abstract int     getQuerySize();
    public abstract String  getQueryName();
    public abstract int     getTargetStart();
    public abstract int     getTargetEnd();
    public abstract boolean isReverseAlignment();
    
    
    public static Comparator<DetailedAlignment> compareByQueryStart = new Comparator<DetailedAlignment>(){
        public int compare(DetailedAlignment alignment1, DetailedAlignment alignment2) {
            int td = alignment1.getQueryName().compareTo(alignment2.getQueryName());
            if(td != 0)
            {
                return td;
            }
            return alignment1.getQueryStart() - alignment2.getQueryStart();
        }
    };
    
    public static Comparator<DetailedAlignment> compareByQueryAlignmentLength = new Comparator<DetailedAlignment>(){
        public int compare(DetailedAlignment alignment1, DetailedAlignment alignment2) {
            int queryLength1 = Math.abs(alignment1.getQueryEnd() - alignment1.getQueryStart());
            int queryLength2 = Math.abs(alignment2.getQueryEnd() - alignment2.getQueryStart());
            return queryLength2 - queryLength1;
        }
    };
    
    public static Comparator<DetailedAlignment> compareByQueryName = new Comparator<DetailedAlignment>(){
        public int compare(DetailedAlignment alignment1, DetailedAlignment alignment2) {
            return alignment1.getQueryName().compareTo(alignment2.getQueryName());
        }
    };         
}
