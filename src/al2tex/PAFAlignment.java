/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package al2tex;

import java.util.*;
/**
 *
 * @author martins
 */
public class PAFAlignment implements Alignment, Comparable 
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
    public String getRefName() { return rName; };
    public int getQueryStartPos() { return qStart; }
    public int getQueryEndPos() { return qEnd; }
    public int getRefStartPos() { return rStart; }
    public int getRefEndPos() { return rEnd; }
    public int getQueryContigLength() { return qLength; }
    public int getQueryAlignmentLength() { return qEnd - qStart; }
    public int getRefContigLength() {return rLength; }
    public int getRefAlignmentLength() { return rEnd - rStart; }
    public boolean isReverseAlignment() { return isReverseAlignment; }
    public String getTargetName() { return this.getRefName(); };
    public int getTargetSize() { return rLength; }
    public int getBlockCount() { return 1; };
    public int getBlockTargetStart(int i) { return rStart; };
    public int getBlockSize(int i) { return blockLength; };    
    
    @Override
    public int compareTo(Object o) 
    {
        int td = rName.compareTo(((PAFAlignment)o).getRefName());
        
        if (td != 0) {
            return td;
        }
        
        return getQueryStartPos() - ((PAFAlignment)o).getQueryStartPos();
    }   
    
    // compare by query start position
    public static Comparator<PAFAlignment> compareByQueryStart = new Comparator<PAFAlignment>(){
        public int compare(PAFAlignment alignment1, PAFAlignment alignment2) {
            int td = alignment1.getQueryName().compareTo(alignment2.getQueryName());
            if(td != 0)
            {
                return td;
            }
            return alignment1.getQueryStartPos() - alignment2.getQueryStartPos();
        }
    };
}