// AlDiTex
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
import java.awt.*;
import javax.imageio.*;

public class HeatMapScale {
    private final static int HEATMAPLIMIT=10240;
    private int heatMapSize = 70;
    private Color[] heatMap = new Color[HEATMAPLIMIT];
    
    public HeatMapScale() {
        this.setHeatMapSize(64);
    }
    
    public HeatMapScale(int s) {
        this.setHeatMapSize(s);
    }
    
    public void setHeatMapSize(int s) {
        heatMapSize = s;
        
        heatMap = new Color[heatMapSize+1];
        
        heatMap[0] = new Color(200, 200, 200);
        for(int i = 1; i <=heatMapSize; i++)
        {
            heatMap[i] = Color.getHSBColor(0.7f*((float)(heatMapSize-(i-1)) / (float)heatMapSize), 0.85f, 1.0f);
        }
        
        System.out.println("Heat map size set to "+heatMapSize);
    }
    
    public void saveHeatmap(String filename) {
        BufferedImage bImage = new BufferedImage(heatMapSize+1, 8, BufferedImage.TYPE_INT_RGB);
        for (int x=0; x<=heatMapSize; x++) {
            for (int y=0; y<8; y++) {
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
        double m = (double)heatMapSize/(double)w;
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
        g.drawString(Integer.toString(heatMapSize)+ (restricted ? "+":""), xo+w-50, yo - h / 4);
        g.drawString(Integer.toString(heatMapSize/2), xo+(w/2)-50, yo - h / 4);
        //g.drawLine(xo + (w/2), yo, xo+(w/2), yo-16);
    }
    
    public Color getColour(int i) {
        if (i >= heatMapSize) {
            return heatMap[heatMapSize -1];
        } else {
            return heatMap[i];
        }
    }
    
    public int getRGBColour(int i) {
        if (i > heatMapSize) {
            return heatMap[heatMapSize].getRGB();            
        } else {
            return heatMap[i].getRGB();
        }
    }
    
    public int getHeatMapSize() {
        return heatMapSize;
    }
}
