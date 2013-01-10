// Al2Tex
//
// Alignment Diagrams in LaTeX
//
// Copyright 2012 Richard Leggett
// richard.leggett@tgac.ac.uk
// 
// This is free software, supplied without warranty.

package al2tex;

import java.io.*;
import java.util.*;
import java.awt.image.*;
import javax.swing.*;
import java.awt.*;
import javax.imageio.*;

public class CoverageMapImage {
    private final static int MAX_ROWS = 400;
    private DiagramOptions options;
    private int targetSize;
    private int nRows;
    private int[] coverage;
    private int largestCoverage = 0;
                            
    public CoverageMapImage(DiagramOptions o, int s) {
        options = o;
        targetSize = s;
        coverage = new int[targetSize];                 
    }

    public void addAlignment(Alignment a) {
        
        for (int b=0; b<a.getBlockCount(); b++) {
            int from = a.getBlockTargetStart(b);
            int to = from + a.getBlockSize(b);
            
            for (int i=from; i<to; i++) {
                coverage[i]++;
                
                if (coverage[i] > largestCoverage) {
                    largestCoverage = coverage[i];
                }
            }
        }
    }    
    
    public void saveImageFile(String filename, HeatMapScale heatMap) {
        int imageWidth = targetSize > 4000 ? 4000:targetSize;
        nRows = (targetSize / imageWidth) > MAX_ROWS ? MAX_ROWS:(targetSize/imageWidth);
        int imageHeight = nRows * (options.getRowHeight() + options.getRowSpacer());
        int pixels = nRows*imageWidth;
        double multiplier = (double)pixels / (double)targetSize;
        
        System.out.println("Multiplier "+multiplier);
                
        System.out.println("Width "+imageWidth+" Height " + imageHeight);
        BufferedImage bImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        
        Graphics g=bImage.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, imageWidth, imageHeight);
        //g.setColor(Color.BLACK);
        g.setColor(new Color(200, 200, 200));
        
        for (int y=0; y<nRows; y++) {            
            g.fillRect(0, y*(options.getRowHeight()+options.getRowSpacer()), imageWidth, options.getRowHeight());
        }
        
        for (int p=0; p<targetSize; p++) {
            if (coverage[p] > 0) {
                int offset = (int)((double)p*multiplier);
                int yo = offset / imageWidth;
                int x = offset % imageWidth;
                
                yo = yo * (options.getRowHeight() + options.getRowSpacer());
                
                for (int y=0; y<options.getRowHeight(); y++) {
                    bImage.setRGB(x, yo+y, heatMap.getRGBColour(coverage[p]));
                }
            }
        }        
        
        try {
            ImageIO.write(bImage, "PNG", new File(filename));
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }    
    
    public int getLargestCoverage() {
        return largestCoverage;
    }    
    
    public int getNumberOfRows() {
        return nRows;
    }
}
