// Alvis
//
// Alignment Diagrams in LaTeX and SVG
//
// Copyright 2018 Richard Leggett, Samuel Martin
// samuel.martin@earlham.ac.uk
// 
// This is free software, supplied without warranty.

package Alvis.Diagrams;

import Alvis.Drawers.SVGDrawer;
import Alvis.Drawers.TikzDrawer;
import Alvis.Drawers.Drawer;
import Alvis.AlignmentFiles.AlignmentFile;
import Alvis.AlignmentFiles.Alignment;
import Alvis.AlignmentFiles.DetailedAlignmentFile;
import Alvis.AlignmentFilters.OverlapFilter;
import Alvis.DiagramOptions;
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
        String filename = o.getOutputFilePath() + "_coverageMap";
        
        if(o.getOutputFormat().equals("svg"))
        {
            int textSize = 16;                  
            m_drawer = new SVGDrawer(filename, true, 4, 1700, 700, 20, textSize);
        }
        else
        {
            m_drawer = new TikzDrawer(filename, true);
        }
    }
    
    public void makeBitmapsFromFile(AlignmentFile alignmentFile, String outputDirectory) 
    {             
        File imagesDir = new File(outputDirectory+"images");
        imagesDir.mkdir();
        
        // NOOOOOOOOOO!!!!
        if(alignmentFile instanceof DetailedAlignmentFile)
        {
            ((DetailedAlignmentFile)alignmentFile).filterAlignments(new OverlapFilter());
        }
        
        alignmentFile.sort(AlignmentFile.compareByTargetName);
        String filenamePrefix = outputDirectory + "images/";
        String previousTarget = "";
        for (int i = 0; i < alignmentFile.getNumberOfAlignments(); i++) 
        {              
            Alignment a = alignmentFile.getAlignment(i);
            if(a.getTargetName().compareTo(previousTarget) != 0)
            {
                String safeTargetName = a.getTargetName().replace('.', '_');
                String filename = filenamePrefix + safeTargetName + "_covmap.png";
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
        heatMapScale.saveHeatmap(outputDirectory + "images/heatmap.png");
    }
    
    public void writeOutputFile() 
    {
        m_drawer.openFile();
        m_drawer.drawScale(heatMapScale, 10, 5, largestCoverage);
        switch(mapType)
        {
            case SQUARE_MAP:
            {
                double y = 40;
                for(int i = 0; i < coverageMaps.size(); i++)
                {  
                    CoverageMapImage coverageMap = coverageMaps.get(i);
                    double x = (i % 2) * targetWidth * 1.5;
                    x += 30;

                    if(i % 2 == 0 && i > 0)
                    {
                        y += targetWidth * 1.25 + 20;
                        if(y + targetWidth > m_drawer.getPageHeight())
                        {
                            m_drawer.newPage();
                            m_drawer.drawScale(heatMapScale, 10, 5, largestCoverage);
                            y = 40;
                        }
                    }
                    m_drawer.drawCoverageMap(coverageMap, x, y);
                }
                break;
            }
            case LONG_MAP:
            {   
                int multiplier = 0;
                for(int i = 0; i < coverageMaps.size(); i++)
                {
                    double y = 25 + (multiplier * 15);
                    if(y + 15 > m_drawer.getPageHeight())
                    {
                        m_drawer.newPage();
                        m_drawer.drawScale(heatMapScale, 10, 5, largestCoverage);   
                        multiplier = 0;
                        y = 25;
                    }             
                    double x = 20;
                    CoverageMapImage coverageMap = coverageMaps.get(i);
                    m_drawer.drawCoverageLong(coverageMap, x, y, 200, 4, NUM_DIVIDERS);
                    multiplier++;
                }
                break;
            }
        }
        m_drawer.closeFile();
    }
}
