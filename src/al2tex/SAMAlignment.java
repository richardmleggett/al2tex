// Al2Tex
//
// Alignment Diagrams in LaTeX
//
// Copyright 2012 Richard Leggett
// richard.leggett@tgac.ac.uk
// 
// This is free software, supplied without warranty.

package al2tex;

import java.util.*;

public class SAMAlignment implements Alignment, Comparable {
    private String qName;
    private int flag;
    private String rName;
    private int pos;
    private int mapq;
    private String cigar;
    private String rNext;
    private int pNext;
    private int tLen;
    private String seq;
    private String qual;
    private int tSize;
    private int aLength;
    
    public SAMAlignment(String line, int size, boolean fStoreSeq) {
        String[] fields = line.split("\\t");

        tSize = size;
        qName = fields[0];
        flag = Integer.parseInt(fields[1]);
        rName = fields[2];
        pos = Integer.parseInt(fields[3]);
        mapq = Integer.parseInt(fields[4]);
        cigar = fields[5];
        rNext = fields[6];
        pNext = Integer.parseInt(fields[7]);        
        tLen = Integer.parseInt(fields[8]);
        if (fStoreSeq) {
            seq = fields[9];
            qual = fields[10];
        }
        aLength = fields[9].length();

        rName=rName.replace("|", "");
        rName=rName.replace(".", "");
        rName=rName.replace("_", "");        
        
        String expectedCigar = aLength+"M";        
        if (!cigar.contains(expectedCigar)) {
            System.out.println("Warning: CIGAR string "+cigar+" doesn't contain expected "+expectedCigar);
        }
    }
    
    public String getQueryName() { return qName; };
    public int getFlags() { return flag; };
    public String getRef() { return rName; };
    public int getPos() { return pos; };
    public int getMapQ() { return mapq; };
    public String getCigar() { return cigar; };
    public String getRefNext() { return rNext; };
    public int getPosNext() { return pNext; };        
    public int getTLen() { return tLen; };
    public String getSeq() { return seq; };
    public String getQual() { return qual; };        
    public int getLength() { return aLength; };
    public String getTargetName() { return this.getRef(); };
    public int getTargetSize() { return tSize; }
    public int getBlockCount() { return 1; };
    public int getBlockTargetStart(int i) { return this.getPos(); };
    public int getBlockSize(int i) { return this.getLength(); };    
    
    @Override
    public int compareTo(Object o) {
        int td = rName.compareTo(((SAMAlignment)o).getRef());
        
        if (td != 0) {
            return td;
        }
        
        return pos - ((SAMAlignment)o).getPos();
    }
}
