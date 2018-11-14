// Al2Tex
//
// Alignment Diagrams in LaTeX
//
// Copyright 2012 Richard Leggett
// richard.leggett@tgac.ac.uk
// 
// This is free software, supplied without warranty.

package al2tex.Diagrams;

import java.io.*;
import java.util.*;
import java.awt.image.*;
import java.awt.*;
import javax.imageio.*;

public class HeatMapScale {
    private final static int HEATMAPLIMIT=10240;
    private int heatMapLength = 70;
    private int heatMapHeight = 8;
    private double ratio = 8./70;
    private Color[] heatMap = new Color[HEATMAPLIMIT];
    private String filename;
    
    public HeatMapScale() {
        this.setHeatMapSize(64);
    }
    
    public HeatMapScale(int s) {
        this.setHeatMapSize(s);
    }
    
    public void setHeatMapSize(int s) {
        heatMapLength = s;
        heatMapHeight = Math.max((int)((double)s * ratio), 8);
        heatMap = new Color[heatMapLength+1];
        
        heatMap[0] = new Color(200, 200, 200);
        for(int i = 1; i <=heatMapLength; i++)
        {
            heatMap[i] = Color.getHSBColor(0.7f*((float)(heatMapLength-(i-1)) / (float)heatMapLength), 0.85f, 1.0f);
        }
        
        System.out.println("Heat map size set to "+heatMapLength);
    }
    
    public void saveHeatmap(String heatMapFilename) {
        filename = heatMapFilename;
        BufferedImage bImage = new BufferedImage(heatMapLength+1, heatMapHeight, BufferedImage.TYPE_INT_RGB);
        for (int x=0; x<=heatMapLength; x++) {
            for (int y=0; y<heatMapHeight; y++) {
                bImage.setRGB(x, y, heatMap[x].getRGB());
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
    
    public void plotHeatMap(BufferedImage bImage, int xo, int yo, int w, int h, boolean restricted) {        
        double m = (double)heatMapLength/(double)w;
        for (int x=0; x<=w; x++) {
            int p = (int)((double)x * m);
            for (int y=0; y<h; y++) {
                bImage.setRGB(xo+x, yo+y, heatMap[p].getRGB());
            }
        }
        
        Graphics g = bImage.getGraphics();
        g.setColor(new Color(200, 200, 200));
        g.fillRect(xo-h-h-16, yo, h, h);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 48));
        g.drawString("0", xo-h-h, yo - h / 4);
        g.drawString("1", xo-10, yo - h / 4);
        g.drawString(Integer.toString(heatMapLength)+ (restricted ? "+":""), xo+w-50, yo - h / 4);
        g.drawString(Integer.toString(heatMapLength/2), xo+(w/2)-50, yo - h / 4);
        //g.drawLine(xo + (w/2), yo, xo+(w/2), yo-16);
    }
    
    public Color getColour(int i) {
        if (i >= heatMapLength) {
            return heatMap[heatMapLength -1];
        } else {
            return heatMap[i];
        }
    }
    
    public int getRGBColour(int i) {
        if (i > heatMapLength) {
            return heatMap[heatMapLength].getRGB();            
        } else {
            return heatMap[i].getRGB();
        }
    }
    
    public int getHeatMapSize() {
        return heatMapLength;
    }
    
    public int getHeatMapWidth() {
        return heatMapLength;
    }
    
    public int getHeatMapHeight() {
        return heatMapHeight;
    }
    
    public String getFilename() {
        return filename;
    }
}
