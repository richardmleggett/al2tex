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

public class CoverageMapDiagram
{
    private DiagramOptions options;
    private int pictureWidth = 160;                  // Width of picture
    private int targetWidth = 100;                   // Width of reference (target) bar, in mm
    private int imageOffset = 7;
    private int targetHeight = targetWidth;
    private int rowHeight = 2;                       // Height of bar, in mm
    private int yStep = (int)(rowHeight * 1.5);      // Allow for gap
    private int pictureHeight = (int)(rowHeight * 2.5);                // Calculated later, based on number of alignments
    private int largestCoverage = 0;
    private HeatMapScale heatMapScale = new HeatMapScale();   // Heat map colours
    private ArrayList<CoverageMapImage> coverageMaps = new ArrayList();
    private CoverageMapImage.Type mapType;
    private Drawer m_drawer;
    
    private static final int NUM_DIVIDERS = 4;
    
    public CoverageMapDiagram(DiagramOptions o) {
        //super(o.getOutputFilePath() + "_coverageMap.tex");
        options = o;
        mapType = options.getCoverageMapImageType();
        m_drawer = new TikzDrawer(o.getOutputFilePath() + "_coverageMap");
    }
    
    public void makeBitmapsFromFile(AlignmentFile alignmentFile, String outputDirectory) 
    {             
        File imagesDir = new File(outputDirectory+"/images");
        imagesDir.mkdir();

        alignmentFile.sortAlignmentsByTargetName();
        String filenamePrefix = outputDirectory + "/images/";
        String previousTarget = "";
        for (int i = 0; i < alignmentFile.getNumberOfAlignments(); i++) 
        {              
            Alignment a = alignmentFile.getAlignment(i);
            if(a.getTargetName().compareTo(previousTarget) != 0)
            {
                String filename = filenamePrefix + a.getTargetName() + "_covmap.png";
                System.out.println("Making new CoverageMapImage: " + filename + " with size " + a.getTargetSize());
                CoverageMapImage mapImage = new CoverageMapImage(options, a.getTargetSize(), filename, a.getTargetName()); 
                coverageMaps.add(mapImage);
            }
            coverageMaps.get(coverageMaps.size() - 1).addAlignment(a);
            previousTarget = a.getTargetName();
        }

        for(CoverageMapImage mapImage : coverageMaps)
        {         
            if(mapImage.getLargestCoverage() > largestCoverage)
            {
                largestCoverage = mapImage.getLargestCoverage();
            }
        }
        heatMapScale.setHeatMapSize(largestCoverage);

        for(CoverageMapImage mapImage : coverageMaps)
        {
            mapImage.saveImageFile(heatMapScale);
        }           

        System.out.println("          Heatmap size was: " + heatMapScale.getHeatMapSize());
        System.out.println("Largest coverage value was: " + largestCoverage);
        heatMapScale.saveHeatmap(outputDirectory + "/heatmap.png");
    }
    
    public void writeTexFile() 
    {
        m_drawer.openFile();
        drawScale();
        for(int i = 0; i < coverageMaps.size(); i++)
        {  
            CoverageMapImage coverageMap = coverageMaps.get(i);
            switch(mapType)
            {
                case SQUARE_MAP:
                {
                    if(i % 2 == 0 && i > 0)
                    {
                        m_drawer.drawNewPage();
                        drawScale();
                    }
                    writeNewSquareImage(coverageMap);
                    break;
                }
                case LONG_MAP:
                {   
                    writeNewLongImage(coverageMap);
                }
            }
        }
        m_drawer.closeFile();
    }
    
    protected void writeTexHeader() {
        //super.writeTexHeader();
        //drawScale();
    }
        
    private void writeNewSquareImage(CoverageMapImage coverageMap) 
    {          
        int nRows = coverageMap.getNumberOfRows();
        int targetSize = coverageMap.getTargetSize();
        String targetName = coverageMap.getTargetName();
        String filename = coverageMap.getFilename();

        m_drawer.openPicture(1,1);

        // draw x-axis labels
        double rowSize = (double)targetSize / (double)nRows;
        for (int r=0; r<nRows; r+=50) {
            int rowY = targetHeight - (int)((double)r * ((double)targetHeight / (double)nRows));
            m_drawer.drawText(0, rowY, Integer.toString((int)(r*rowSize)));
        }

        int rowY = targetHeight - (int)((double)(nRows) * ((double)targetHeight / (double)nRows));
        m_drawer.drawText(0, rowY, Integer.toString(targetSize));
        
        // draw the image
        m_drawer.drawImage(imageOffset, 0, targetWidth, targetHeight, filename, "[anchor=south west, inner sep=0pt, outer sep=0pt]");

        // draw the text labels
        int textxPos = 5 + targetWidth / 2;
        m_drawer.drawText(textxPos, -5, "Each row represents "+(int)rowSize+" nt");
        m_drawer.drawText(textxPos, -10, targetName);
        m_drawer.drawTextRotated(-10, 60, "Position in genome (nt)", 90);
        m_drawer.closePicture();
        m_drawer.drawHorizontalGap(10);
    } 
    
    private void writeNewLongImage(CoverageMapImage coverageMap) 
    {
        int targetSize = coverageMap.getTargetSize();
        String targetName = coverageMap.getTargetName();
        String filename = coverageMap.getFilename();
        double unit = (double)targetWidth / targetSize;                

        m_drawer.openPicture(1,1);

        for (int i=0; i <= NUM_DIVIDERS; i++) {
            int num = i == NUM_DIVIDERS ? targetSize: (int)((targetSize / NUM_DIVIDERS) * i);
            int pos = (int)((double)num * unit);

            m_drawer.drawText(pos, (pictureHeight-(rowHeight / 2)), Integer.toString(num));
        }

        m_drawer.drawImage(0, 0, targetWidth, rowHeight, filename, "[anchor=south west, inner sep=0pt, outer sep=0pt]");
        m_drawer.drawText(targetWidth + 10, (pictureHeight-(rowHeight * 2)), targetName);
        m_drawer.closePicture();
        m_drawer.drawVerticalGap(5);
        m_drawer.drawNewline();
    }
    
    private void drawScale()
    {
        int yPos = pictureHeight-(rowHeight/2);
        m_drawer.openPicture(1,1);
        m_drawer.drawImage(0, 0, targetWidth / 2, rowHeight, "heatmap.png", "[anchor=south west, inner sep=0pt, outer sep=0pt]");
        m_drawer.drawText(27.5, yPos, "Coverage");
        m_drawer.drawText(0, yPos, "0");
        m_drawer.drawText(targetWidth/2, yPos, Integer.toString(heatMapScale.getHeatMapSize()));
        m_drawer.closePicture();
        m_drawer.drawVerticalGap(5);
        m_drawer.drawNewline();              
    }
}
