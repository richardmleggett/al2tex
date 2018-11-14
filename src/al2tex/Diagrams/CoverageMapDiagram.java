// Al2Tex
//
// Alignment Diagrams in LaTeX
//
// Copyright 2012 Richard Leggett
// richard.leggett@tgac.ac.uk
// 
// This is free software, supplied without warranty.

package al2tex.Diagrams;

import al2tex.Drawers.SVGDrawer;
import al2tex.Drawers.TikzDrawer;
import al2tex.Drawers.Drawer;
import al2tex.AlignmentFIles.AlignmentFile;
import al2tex.AlignmentFIles.Alignment;
import al2tex.AlignmentFIles.DetailedAlignmentFile;
import al2tex.AlignmentFilters.OverlapFilter;
import al2tex.DiagramOptions;
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
            m_drawer = new SVGDrawer(filename, true, 4, 1242, 595);
        }
        else
        {
            m_drawer = new TikzDrawer(filename, true);
        }
    }
    
    public void makeBitmapsFromFile(AlignmentFile alignmentFile, String outputDirectory) 
    {             
        File imagesDir = new File(outputDirectory+"/images");
        imagesDir.mkdir();
        
        //TODO: write filter for overlaps so we don't get extra coverage.
        // NOOOOOOOOOO!!!!
        if(alignmentFile instanceof DetailedAlignmentFile)
        {
            ((DetailedAlignmentFile)alignmentFile).filterAlignments(new OverlapFilter());
        }
        
        
        alignmentFile.sortAlignments(AlignmentFile.compareByTargetName);
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
    
    public void writeOutputFile() 
    {
        m_drawer.openFile();
        m_drawer.drawScale(heatMapScale, 10, 5);
        switch(mapType)
        {
            case SQUARE_MAP:
            {
                double y = 30;
                for(int i = 0; i < coverageMaps.size(); i++)
                {  
                    CoverageMapImage coverageMap = coverageMaps.get(i);
                    double x = (i % 2) * targetWidth * 1.5;
                    x += 30;

                    if(i % 2 == 0 && i > 0)
                    {
                        y += targetWidth * 1.25 + 30;
                        if(y > m_drawer.getPageHeight())
                        {
                            m_drawer.newPage();
                            m_drawer.drawScale(heatMapScale, 10, 5);
                            y = 30;
                        }
                    }
                    m_drawer.drawCoverageMap(coverageMap, x, y);
                }
                break;
            }
            case LONG_MAP:
            {   
                for(int i = 0; i < coverageMaps.size(); i++)
                {
                    double y = 20 + (i * 15);
                    double x = 20;
                    CoverageMapImage coverageMap = coverageMaps.get(i);
                    m_drawer.drawCoverageLong(coverageMap, x, y, 200, 4, NUM_DIVIDERS);
                }
                break;
            }
        }
        m_drawer.closeFile();
    }
}
