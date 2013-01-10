package al2tex;

public interface AlignmentFile {
    abstract int getNumberOfAlignments();
    abstract Alignment getAlignment(int i);
}
