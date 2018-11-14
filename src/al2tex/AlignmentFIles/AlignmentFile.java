package al2tex.AlignmentFIles;

import al2tex.AlignmentFilters.AlignmentFilter;
import java.util.ArrayList;
import java.util.Comparator;

public interface AlignmentFile {
    abstract int getNumberOfAlignments();
    abstract Alignment getAlignment(int i);
    abstract void sortAlignments(Comparator<? super Alignment> comparator);
    
    public static Comparator<Alignment> compareByTargetName = new Comparator<Alignment>(){
        public int compare(Alignment alignment1, Alignment alignment2) {
            return alignment1.getTargetName().compareTo(alignment2.getTargetName());
        }
    };
}
