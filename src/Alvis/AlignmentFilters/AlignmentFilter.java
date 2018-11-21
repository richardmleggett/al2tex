// Alvis
//
// Alignment Diagrams in LaTeX and SVG
//
// Copyright 2018 Richard Leggett, Samuel Martin
// samuel.martin@earlham.ac.uk
// 
// This is free software, supplied without warranty.

package Alvis.AlignmentFilters;

import Alvis.AlignmentFiles.Alignment;
import Alvis.AlignmentFiles.DetailedAlignment;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author martins
 */

public interface AlignmentFilter {
    public abstract ArrayList<DetailedAlignment> filterAlignments(ArrayList<DetailedAlignment> alignments);  
}

