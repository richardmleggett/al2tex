package Alvis.AlignmentFiles;

import java.util.Comparator;

public interface AlignmentFile {
    abstract int getNumberOfAlignments();
    abstract Alignment getAlignment(int i);
    abstract void sort(Comparator<? super Alignment> comparator);
    
    public static Comparator<Alignment> compareByTargetName = new Comparator<Alignment>(){
        public int compare(Alignment alignment1, Alignment alignment2) {
            return alignment1.getTargetName().compareTo(alignment2.getTargetName());
        }
    };
}
