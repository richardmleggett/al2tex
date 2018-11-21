// Al2Tex
//
// Alignment Diagrams in LaTeX
//
// Copyright 2012 Richard Leggett
// richard.leggett@tgac.ac.uk
// 
// This is free software, supplied without warranty.

package Alvis.AlignmentFiles;

import java.util.*;

public class PSLAlignment implements Comparable, DetailedAlignment {
    private int matches;
    private int misMatches;
    private int repMatches;
    private int nCount;    
    private int qNumInsert;
    private int qBaseInsert;
    private int tNumInsert;
    private int tBaseInsert;
    private String strand;
    private String qName;
    private int qSize;
    private int qStart;
    private int qEnd;
    private String tName;
    private int tSize;
    private int tStart;
    private int tEnd;
    private int blockCount;
    private int[] blockSizes;
    private int[] qStarts;
    private int[] tStarts;
    
    public PSLAlignment (String line) {
        String[] fields = line.split("\\t");
        
        matches = Integer.parseInt(fields[0]);
        misMatches = Integer.parseInt(fields[1]);
        repMatches = Integer.parseInt(fields[2]);
        nCount = Integer.parseInt(fields[3]);
        qNumInsert = Integer.parseInt(fields[4]);
        qBaseInsert = Integer.parseInt(fields[5]);
        tNumInsert = Integer.parseInt(fields[6]);
        tBaseInsert = Integer.parseInt(fields[7]);
        strand = fields[8];
        qName = fields[9];
        qSize = Integer.parseInt(fields[10]);
        qStart = Integer.parseInt(fields[11]);
        qEnd = Integer.parseInt(fields[12]);
        tName = fields[13];
        tSize = Integer.parseInt(fields[14]);
        tStart = Integer.parseInt(fields[15]);
        tEnd = Integer.parseInt(fields[16]);
        blockCount = Integer.parseInt(fields[17]);
        
        blockSizes = new int[blockCount];
        qStarts = new int[blockCount];
        tStarts = new int[blockCount];
        
        tName=tName.replace("|", "");
        tName=tName.replace(".", "");
        tName=tName.replace("_", "");
        
        String[] bs = fields[18].split(",");
        String[] qs = fields[19].split(",");
        String[] ts = fields[20].split(",");
        
        for (int i=0; i<blockCount; i++) {
            blockSizes[i] = Integer.parseInt(bs[i]);
            qStarts[i] = Integer.parseInt(qs[i]);
            tStarts[i] = Integer.parseInt(ts[i]);
        }
    }
    
    public int getMatches() { return matches; }
    public int getMisMatches() { return misMatches; }
    public int getNCount() { return nCount; }
    public int getQueryNumInsert() { return qNumInsert; }
    public int getQueryBaseInsert() { return qBaseInsert; }
    public int getTargetNumInsert() { return tNumInsert; }
    public int getTargetBaseInsert() { return tBaseInsert; }
    public String getStrand() { return strand; }
    public String getQueryName() { return qName; }
    public int getQuerySize() { return qSize; }
    public int getQueryStart() { return qStart; }
    public int getQueryEnd() { return qEnd; }
    public String getTargetName() { return tName; }
    public int getTargetSize() { return tSize; }
    public int getTargetStart() { return tStart; }
    public int getTargetEnd() { return tEnd; }
    public int getBlockCount() { return blockCount; }
    public int getBlockSize(int i) { return blockSizes[i]; }
    public int getBlockQueryStart(int i) { return qStarts[i]; }
    public int getBlockTargetStart(int i) { return tStarts[i]; }
    public boolean isReverseAlignment() { return strand.equals("-"); }
    
    @Override
    public int compareTo(Object o) {
        int td = tName.compareTo(((PSLAlignment)o).getTargetName());
        
        if (td != 0) {
            return td;
        }
        
        return tStart - ((PSLAlignment)o).getTargetStart();
    }
}
