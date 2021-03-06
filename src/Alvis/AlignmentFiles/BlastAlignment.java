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
public class BlastAlignment implements DetailedAlignment, Comparable
{
    private String qName;
    private int qLength;
    private int qStart;
    private int qEnd;
    private String rName;
    private int rLength;
    private int rStart;
    private int rEnd;
    private boolean isReverseAlignment;

    
    public BlastAlignment(String line, String formatString) 
    {
        formatString = formatString.toLowerCase();
        String[] format = formatString.split(" ");
        String[] fields = line.split("\\t");
        try
        {
            for(int i = 1; i < format.length; i++)
            {
                switch (format[i]) 
                {
                    case "qseqid":
                        qName = fields[i-1];
                        break;
                    case "qlen":
                        qLength = Integer.parseInt(fields[i-1]);
                        break;
                    case "qstart":
                        qStart = Integer.parseInt(fields[i-1]);
                        break;
                    case "qend":
                        qEnd = Integer.parseInt(fields[i-1]);
                        break;
                    case "sseqid":
                        rName = fields[i-1];
                        break;
                    case "slen":
                        rLength = Integer.parseInt(fields[i-1]);
                        break;
                    case "sstart":
                        rStart = Integer.parseInt(fields[i-1]);
                        break;
                    case "send":
                        rEnd = Integer.parseInt(fields[i-1]);
                        break;
                    default:
                        break;
                }
            }
        }
        catch(NumberFormatException e)
        {  
            System.out.println("Error: This file does not appear to be in the format specified. Terminating...");
            System.exit(0);   
        }
        
        if(rStart < rEnd && qStart > qEnd)
        {
            isReverseAlignment  = true;
            int temp = qStart;
            qStart = qEnd;
            qEnd = temp;              
        }
        else if (rStart> rEnd && qStart < qEnd)
        {
            isReverseAlignment  = true;
            int temp = rStart;
            rStart = rEnd;
            rEnd = temp;
        }
        else
        {
            isReverseAlignment  = false;
        }
    }
    
    public String   getQueryName() { return qName; };
    public int      getQueryStart() { return qStart; }
    public int      getQueryEnd() { return qEnd; }
    public int      getTargetStart() { return rStart; }
    public int      getTargetEnd() { return rEnd; }
    public int      getQuerySize() { return qLength; }
    public int      getRefContigLength() {return rLength; }
    public boolean  isReverseAlignment() { return isReverseAlignment; }
    public String   getTargetName() { return rName; };
    public int      getTargetSize() { return rLength; }
    public int      getBlockCount() { return 1; };
    public int      getBlockTargetStart(int i) { return rStart; };
    public int      getBlockSize(int i) { return rEnd - rStart; };    
    
    @Override
    public int compareTo(Object o) 
    {
        int td = rName.compareTo(((BlastAlignment)o).getTargetName());
        
        if (td != 0) {
            return td;
        }
        
        return getQueryStart() - ((BlastAlignment)o).getQueryStart();
    }   
}
