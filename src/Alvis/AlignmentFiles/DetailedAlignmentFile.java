// Alvis
//
// Alignment Diagrams in LaTeX and SVG
//
// Copyright 2018 Richard Leggett, Samuel Martin
// samuel.martin@earlham.ac.uk
// 
// This is free software, supplied without warranty.

package Alvis.AlignmentFiles;

import java.util.Hashtable;
import Alvis.AlignmentFilters.AlignmentFilter;

/**
 *
 * @author martins
 */
public interface DetailedAlignmentFile extends AlignmentFile {
    abstract DetailedAlignment getAlignment(int i);
    abstract Hashtable getTargetHits();
    abstract int getTargetHitCount(String target);
    abstract void filterAlignments(AlignmentFilter filter);
}
