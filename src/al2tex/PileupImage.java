package al2tex;

import java.io.*;
import java.util.*;
import java.awt.image.*;
import javax.swing.*;
import java.awt.*;
import javax.imageio.*;
import java.lang.*;

public class PileupImage {
    private static final int MAX_COVERAGE=500;
    private BufferedImage bImage = null;
    private int rowHeight;
    private int barHeight;
    private int chromosomeGap;
    private int heatMapHeight;
    private int imageWidth;
    private int labelWidth;
    private int barWidth;
    private int heatMapWidth;
    private int heatMapX;
    private int heatMapY;
    private int barOffset;
    private int imageHeight;
    private int nContigs = 0;
    private int chrStart = 0;
    private double pixelMultiplier;
    private int unitWidth;
    private int y;
    private HeatMapScale heatMap;
    private DiagramOptions options;
    
    public PileupImage(int n, int nc, int hp, int hc, DiagramOptions o) {
        options = o;
        int maxCoverage = options.getMaxCoverage();
        int heatMapSize = hc;
        
        if (maxCoverage > 0) {
            heatMapSize = hc > maxCoverage ? maxCoverage:hc;
        }
        
        heatMap = new HeatMapScale(heatMapSize);

        setupSizes(n, nc);
        calculatePixelMultiplier(hp);        
        storeStart();
        
        bImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        
        if (bImage == null) {
            System.out.println("ERROR: Couldn't create image\n");
            System.exit(0);
        }
        
        Graphics g = bImage.getGraphics();
        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 0, imageWidth, imageHeight);        
        g.setColor(Color.BLACK);
        
        heatMap.plotHeatMap(bImage, heatMapX, heatMapY, heatMapWidth, heatMapHeight, options.getMaxCoverage() > 0 ? true:false);
    }
    
    private void setupSizes(int numberOfGenes, int numberOfChromosomes) {
        rowHeight = 9;
        imageWidth = 3000;
        labelWidth = 250;
        imageHeight = (2*heatMapHeight) + (numberOfGenes*rowHeight) + (numberOfChromosomes * chromosomeGap) + chromosomeGap;
        barWidth = imageWidth-labelWidth-16;
        barHeight = rowHeight-3;
        barOffset = labelWidth;
        chromosomeGap = 48;        
        heatMapWidth = imageWidth / 4;
        heatMapHeight = 64;
        heatMapX = imageWidth - (imageWidth/4) - (imageWidth/16);
        heatMapY = heatMapHeight;
        y = heatMapHeight + heatMapHeight + chromosomeGap + (2 * rowHeight);
    }
    
    private void calculatePixelMultiplier(int s) {
        pixelMultiplier = (double)barWidth / (double)s;
        unitWidth = (int)(Math.ceil(pixelMultiplier));
    }
    
    private int getXCoord(int p) {
        return barOffset + (int)((double)p * pixelMultiplier);
    }
    
    public void addContig(PileupContig contig, DomainInfo di) {
        Graphics g = bImage.getGraphics();
        calculatePixelMultiplier(contig.getSize());
        
        if (options.isNewHeatMapForEachContig()) {
            int hc = contig.getHighestCoverage();
            int maxCoverage = options.getMaxCoverage();
            int heatMapSize = hc;
            if (maxCoverage > 0) {
                heatMapSize = hc > maxCoverage ? maxCoverage:hc;
            }
            heatMap = new HeatMapScale(heatMapSize);
        }
        
        for (int i=0; i<contig.getSize(); i++) {
            int xStart = getXCoord(i);
            int xEnd = xStart + unitWidth;
            g.setColor(heatMap.getColour(contig.getCoverage(i)));
            g.fillRect(xStart, y, unitWidth, barHeight);
        }
        
        if (di != null) {
            if (di.getNBARCStart() > 0) {
                g.setColor(Color.BLACK);
                plotDomain(di.getNBARCStart(), di.getNBARCEnd(), g);
            }

            if (di.getLRRStart() > 0) {
                g.setColor(Color.BLACK);
                plotDomain(di.getLRRStart(), di.getLRREnd(), g);
            }        
        }
        
        nContigs++;
        y+=rowHeight;
    }
    
    private void plotDomain(int start, int end, Graphics g) {
        int startX = getXCoord(start);
        int endX = getXCoord(end);
        
        g.drawLine(startX, y-1, startX, y+barHeight);
        g.drawLine(endX, y-1, endX, y+barHeight);
        g.drawLine(startX, y-1, endX, y-1);
        g.drawLine(startX, y+barHeight, endX, y+barHeight);        
        //plotTri(start, g);
        //plotTri(end, g);
        //g.drawLine(startX, y-4, endX, y-4);
        //g.drawLine(startX, y-3, endX, y-3);
    }
    
    private void plotTri(int x, Graphics g) {
        int xpos = getXCoord(x);
        int[] xs = {xpos, xpos-4, xpos+5};
        int[] ys = {y+4, y-4, y-4};
        g.fillPolygon(xs, ys, 3);
    }
    
    public void saveImageFile(String filename) {
        try {
            ImageIO.write(bImage, "PNG", new File(filename));
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }   
    
    public HeatMapScale getHeatMap() {
        return heatMap;
    }
    
    public void addChromosomeBreak() {
        y+=chromosomeGap;
    }
    
    public void storeStart() {
        chrStart = y;
    }
    
    public void labelSet(String label) {
        int middle = chrStart + ((y - chrStart)/2);
        Graphics g = bImage.getGraphics();
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 64));
        g.drawString(label, 16, middle+40);
    }
}
