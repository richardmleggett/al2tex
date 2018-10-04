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
import java.io.*;

public class TexFileWriter extends TikzDrawer{
    private final static int MAX_OVERHANG = 200;
    private float unit;
    private int targetWidth = 1800;                        // Width of reference (target) bar, in mm
    private int targetHeight = 20;
    private int targetSize;
    private int yStep = (int)((float)targetHeight * 1.5); // Allow for gap
    private int pictureHeight;                            // Calculated later, based on number of alignments
    private int rowsPerPage = (1400 - (2 * yStep)) / yStep;
    private int y;
    
    public TexFileWriter(String f) {
        super(f);
    }
     
    public void newTarget(int s, int h) {
        targetSize = s;
        int targetHits = h;

        // Calculate unit length
        unit = ((float)targetWidth / (float)targetSize);                

        // Calculate picture height
        if (targetHits > rowsPerPage) {
            pictureHeight = (rowsPerPage + 2) * yStep;
        } else {
            pictureHeight = (targetHits + 2) * yStep;
        }        

        y = pictureHeight;
        
    }

    public void outputAlignmentLine(int from, int to) {
        float x1 = (float)from * unit;
        float x2 = (float)to * unit;
        float y1 = (float)y - ((float)targetHeight / 2);
        
        if (from > to) {
            System.out.println("Something went wrong - from > to!");
            System.exit(-1);
        }

        
        if (x1 < -MAX_OVERHANG) {
            x1 = -MAX_OVERHANG;
            try {
                bw.write("\\node [anchor=east] at ("+(-MAX_OVERHANG-10)+","+y1+") {"+"+"+(-from)+"};"); bw.newLine();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
        
        if (x2 > (targetWidth+MAX_OVERHANG)) {
            x2 = targetWidth+MAX_OVERHANG;
            try {
                bw.write("\\node [anchor=west] at ("+(targetWidth + MAX_OVERHANG + 10)+","+y1+") {"+"+"+(to-targetSize)+"};"); bw.newLine();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
                
        try {
            bw.write("\\draw("+x1+", "+y1+") -- ("+x2+","+y1+");"); bw.newLine();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    public void outputAlignmentBox(int from, int to) {
        float x1 = (float)from * unit;
        float x2 = (float)to * unit;
        float y1 = y;
        float y2 = y - (float)targetHeight;

        if (from > to) {
            System.out.println("Something went wrong - from > to!");
            System.exit(-1);
        }

        try {
            bw.write("\\draw [fill=white] ("+x1+","+y1+") rectangle ("+x2+","+y2+");"); bw.newLine();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    public void nextAlignment() {
        y-=yStep;
    }
    
    public void outputContigLabel(String label) {
        label = label.replace("_", "\\_");
        try {
            bw.write("\\node [anchor=west] at ("+(targetWidth + 5)+","+(y - (targetHeight / 2))+") {"+label+"};"); bw.newLine();  
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    public void outputGapBetweenPictures() {
        try {
            bw.write("\\linebreak"); bw.newLine();
            bw.write("\\vspace{1cm}"); bw.newLine();
            bw.write("\\newpage"); bw.newLine();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    public void drawDividers(int n) {
        try {
            for (int j=0; j<=n; j++) {
                int num = (targetSize * j) / n;
                int pos = (int)((float)num * unit);
                bw.write("\\draw [dashed] ("+pos+", 0) -- ("+pos+","+(pictureHeight - yStep)+");"); bw.newLine();
                bw.write("\\node at ("+pos+","+(y-(targetHeight / 2))+") {"+num+"};"); bw.newLine();
            }
            y-=yStep;
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    public void drawTargetBar(String name) {
        //name = name.replace("_", "-");
        try {
            bw.write("\\color{red}"); bw.newLine();
            bw.write("\\draw [fill] (0,"+y+") rectangle ("+targetWidth+","+(y - targetHeight)+");"); bw.newLine();
            bw.write("\\node [anchor=west] at ("+(targetWidth + 5)+","+(y - (targetHeight / 2))+") {"+name+"};"); bw.newLine();
            bw.write("\\color{blue}"); bw.newLine();
            y-=yStep;
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    public void nextLine() {
        y-=yStep;
    }
    
    public int getRowsPerPage() {
        return rowsPerPage;
    }
}
