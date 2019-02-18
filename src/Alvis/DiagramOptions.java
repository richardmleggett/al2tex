// Alvis
//
// Alignment Diagrams in LaTeX and SVG
//
// Copyright 2018 Richard Leggett, Samuel Martin
// samuel.martin@earlham.ac.uk
// 
// This is free software, supplied without warranty.

package Alvis;

import Alvis.Diagrams.CoverageMapImage;
import java.io.*;

public class DiagramOptions {
    private String diagramType = null;
    private String inputFilename = null;
    private String outputDirectory = null;
    private String outputFilename = "diagram.tex";
    private String listFilename = null;
    private String inputFormat = null;
    private String domainsFilename = null;
    private String outputFormat = null;
    private int maxCoverage = 0;
    private int maxTargets = 0;
    private String tSizes = "";
    private int rowHeight = 10;
    private int rowSpacer = 0;
    private boolean newHeatMapForEachContig = false;
    private double minContigAlignmentProp = 0.01;
    private String alignmentQueryName = null;
    private String alignmentRefName = null;
    private CoverageMapImage.Type cmiType = CoverageMapImage.Type.SQUARE_MAP;
    private int binSize = 30;
    private String blastFormatString;
    private boolean filter = false;
    private boolean findChimeras = false;
    private boolean printChimeras = false;
    
