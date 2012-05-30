// AlDiTex
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

public class PSLCoverageDiagram
{
    private static final int NUM_DIVIDERS = 4;
    private PSLFile pslFile;
    private DiagramOptions options;
    private String outputDirectory;
    private int pictureWidth = 220;                  // Width of picture
    private int targetWidth = 180;                   // Width of reference (target) bar, in mm
    private int rowHeight = 2;                       // Height of bar, in mm
    private int yStep = (int)(rowHeight * 1.5);      // Allow for gap
    private int pictureHeight = (int)(rowHeight * 2.5);                // Calculated later, based on number of alignments
    private int rowsPerPage = (140 - (2 * yStep)) / yStep;
    private int largestCoverage = 0;
    private HeatMapScale heatMapScale = new HeatMapScale();   // Heat map colours
    
    public PSLCoverageDiagram(DiagramOptions o) {
        options = o;
    }
    
    public void makeBitmaps(PSLFile p, String o) {        
        outputDirectory = o;
        pslFile = p;
        
        File imagesDir = new File(outputDirectory+"/images");
        imagesDir.mkdir();
        
        for (int pass=0; pass<2; pass++) {
            PSLCoverageImage pslImage = null;
            String previousTarget = new String("");
            
            for (int i=0; i<pslFile.getNumberOfAlignments(); i++) {
                PSLAlignment a = pslFile.getAlignment(i);

                if (a.getTargetName().compareTo(previousTarget) != 0) {
                    if (pslImage != null) {
                        if (pass > 0) {
                            String filename = outputDirectory + "/images/" + previousTarget + ".png";
                            pslImage.saveImageFile(filename, heatMapScale);
                        }
                        
                        if (pslImage.getLargestCoverage() > largestCoverage) {
                            largestCoverage = pslImage.getLargestCoverage();
                        }
                    }

                    pslImage = new PSLCoverageImage(options, a.getTargetSize());
                }

                pslImage.addAlignment(a);
                previousTarget = a.getTargetName();
            }        

            if (pslImage != null) {
                String filename = outputDirectory + "/images/" + previousTarget + ".png";
                pslImage.saveImageFile(filename, heatMapScale);

                if (pslImage.getLargestCoverage() > largestCoverage) {
                    largestCoverage = pslImage.getLargestCoverage();
                }                
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
    
    public void writeTexFile() {
        try
        {
            String filename = new String(outputDirectory+"/coverage_diagram.tex");
            String previousTarget = new String("");
            String finalTarget = pslFile.getAlignment(pslFile.getNumberOfAlignments() - 1).getTargetName();
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
            
            writeTexHeader(bw);
            
            for (int i=0; i<pslFile.getNumberOfAlignments(); i++) {
                PSLAlignment a = pslFile.getAlignment(i);

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
            bw.write("\\node [anchor=west] at ("+((targetWidth/2) + 5)+","+(pictureHeight-(rowHeight * 2))+") {Scale};"); bw.newLine();
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
            String filename = "images/" + targetName + ".png";

            bw.write("\\begin{tikzpicture}[x=1mm,y=1mm]"); bw.newLine();
            for (int i=0; i<=NUM_DIVIDERS; i++) {
                int num = i == NUM_DIVIDERS ? targetSize: (int)((targetSize / NUM_DIVIDERS) * i);
                int pos = (int)((double)num * unit);
                
                //bw.write("\\draw [dashed] ("+pos+", 0) -- ("+pos+","+(pictureHeight - yStep)+");"); bw.newLine();
                bw.write("\\node at ("+pos+","+(pictureHeight-(rowHeight / 2))+") {"+num+"};"); bw.newLine();
            }
            
            bw.write("\\node[anchor=south west, inner sep=0pt, outer sep=0pt] at (0,0) {\\includegraphics[width="+targetWidth+"mm,height="+rowHeight+"mm]{"+filename+"}};"); bw.newLine();
            bw.write("\\node [anchor=west] at ("+(targetWidth + 5)+","+(pictureHeight-(rowHeight * 2))+") {"+targetName+"};"); bw.newLine();
            bw.write("\\end{tikzpicture}"); bw.newLine();
        } catch (IOException e) {
            System.out.println(e);
        }
    } 
}
