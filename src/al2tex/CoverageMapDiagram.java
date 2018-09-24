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

public class CoverageMapDiagram extends TikzPicture
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
    
    public CoverageMapDiagram(DiagramOptions o) {
        super(o.getOutputFilePath() + "_coverageMap.tex");
        options = o;
    }
    
    private String getTexSafeTargetName(Alignment a)
    {
        return a.getTargetName().replace(".", "-");
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
                String filename = filenamePrefix + getTexSafeTargetName(a) + "_covmap.png";
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
        openFile();

        for(CoverageMapImage coverageMap : coverageMaps)
        {  
            writeNewImage(coverageMap);
            try
            {
                bw.write("\\hspace{1cm}");
            }
            catch(IOException e)
            {
                System.out.println(e);
            }
        }
        closeFile();
    }
    
    protected void writeTexHeader() {
        super.writeTexHeader();
        try {
            bw.write("\\begin{tikzpicture}[x=1mm,y=1mm]"); bw.newLine();
            bw.write("\\node[anchor=south west, inner sep=0pt, outer sep=0pt] at (0,0) {\\includegraphics[width="+targetWidth/2+"mm,height="+rowHeight+"mm]{heatmap.png}};"); bw.newLine();
            bw.write("\\node [anchor=west] at (20,"+(pictureHeight-(rowHeight / 2))+") {Coverage};"); bw.newLine();
            bw.write("\\node at (0,"+(pictureHeight-(rowHeight / 2))+") {0};"); bw.newLine();
            bw.write("\\node at ("+(targetWidth / 2)+","+(pictureHeight-(rowHeight / 2))+") {"+heatMapScale.getHeatMapSize()+"};"); bw.newLine();
            bw.write("\\end{tikzpicture}"); bw.newLine();
            bw.write("\\vspace{5mm}"); bw.newLine();
            bw.write("\\newline"); bw.newLine();
            bw.write("\\noindent"); bw.newLine();            
        } catch (IOException e){
            System.out.println(e);
        }
    }
        
    private void writeNewImage(CoverageMapImage coverageMap) 
    {          
        try 
        {
            int nRows = coverageMap.getNumberOfRows();
            int targetSize = coverageMap.getTargetSize();
            String targetName = coverageMap.getTargetName().replace("_", "\\string_");
            String filename = coverageMap.getFilename().replace("_", "\\string_");
            
            bw.write("\\begin{tikzpicture}[x=1mm,y=1mm]"); bw.newLine();
         
            // draw x-axis labels
            double rowSize = (double)targetSize / (double)nRows;
            for (int r=0; r<nRows; r+=50) {
                int rowY = targetHeight - (int)((double)r * ((double)targetHeight / (double)nRows));
                bw.write("\\node at (0,"+rowY+") {"+(int)((double)r*rowSize)+"};"); bw.newLine();
            }
            
            int rowY = targetHeight - (int)((double)(nRows) * ((double)targetHeight / (double)nRows));
            bw.write("\\node at (0,"+rowY+") {"+targetSize+"};"); bw.newLine();
            
            // draw the image
            bw.write(   "\\node[anchor=south west, inner sep=0pt, outer sep=0pt] at ("+imageOffset+",0)" +  
                        "{\\includegraphics[width="+targetWidth+"mm,height="+targetHeight+"mm]{" + filename + "}};"); 
            bw.newLine();
            
            // draw the text labels
            int textxPos = 5 + targetWidth / 2;
            bw.write("\\node at (" + textxPos + ", -5) {Each row represents "+(int)rowSize+" nt};"); bw.newLine();
            bw.write("\\node at (" + textxPos + ", -10) {" + targetName + "};"); bw.newLine();
            bw.write("\\node[rotate=90] at (-10,60) {Position in genome (nt)};"); bw.newLine();
            bw.write("\\end{tikzpicture}"); bw.newLine();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }
    } 
}
