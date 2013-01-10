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
    private static final int NUM_DIVIDERS = 4;
    private AlignmentFile alignmentFile;
    private DiagramOptions options;
    private String outputDirectory;
    private int pictureWidth = 160;                  // Width of picture
    private int targetWidth = 120;                   // Width of reference (target) bar, in mm
    private int imageOffset = 7;
    private int targetHeight = targetWidth;
    private int rowHeight = 2;                       // Height of bar, in mm
    private int yStep = (int)(rowHeight * 1.5);      // Allow for gap
    private int pictureHeight = (int)(rowHeight * 2.5);                // Calculated later, based on number of alignments
    private int nRows;
    private int largestCoverage = 0;
    private HeatMapScale heatMapScale = new HeatMapScale();   // Heat map colours
    
    public CoverageMapDiagram(DiagramOptions o) {
        options = o;
    }
    
    public void makeBitmapsFromFile(AlignmentFile f, String o) {        
        outputDirectory = o;
        alignmentFile = f;
        
        File imagesDir = new File(outputDirectory+"/images");
        imagesDir.mkdir();
        
        for (int pass=0; pass<2; pass++) {
            CoverageMapImage mapImage = null;
            String previousTarget = new String("");
            
            for (int i=0; i<alignmentFile.getNumberOfAlignments(); i++) {
                Alignment a = alignmentFile.getAlignment(i);

                if (a.getTargetName().compareTo(previousTarget) != 0) {
                    if (mapImage == null) {
                        mapImage = new CoverageMapImage(options, a.getTargetSize());                    
                    } else {
                        System.out.println("Only one target allowed for this diagram");
                        break;
                    }
                }

                mapImage.addAlignment(a);
                previousTarget = a.getTargetName();
            }        

            if (mapImage != null) {
               if (pass > 0) {
                    String filename = outputDirectory + "/images/" + previousTarget + "covmap.png";
                    mapImage.saveImageFile(filename, heatMapScale);
                    nRows = mapImage.getNumberOfRows();
                }
                        
                largestCoverage = mapImage.getLargestCoverage();
            }

            System.out.println("          Heatmap size was: " + heatMapScale.getHeatMapSize());
            System.out.println("Largest coverage value was: " + largestCoverage);
            if (pass == 0) {
                heatMapScale.setHeatMapSize(largestCoverage);
            } else if (pass == 1){
                heatMapScale.saveHeatmap(outputDirectory + "/heatmap.png");
            }
        }
    }
    
    public void writeTexFile(String filename) {
        try
        {
            String previousTarget = new String("");
            String finalTarget = alignmentFile.getAlignment(alignmentFile.getNumberOfAlignments() - 1).getTargetName();
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
            
            writeTexHeader(bw);
            
            for (int i=0; i<alignmentFile.getNumberOfAlignments(); i++) {
                Alignment a = alignmentFile.getAlignment(i);

                if (a.getTargetName().compareTo(previousTarget) != 0) {
                    writeNewImage(bw, a.getTargetName(), a.getTargetSize());
                    if (a.getTargetName().compareTo(finalTarget) != 0) {
                        bw.write("\\linebreak"); bw.newLine();
                    }
                }

                previousTarget = a.getTargetName();
            }        
            
            writeTexFooter(bw);

            // Finish up
            bw.flush();
            bw.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    private void writeTexHeader(BufferedWriter bw) {
        try {
            bw.write("\\documentclass[a4paper,11pt,oneside]{article}"); bw.newLine();
            bw.write("\\usepackage{graphicx}"); bw.newLine();
            bw.write("\\usepackage{url}"); bw.newLine();
            bw.write("\\usepackage{subfigure}"); bw.newLine();
            bw.write("\\usepackage{rotating}"); bw.newLine();
            bw.write("\\usepackage{color}"); bw.newLine();
            bw.write("\\usepackage{tikz}"); bw.newLine();
            bw.write("\\usepackage[landscape,top=3cm, bottom=3cm, left=3cm, right=3cm]{geometry}"); bw.newLine();
            bw.write("\\begin{document}"); bw.newLine();
            bw.write("\\let\\mypdfximage\\pdfximage"); bw.newLine();
            bw.write("\\def\\pdfximage{\\immediate\\mypdfximage}"); bw.newLine();
            bw.write("\\sffamily"); bw.newLine();
            bw.write("\\scriptsize"); bw.newLine();
            bw.write("\\noindent"); bw.newLine();

            bw.write("\\begin{tikzpicture}[x=1mm,y=1mm]"); bw.newLine();
            bw.write("\\node[anchor=south west, inner sep=0pt, outer sep=0pt] at (0,0) {\\includegraphics[width="+targetWidth/2+"mm,height="+rowHeight+"mm]{heatmap.png}};"); bw.newLine();
            bw.write("\\node [anchor=west] at (25,"+(pictureHeight-(rowHeight / 2))+") {Coverage};"); bw.newLine();
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
    
    private void writeTexFooter(BufferedWriter bw) {
        try {
            bw.write("\\end{document}"); bw.newLine();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    private void writeNewImage(BufferedWriter bw, String targetName, int targetSize) {
        double unit = (double)targetWidth / targetSize;                

        try {
            String filename = "images/" + targetName + "covmap.png";

            bw.write("\\begin{tikzpicture}[x=1mm,y=1mm]"); bw.newLine();
            //for (int i=0; i<=NUM_DIVIDERS; i++) {
            //    int num = i == NUM_DIVIDERS ? targetSize: (int)((targetSize / NUM_DIVIDERS) * i);
            //    int pos = (int)((double)num * unit);
                
                //bw.write("\\draw [dashed] ("+pos+", 0) -- ("+pos+","+(pictureHeight - yStep)+");"); bw.newLine();
            //    bw.write("\\node at ("+pos+","+(pictureHeight-(rowHeight / 2))+") {"+num+"};"); bw.newLine();
            //}
            
            double rowSize = (double)targetSize / (double)nRows;
            for (int r=0; r<nRows; r+=50) {
                int rowY = targetHeight - (int)((double)r * ((double)targetHeight / (double)nRows));
                //bw.write("\\draw [dashed] (0,"+rowY+") -- ("+imageOffset+","+rowY+");"); bw.newLine();
                bw.write("\\node at (0,"+rowY+") {"+(int)((double)r*rowSize)+"};"); bw.newLine();
            }
            
            //bw.write("\\node at (0,"+targetHeight+") {0};"); bw.newLine();
            int rowY = targetHeight - (int)((double)(nRows) * ((double)targetHeight / (double)nRows));
            //bw.write("\\node at ("+(targetWidth+imageOffset+7)+","+rowY+") {"+targetSize+"};"); bw.newLine();
            bw.write("\\node at (0,"+rowY+") {"+targetSize+"};"); bw.newLine();
            
            bw.write("\\node[anchor=south west, inner sep=0pt, outer sep=0pt] at ("+imageOffset+",0) {\\includegraphics[width="+targetWidth+"mm,height="+targetHeight+"mm]{"+filename+"}};"); bw.newLine();
            //bw.write("\\node [anchor=west] at ("+(targetWidth + 5)+","+(pictureHeight-(rowHeight * 2))+") {"+targetName+"};"); bw.newLine();
            bw.write("\\node at (70, -5) {Each row represents "+(int)rowSize+" nt};"); bw.newLine();
            bw.write("\\node[rotate=90] at (-10,60) {Position in genome (nt)};"); bw.newLine();
            bw.write("\\end{tikzpicture}"); bw.newLine();
        } catch (IOException e) {
            System.out.println(e);
        }
    } 
}
