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

public class Al2Tex {
    public static void main(String[] args) {
        DiagramOptions options = new DiagramOptions();

        System.out.println("al2tex\n");
        options.parseArgs(args);
    
        if ((options.getInputFormat().equals("pileup")) &&
            (options.getDiagramType().equals("coverage"))) {
            System.out.println("\nOpening pileup file");
            PileupFile pileupFile = new PileupFile(options.getInputFilename(), options.getListFilename());
            System.out.println("Building coverage diagram");
            PileupCoverageDiagram pileupCoverageDiagram = new PileupCoverageDiagram(options, pileupFile);
            if (options.getDomainsFilename() != null) {
                System.out.println("Loading domain information");
                pileupCoverageDiagram.addDomainInfo(options.getDomainsFilename());
            }
            System.out.println("Making bitmaps");
            pileupCoverageDiagram.makeBitmaps();
            
        } else if (options.getInputFormat().equals("psl")) {
            System.out.println("\nOpening PSL file");
            PSLFile pslFile = new PSLFile(options.getInputFilename());
            
            if ((options.getDiagramType().equals("coveragemap"))  ||
                (options.getDiagramType().equals("all"))) {
                System.out.println("Building coverage map diagram");
                CoverageMapDiagram coverageDiagram = new CoverageMapDiagram(options);
                System.out.println("Making bitmaps");
                coverageDiagram.makeBitmapsFromFile(pslFile, options.getOutputDirectory());
                System.out.println("Writing LaTeX files");
                coverageDiagram.writeTexFile(options.getOutputFilePath());
            }
            
            if ((options.getDiagramType().equals("coverage")) ||
                (options.getDiagramType().equals("all"))) {
                System.out.println("Building coverage diagram");
                PSLCoverageDiagram pslCoverageDiagram = new PSLCoverageDiagram(options);
                System.out.println("Making bitmaps");
                pslCoverageDiagram.makeBitmaps(pslFile, options.getOutputDirectory());
                System.out.println("Writing LaTeX files");
                pslCoverageDiagram.writeTexFile(options.getOutputFilePath());
            }

            if ((options.getDiagramType().equals("alignment"))  ||
                (options.getDiagramType().equals("all"))) {
                System.out.println("Building alignment diagram");
                PSLAlignmentDiagram pslAlignmentDiagram = new PSLAlignmentDiagram(options, pslFile);
                System.out.println("Writing LaTeX files");
                pslAlignmentDiagram.writeTexFile(options.getOutputFilePath());
            }
        } else if (options.getInputFormat().equals("sam")) {
            System.out.println("\nOpening SAM file");
            SAMFile samFile = new SAMFile(options.getInputFilename(), options.getTargetSize());

            if ((options.getDiagramType().equals("coveragemap"))  ||
                (options.getDiagramType().equals("all"))) {
                System.out.println("Building coverage map diagram");
                CoverageMapDiagram coverageDiagram = new CoverageMapDiagram(options);
                System.out.println("Making bitmaps");
                coverageDiagram.makeBitmapsFromFile(samFile, options.getOutputDirectory());
                System.out.println("Writing LaTeX files");
                coverageDiagram.writeTexFile(options.getOutputFilePath());
            }            
        } else if (options.getInputFormat().equals("coords") || options.getInputFormat().equals("tiling")) {
            int type = 0;
            if (options.getInputFormat().equals("coords")) {
                System.out.println("\nOpening MUMmer show-coords file");
                type = MummerAlignment.SHOW_COORDS;
            } else if (options.getInputFormat().equals("tiling")) {
                System.out.println("\nOpening MUMmer show-tiling file");
                type = MummerAlignment.SHOW_TILING;
            }
            MummerFile alignmentFile = new MummerFile(options.getInputFilename(), type);
            if ((options.getDiagramType().equals("alignment"))  ||
                (options.getDiagramType().equals("all"))) {
                System.out.println("Building alignment diagram");
                MummerAlignmentDiagram nucmerAlignmentDiagram = new MummerAlignmentDiagram(options, alignmentFile);
                System.out.println("Writing LaTeX files");
                nucmerAlignmentDiagram.writeTexFile(options.getOutputFilePath());
            }            
        }
        System.out.println("Done");
    }
}
