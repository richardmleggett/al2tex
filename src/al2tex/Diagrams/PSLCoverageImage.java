// AlDiTex
//
// Alignment Diagrams in LaTeX
//
// Copyright 2012 Richard Leggett
// richard.leggett@tgac.ac.uk
// 
// This is free software, supplied without warranty.

package al2tex.Diagrams;

import al2tex.AlignmentFiles.DetailedAlignment;
import al2tex.DiagramOptions;
import java.io.*;
import java.util.*;
import java.awt.image.*;
import javax.swing.*;
import java.awt.*;
import javax.imageio.*;

public class PSLCoverageImage {
    private DiagramOptions options;
    private int targetWidth;
    private int targetHeight = 16;
    private int[] coverage;
    private int largestCoverage = 0;
                            
    public PSLCoverageImage(DiagramOptions o, int t) {
        options = o;
        targetWidth = t;
        coverage = new int[targetWidth];                 
    }
        
    public void addAlignment(DetailedAlignment a) {
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
        double multiplier = 1.0;
        int imageWidth = targetWidth;
        
        if (targetWidth > 10000) {
            multiplier = 10000.0/targetWidth;
            System.out.println("Multiplier "+multiplier);
            imageWidth = (int)((double)targetWidth * multiplier);
        }
        
        
        System.out.println("Width "+imageWidth+" Height "+targetHeight);
        BufferedImage bImage = new BufferedImage(imageWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        
        Graphics g = bImage.getGraphics();
        g.setColor(new Color(200, 200, 200));
        g.fillRect(0, 0, targetWidth, targetHeight);

        for (int x=0; x<targetWidth; x++) {
            for (int y=0; y<targetHeight; y++) {
                bImage.setRGB((int)((double)x*multiplier), y, heatMap.getRGBColour(coverage[x]));
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
}
