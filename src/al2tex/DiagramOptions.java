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

public class DiagramOptions {
    private String diagramType = null;
    private String inputFilename = null;
    private String outputDirectory = null;
    private String outputFilename = null;
    private String listFilename = null;
    private String inputFormat = null;
    private int maxCoverage = 0;
    private int maxPages = 0;
    private int maxTargets = 0;
    private boolean newHeatMapForEachContig = false;
    
    public void parseArgs(String[] args) {
        int i=0;
        
        while (i < (args.length-1)) {
            if (args[i].equalsIgnoreCase("-type")) {
                diagramType = args[i+1].toLowerCase();
                if ((!diagramType.equals("coverage")) &&
                   (!diagramType.equals("alignment")) &&
                   (!diagramType.equals("all"))) {
                    System.out.println("Error: type must be 'all', coverage' or 'alignment'.");
                    System.exit(0);
                }
                System.out.println("    Diagram type: " + diagramType);
            } else if (args[i].equalsIgnoreCase("-inputfmt")) {
                inputFormat = args[i+1].toLowerCase();
                if ((!inputFormat.equals("psl")) &&
                    (!inputFormat.equals("coords")) &&
                    (!inputFormat.equals("pileup")) &&
                    (!inputFormat.equals("tiling"))) {
                    System.out.println("Error: inputfmt must be 'psl', 'coords', 'pileup' or 'tiling'.");
                    System.exit(0);
                }
            } else if (args[i].equalsIgnoreCase("-in")) {
                inputFilename = args[i+1];
                System.out.println("  Input filename: "+inputFilename);
            } else if (args[i].equalsIgnoreCase("-list")) {
                listFilename = args[i+1];
                System.out.println("   List filename: "+listFilename);
            } else if (args[i].equalsIgnoreCase("-outdir")) {                
                outputDirectory = args[i+1];
                System.out.println("Output directory: "+outputDirectory);
            } else if (args[i].equalsIgnoreCase("-out")) {                
                outputFilename = args[i+1];
                System.out.println(" Output filename: "+outputFilename);
            } else if (args[i].equalsIgnoreCase("-maxtargets")) {
                maxTargets = Integer.parseInt(args[i+1]);
                System.out.println("     Max targets: " + maxTargets);
            } else if (args[i].equalsIgnoreCase("-maxcoverage")) {
                maxCoverage = Integer.parseInt(args[i+1]);
                System.out.println("    Max coverage: " + maxCoverage);
            } else {
                System.out.println("Unknown paramter: " + args[i]);
                System.exit(0);
            }
            
            i+=2;
        }
        
        if (inputFormat.equals("pileup")) {
            if (!diagramType.equals("coverage")) {          
                System.out.println("Error: For pileup files, you can only plot coverage diagrams.");
                System.exit(0);
            }
            if (outputFilename == null) {
                System.out.println("Error: you must specify a -out parameter.");
                System.exit(0);
            }            
            if (listFilename == null) {
                System.out.println("Error: you must specify a -list parameter.");
                System.exit(0);
            }            
        } else {        
            if (diagramType == null) {
                System.out.println("Error: you must specify a -type parameter.");
                System.exit(0);
            }        

            if (inputFormat == null) {
                System.out.println("Error: you must specify a -inputfmt parameter.");
                System.exit(0);
            }        

            if (inputFilename == null) {
                System.out.println("Error: you must specify a -in parameter.");
                System.exit(0);
            }        

            if (outputDirectory == null) {
                System.out.println("Error: you must specify a -outdir parameter.");
                System.exit(0);
            }               
        }
    }        
    
    public String getInputFilename() {
        return inputFilename;
    }
    
    public String getInputFormat() {
        return inputFormat;
    }
    
    public String getOutputDirectory() {
        return outputDirectory;
    }
    
    public String getOutputFilename() {
        return outputFilename;
    }
    
    public String getListFilename() {
        return listFilename;
    }

    public int getMaxCoverage() {
        return maxCoverage;
    }
    
    public String getDiagramType() {
        return diagramType;
    }
    
    public int getMaxTargets() {
        return maxTargets;
    }
    
    public String filterName(String s) {
        String a = s.replace("|", "$|$");
        String b = a.replace("_", "\\_");
        
        return b;
    }
    
    public String getInputFilenameLeaf() {
        File f = new File(inputFilename);
        return f.getName();
    }
    
    public boolean isNewHeatMapForEachContig() {
        return newHeatMapForEachContig;
    }
}