    public void parseArgs(String[] args) {
        int i=0;
        
        if (args.length <= 1) {
            System.out.println("Alvis - ALignment VISualisation tool");
            System.out.println("Syntax: Java -jar .../Alvis.jar [options]");
            System.out.println();
            System.out.println("Options:");
            System.out.println("    -type alignment|contigalignment|coveragemap|genomecoverage \t\t Type of diagram to draw.");
            System.out.println("    -inputfmt psl|coords|tiling|sam|paf|blast \t\t\t\t Format of alignment file to use.");
            System.out.println("    -outputfmt tex|svg \t\t\t\t\t\t\t Format for output.");
            System.out.println("    -in <filename> \t\t\t\t\t\t\t Filename for alignment file.");
            System.out.println("    -outdir <directory> \t\t\t\t\t\t Output directory - where the diagrams go.");
            System.out.println("    -out <prefix> \t\t\t\t\t\t\t Prefix for output file.");
            //System.out.println("    -maxtargets <int>");
            //System.out.println("    -maxcoverage <int>");
            System.out.println("    -tsizes <int> \t\t\t\t\t\t\t The sizes of target contigs. To be used when inputfmt is sam and values are not provided "
                                + " in the header of the SAM file.");
            System.out.println("    -blastfmt <format string> \t\t\t\t\t\t The format of blast file. To be used when inputfmt is blast and format of "
                                + " blast file is user-defined.");
            System.out.println("    -alignmentQueryName <query sequence> \t\t\t\t Name of query sequence for detailed contig alignment diagram.");
            System.out.println("    -alignmentTargetName <target sequence> \t\t\t\t Name of target sequence for detailed contig alignment diagram.");
            System.out.println("    -coverageType square|long \t\t\t\t\t\t Type of coverage map to draw for coveragemap diagram.");
            System.out.println("    -binSize <int> \t\t\t\t\t\t\t Size of bin for coveragemap and genomecoverage diagrams.");
            System.out.println("    -filter \t\t\t\t\t\t\t\t Filter small alignments.");
            System.out.println("    -chimeras \t\t\t\t\t\t\t\t Only draw potentially chimeric contigs in contig alignment diagram.");
            System.out.println("    -printChimeras \t\t\t\t\t\t\t Output a file containing coordinate information for chimeric contigs.");
            System.out.println("");
            System.exit(1);
        }
        
        while (i < (args.length)) {
            if (args[i].equalsIgnoreCase("-type")) {
                diagramType = args[i+1].toLowerCase();
                if((!diagramType.equals("coveragemap")) &&
                   (!diagramType.equals("alignment")) &&
                   (!diagramType.equals("contigalignment")) &&
                   (!diagramType.equals("genomecoverage")) &&
                   (!diagramType.equals("all"))) {
                    System.out.println("Error: type must be 'coveragemap', 'contigalignment','alignment' or 'genomecoverage'.");
                    System.exit(0);
                }
                                
                System.out.println("    Diagram type: " + diagramType);
            } else if (args[i].equalsIgnoreCase("-inputfmt")) {
                inputFormat = args[i+1].toLowerCase();
                if ((!inputFormat.equals("psl")) &&
                    (!inputFormat.equals("coords")) &&
                    (!inputFormat.equals("pileup")) &&
                    (!inputFormat.equals("sam")) &&
                    (!inputFormat.equals("paf")) &&
                    (!inputFormat.equals("blast")) &&
                    (!inputFormat.equals("tiling"))) {
                    System.out.println("Error: inputfmt must be 'psl', 'coords', 'pileup', 'sam', 'paf', 'blast' or 'tiling'.");
                    System.exit(0);
                } 
            } else if (args[i].equalsIgnoreCase("-outputfmt")) {
                outputFormat = args[i+1].toLowerCase();
                if ((!outputFormat.equals("tex")) &&
                    (!outputFormat.equals("svg"))) {
                    System.out.println("Error: outputfmt must be 'tex' or 'svg'.");
                    System.exit(0);
                }
            } else if (args[i].equalsIgnoreCase("-in")) {
                inputFilename = args[i+1];
                System.out.println("  Input filename: "+inputFilename);
            } else if (args[i].equalsIgnoreCase("-list")) {
                listFilename = args[i+1];
                System.out.println("   List filename: "+listFilename);
            } else if (args[i].equalsIgnoreCase("-domains")) {
                domainsFilename = args[i+1];
                System.out.println("Domains filename: "+domainsFilename);
            } else if (args[i].equalsIgnoreCase("-outdir")) {                
                outputDirectory = args[i+1];
                if(!outputDirectory.endsWith(File.separator))
                {
                    outputDirectory = outputDirectory + File.separator;
                }
                System.out.println("Output directory: "+outputDirectory);
            } else if (args[i].equalsIgnoreCase("-out")) {                
                outputFilename = args[i+1];
                // remove .tex ending
                if (outputFilename.endsWith(".tex") || outputFilename.endsWith(".Tex") || outputFilename.endsWith(".TEX")) {
                    outputFilename = outputFilename.substring(0, outputFilename.length() - 4);
                }              
                System.out.println(" Output filename: "+outputFilename);
            } else if (args[i].equalsIgnoreCase("-maxtargets")) {
                maxTargets = Integer.parseInt(args[i+1]);
                System.out.println("     Max targets: " + maxTargets);
                
            } else if (args[i].equalsIgnoreCase("-maxcoverage")) {
                maxCoverage = Integer.parseInt(args[i+1]);
                System.out.println("    Max coverage: " + maxCoverage);
            } else if (args[i].equalsIgnoreCase("-tsizes")) {
                tSizes = (args[i+1]);
                System.out.println("     Target size: " + tSizes);
            } else if (args[i].equalsIgnoreCase("-rowheight")) {
                rowHeight = Integer.parseInt(args[i+1]);
                System.out.println("      Row height: " + rowHeight);
            } else if (args[i].equalsIgnoreCase("-rowspacer")) {
                rowSpacer = Integer.parseInt(args[i+1]);
                System.out.println("      Row spacer: " + rowSpacer);
            } else if (args[i].equalsIgnoreCase("-minAlignmentProp")) {
                minContigAlignmentProp = Double.parseDouble(args[i+1]);
                System.out.println("      Min alignment proportion for filtering: " + minContigAlignmentProp);
            } else if (args[i].equalsIgnoreCase("-alignmentQueryName")) {
                alignmentQueryName = args[i+1];
                System.out.println("   Query name for alignment diagram: " + alignmentQueryName);
            } else if (args[i].equalsIgnoreCase("-alignmentTargetName")) {
                alignmentRefName = args[i+1];
                System.out.println("   Target name for alignment diagram: " + alignmentRefName);
            } else if (args[i].equalsIgnoreCase("-binsize")) {
                binSize = Integer.parseInt(args[i+1]);
                System.out.println("   Bin size: " + binSize);
            } else if (args[i].equalsIgnoreCase("-blastfmt")) {
                blastFormatString = args[i+1];
                System.out.println("Format string for blastfile: " + blastFormatString);
            } else if(args[i].equalsIgnoreCase("-filter")) {
                filter = true;
                System.out.println("Filtering enabled.");
            } else if(args[i].equalsIgnoreCase("-chimeras")) {
                findChimeras = true;
                System.out.println("Looking for Chimeric reads only.");
            } else if(args[i].equalsIgnoreCase("-printchimeras")) {
                printChimeras = true;
            } else if(args[i].equalsIgnoreCase("-coverageType")) {
                if(args[i+1].equalsIgnoreCase("square")) {
                    cmiType = CoverageMapImage.Type.SQUARE_MAP;
                } else if(args[i+1].equalsIgnoreCase("long")) {
                    cmiType = CoverageMapImage.Type.LONG_MAP;
                } else {
                    System.out.println("Did not recognise coverage type: " + args[i+1]);
                }
            }          
            i++;
        }
        
        if (inputFormat == null) {
            System.out.println("Error: You must specify an input format");
            System.exit(0);
        }

        if (outputFormat == null) {
            System.out.println("Error: You must specify an output format");
            System.exit(0);
        }  
        
        if (inputFormat.equals("sam")) {
            if (!(diagramType.equals("coveragemap") || diagramType.equals("genomecoveragemap"))) {
                System.err.println("Error: SAM files can only produce coverage maps");
            }
            System.exit(0);
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
            File f = new File(outputDirectory);
            if(!f.isDirectory())
            {
                System.out.println("Error: outdir " + outputDirectory + " does not exist.");
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

    public String getDomainsFilename() {
        return domainsFilename;
    }
    
    public int getMaxCoverage() {
        return maxCoverage;
    }
    
    public String getTargetSizes() {
        return tSizes;
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
    
    public String getOutputFilePath() {
        return outputDirectory + outputFilename;
    }
    
    public boolean isNewHeatMapForEachContig() {
        return newHeatMapForEachContig;
    }
    
    public int getRowHeight() {
        return rowHeight;
    }
    
    public int getRowSpacer() {
        return rowSpacer;
    }
    
    public double getMinAlignmentProp() {
        return minContigAlignmentProp;
    }
    
    public String getAlignmentQueryName() {
        return alignmentQueryName;
    }
    
    public String getAlignmentRefName() {
        return alignmentRefName;
    }
    
    public CoverageMapImage.Type getCoverageMapImageType() {
        return cmiType;
    }
    
    public String getOutputFormat() {
        return outputFormat;
    }
    
    public int getBinSize() {
        return binSize;
    }
    
    public String getBlastFormatString() {
        return blastFormatString;
    }
    
    public boolean getFilter() {
        return filter;
    }
    
    public boolean getFindChimeras() {
        return findChimeras;
    }
    
    public boolean getPrintChimeras() {
        return printChimeras;
    }
}
