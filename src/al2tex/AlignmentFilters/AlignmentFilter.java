/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package al2tex.AlignmentFilters;

import al2tex.Alignment;
import al2tex.DetailedAlignment;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author martins
 */

public interface AlignmentFilter {
    public abstract ArrayList<DetailedAlignment> filterAlignments(ArrayList<DetailedAlignment> alignments);  
}

