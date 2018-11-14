/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package al2tex;

import java.util.Hashtable;
import al2tex.AlignmentFilters.AlignmentFilter;

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
