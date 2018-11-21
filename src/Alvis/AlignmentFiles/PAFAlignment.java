// Alvis
//
// Alignment Diagrams in LaTeX and SVG
//
// Copyright 2018 Richard Leggett, Samuel Martin
// samuel.martin@earlham.ac.uk
// 
// This is free software, supplied without warranty.

package Alvis.AlignmentFiles;

/**
 *
 * @author martins
 */
public class PAFAlignment implements DetailedAlignment, Comparable 
{
    private String qName;
    private int qLength;
    private int qStart;
    private int qEnd;
    private String rName;
    private int rLength;
    private int rStart;
    private int rEnd;
    private int blockLength;
    private int quality;
    private boolean isReverseAlignment;

    
    public PAFAlignment(String line) 
    {
        String[] fields = line.split("\\t");

        qName               = fields[0];
        qLength             = Integer.parseInt(fields[1]);
        qStart              = Integer.parseInt(fields[2]);
        qEnd                = Integer.parseInt(fields[3]);
        isReverseAlignment  = fields[4].equals("-") ? true : false;
        rName               = fields[5];
        rLength             = Integer.parseInt(fields[6]);
        rStart              = Integer.parseInt(fields[7]);
        rEnd                = Integer.parseInt(fields[8]);
        blockLength         = Integer.parseInt(fields[10]);
        quality             = Integer.parseInt(fields[11]);

        rName=rName.replace("|", "");
        rName=rName.replace(".", "");
        rName=rName.replace("_", "");        
        
    }
    
    public String getQueryName() { return qName; };
    //public String getRefName() { return rName; };
    public int getQueryStart() { return qStart; }
    public int getQueryEnd() { return qEnd; }
    public int getTargetStart() { return rStart; }
    public int getTargetEnd() { return rEnd; }
    public int getQuerySize() { return qLength; }
    public int getRefContigLength() {return rLength; }
    public boolean isReverseAlignment() { return isReverseAlignment; }
    public String getTargetName() { return rName; };
    public int getTargetSize() { return rLength; }
    public int getBlockCount() { return 1; };
    public int getBlockTargetStart(int i) { return rStart; };
    public int getBlockSize(int i) { return rEnd - rStart; };    
    
    @Override
    public int compareTo(Object o) 
    {
        int td = rName.compareTo(((PAFAlignment)o).getTargetName());
        
        if (td != 0) {
            return td;
        }
        
        return getQueryStart() - ((PAFAlignment)o).getQueryStart();
    }   
  
}
