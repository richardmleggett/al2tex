package Alvis.AlignmentFiles;

public interface Alignment {
    public abstract String getTargetName();
    public int getTargetSize();
    public abstract int getBlockCount();
    public abstract int getBlockTargetStart(int i);
    public abstract int getBlockSize(int i);
}
