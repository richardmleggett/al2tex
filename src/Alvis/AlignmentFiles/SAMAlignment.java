// Alvis
//
// Alignment Diagrams in LaTeX and SVG
//
// Copyright 2018 Richard Leggett, Samuel Martin
// samuel.martin@earlham.ac.uk
// 
// This is free software, supplied without warranty.

package Alvis.AlignmentFiles;

public class SAMAlignment implements Alignment, Comparable {
    private String m_qName;
    private int m_flag;
    private String m_rName;
    private int m_pos;
    private int m_mapq;
    private int m_tSize;
    private int m_aLength;
    
    public SAMAlignment( int tSize, String qName, int flag, String rName, int pos, int mapq, int length) 
    {

        m_tSize = tSize;
        m_qName = qName;
        m_flag = flag;
        m_rName = rName;
        m_pos = pos;
        m_mapq = mapq;       
        m_aLength = length;

        rName=rName.replace("|", "");
        rName=rName.replace(".", "");
        rName=rName.replace("_", "");        
        
    }
    
    public String getQueryName() { return m_qName; };
    public int getFlags() { return m_flag; };
    public int getPos() { return m_pos; };
    public int getMapQ() { return m_mapq; };             
    public int getLength() { return m_aLength; };
    public String getTargetName() { return m_rName; };
    public int getTargetSize() { return m_tSize; }
    public int getBlockCount() { return 1; };
    public int getBlockTargetStart(int i) { return this.getPos(); };
    public int getBlockSize(int i) { return this.getLength(); };    
    
    @Override
    public int compareTo(Object o) {
        int td = m_rName.compareTo(((SAMAlignment)o).getTargetName());
        
        if (td != 0) {
            return td;
        }
        
        return m_pos - ((SAMAlignment)o).getPos();
    }
}
