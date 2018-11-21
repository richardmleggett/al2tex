// Alvis
//
// Alignment Diagrams in LaTeX and SVG
//
// Copyright 2018 Richard Leggett, Samuel Martin
// samuel.martin@earlham.ac.uk
// 
// This is free software, supplied without warranty.

package Alvis.Diagrams;

import Alvis.AlignmentFiles.Alignment;
import Alvis.DiagramOptions;
import java.io.*;
import java.awt.image.*;
import java.awt.*;
import javax.imageio.*;

public class CoverageMapImage {
    
    static public enum Type {
        SQUARE_MAP,
        LONG_MAP
    };
    
    private final static int MAX_ROWS = 400;
    private DiagramOptions options;
    private int targetSize;
    private String targetName;
    private int nRows;
    private int[] coverage;
    private int largestCoverage = 0;
    private String outputFilename;
    private Type mapType;
    private int longImageHeight = 16;
                            
    public CoverageMapImage(DiagramOptions o, int s, String _outputFilename, String _targetName) {
        options = o;
        targetSize = s;
        coverage = new int[targetSize];
        outputFilename = _outputFilename;
        targetName = _targetName;
        mapType = options.getCoverageMapImageType();
    }

    public void addAlignment(Alignment a) 
    {     
        for (int b=0; b<a.getBlockCount(); b++) 
        {
            int from = a.getBlockTargetStart(b);
            int to = from + a.getBlockSize(b);
            for (int i=from; i<to; i++) 
            {
                coverage[i]++;               
                if (coverage[i] > largestCoverage) 
                {
                    largestCoverage = coverage[i];
                }
            }
        }
    }    
    
    public void saveSquareImageFile(HeatMapScale heatMap) {
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
            ImageIO.write(bImage, "PNG", new File(outputFilename));
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public void saveLongImageFile(HeatMapScale heatMap)
    {
        double multiplier = 1.0;
        int imageWidth = targetSize;
        
        if (targetSize > 10000) {
            multiplier = 10000.0/targetSize;
            System.out.println("Multiplier "+multiplier);
            imageWidth = (int)((double)targetSize * multiplier);
        }
        
        
        System.out.println("Width "+imageWidth+" Height "+longImageHeight);
        BufferedImage bImage = new BufferedImage(imageWidth, longImageHeight, BufferedImage.TYPE_INT_RGB);
        
        Graphics g = bImage.getGraphics();
        g.setColor(new Color(200, 200, 200));
        g.fillRect(0, 0, targetSize, longImageHeight);

        for (int x=0; x<targetSize; x++) {
            for (int y=0; y<longImageHeight; y++) {
                bImage.setRGB((int)((double)x*multiplier), y, heatMap.getRGBColour(coverage[x]));
            }
        }        
        
        try {
            ImageIO.write(bImage, "PNG", new File(outputFilename));
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
    
    public String getFilename() {
        return outputFilename;
    }
    
    public int getTargetSize() {
        return targetSize;
    }
    
    public String getTargetName() {
        return targetName;
    }
    
    public void saveImageFile(HeatMapScale heatMap)
    {
        switch(mapType)
        {
            case SQUARE_MAP:
            {
                saveSquareImageFile(heatMap);
                break;
            }
            case LONG_MAP:
            {
                saveLongImageFile(heatMap);
                break;
            }
        }
    }
}
