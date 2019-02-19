// Alvis
//
// Alignment Diagrams in LaTeX and SVG
//
// Copyright 2018 Richard Leggett, Samuel Martin
// samuel.martin@earlham.ac.uk
// 
// This is free software, supplied without warranty.

package Alvis.AlignmentFiles;

import java.util.*;

public class MummerAlignment implements Comparable, DetailedAlignment {
    public final static int SHOW_COORDS = 1;
    public final static int SHOW_TILING = 2;
    private int referenceStart;
    private int referenceEnd;
    private int queryStart;
    private int queryEnd;
    private int referenceLength;
    private int queryLength;
    private double pcIdentity;
    private String referenceId;
    private String queryId;
    private boolean reverseAlignment = false;
    
    public MummerAlignment(String line, int type) {
        if (type == SHOW_COORDS) {
            parseShowCoordsAlignment(line);
        } else if (type == SHOW_TILING) {
            parseShowTilingAlignment(line);
        } else {
            System.out.println("Error in type. Shouldn't get here!");
            System.exit(0);
        }
        
        if (queryStart > queryEnd) {
            reverseAlignment = true;
            int temp = queryStart;
            queryStart = queryEnd;
            queryEnd = temp;
        }    
    }
    
    private void parseShowCoordsAlignment(String line) {
        String[] fields = line.split("\\t");
        
        if (fields.length != 21) {
            System.out.println("Error in input Nucmer file - did you create with -B option?");
            System.exit(0);
        }
        
        queryId = fields[0];                           //  [0] query sequence ID
        //  [1] date of alignment
        queryLength = Integer.parseInt(fields[2]);     //  [2] length of query sequence
        //  [3] alignment type
        //  [4] reference file
        referenceId = fields[5];                       //  [5] reference sequence ID
        queryStart = Integer.parseInt(fields[6]);      //  [6] start of alignment in the query
        queryEnd = Integer.parseInt(fields[7]);        //  [7] end of alignment in the query
        referenceStart = Integer.parseInt(fields[8]);  //  [8] start of alignment in the reference
        referenceEnd = Integer.parseInt(fields[9]);    //  [9] end of alignment in the reference
        pcIdentity = Double.parseDouble(fields[10]);   // [10] percent identity
        // [11] percent similarity
        // [12] length of alignment in the query
        // [13] 0 for compatibility
        // [14] 0 for compatibility
        // [15] NULL for compatibility
        // [16] 0 for compatibility
        // [17] strand of the query
        referenceLength = Integer.parseInt(fields[18]); // [18] length of the reference sequence
        // [19] 0 for compatibility
        // [20] and 0 for compatibility. 
    }
    
    private void parseShowTilingAlignment(String line) {
        String[] fields = line.split("\\t");
        
        if (fields.length != 13) {
            System.out.println("Error in input tiling file - did you create with -a option?");
            System.exit(0);
        }
        
        referenceStart = Integer.parseInt(fields[0]);
        referenceEnd = Integer.parseInt(fields[1]);
        queryStart = Integer.parseInt(fields[2]);
        queryEnd = Integer.parseInt(fields[3]);
        // [4] Length of alignment region in the reference sequence
        // [5] Length of alignment region in the query sequence
        pcIdentity = Double.parseDouble(fields[6]);
        referenceLength = Integer.parseInt(fields[7]);
        queryLength = Integer.parseInt(fields[8]);
        // [9] % alignment coverage in the reference sequence
        // [10] % alignment coverage in the query sequence
        referenceId = fields[11];
        queryId = fields[12];
    }
        
    public double getPcIdentity() { return pcIdentity; }

    // from DetailedAlignment interface
    public int getQueryStart() { return queryStart; }
    public int getQueryEnd() { return queryEnd; } 
    public int getQuerySize() { return queryLength; }
    public String getQueryName() { return queryId; }
    public int getTargetStart() { return referenceStart; }
    public int getTargetEnd() { return referenceEnd; }
    public int getTargetSize() { return referenceLength; }
    public String getTargetName() { return referenceId; }
    public boolean isReverseAlignment() { return reverseAlignment; }

    public int getBlockCount() { return 1; };  
    public int getBlockSize(int i) {
        if (i != 0) {
            System.out.println("Something went wrong: block number for MummerAlignment should always be 0!\n");
            System.exit(-1);
        }
        return referenceEnd - referenceStart;
    }

    public int getBlockTargetStart(int i) {
        if (i != 0) {
            System.out.println("Something went wrong: block number for MummerAlignment should always be 0!\n");
            System.exit(-1);
        }
        return referenceStart;
    }
    
    @Override
    public int compareTo(Object o) {
        int td = referenceId.compareTo(((MummerAlignment)o).getTargetName());
        
        if (td != 0) {
            return td;
        }
        
        return referenceStart - ((MummerAlignment)o).getTargetStart();
    }        
}
