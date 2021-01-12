// Alvis
//
// Alignment Diagrams in LaTeX and SVG
//
// Copyright 2018 Richard Leggett, Samuel Martin
// samuel.martin@earlham.ac.uk
// 
// This is free software, supplied without warranty.

package Alvis;

import Alvis.AlignmentFiles.BlastFile;
import Alvis.AlignmentFiles.SAMFile;
import Alvis.AlignmentFiles.MummerAlignment;
import Alvis.AlignmentFiles.PSLFile;
import Alvis.AlignmentFiles.MummerFile;
import Alvis.AlignmentFiles.PileupFile;
import Alvis.AlignmentFiles.PAFFile;
import Alvis.AlignmentFiles.DetailedAlignmentFile;
import Alvis.Diagrams.CoverageMapDiagram;
import Alvis.Diagrams.GenomeCoverageDiagram;
import Alvis.Diagrams.AlignmentDiagram;
import Alvis.Diagrams.ContigAlignmentDiagram;
import Alvis.Diagrams.PileupCoverageDiagram;

public class Alvis 
{
    public static void main(String[] args) 
    {
        DiagramOptions options = new DiagramOptions();

        System.out.println("Alvis\n");
        options.parseArgs(args);
    
        if ((options.getInputFormat().equals("pileup")) &&
            (options.getDiagramType().equals("coverage"))) 
        {
            System.out.println("\nOpening pileup file");
            PileupFile pileupFile = new PileupFile(options.getInputFilename(), options.getListFilename());
            System.out.println("Building coverage diagram");
            PileupCoverageDiagram pileupCoverageDiagram = new PileupCoverageDiagram(options, pileupFile);
            if (options.getDomainsFilename() != null) 
            {
                System.out.println("Loading domain information");
                pileupCoverageDiagram.addDomainInfo(options.getDomainsFilename());
            }
            System.out.println("Making bitmaps");
            pileupCoverageDiagram.makeBitmaps();           
        } 
        else if (options.getInputFormat().equals("sam")) 
        {
            System.out.println("\nOpening SAM file");
            SAMFile samFile = new SAMFile(options.getInputFilename(), options.getTargetSizes());

            if ((options.getDiagramType().equals("coveragemap"))) 
            {
                System.out.println("Building coverage map diagram");
                CoverageMapDiagram coverageDiagram = new CoverageMapDiagram(options);
                System.out.println("Making bitmaps");
                coverageDiagram.makeBitmapsFromFile(samFile, options.getOutputDirectory());
                System.out.println("Writing " + options.getOutputFormat() + " files");
                coverageDiagram.writeOutputFile();
            }       
            else if((options.getDiagramType().equals("genomecoverage"))) 
            {
                System.out.println("Building genome coverage diagram");
                GenomeCoverageDiagram genomeCoverageDiagram = new GenomeCoverageDiagram(options);
                System.out.println("Writing output files");
                genomeCoverageDiagram.makeBitmapsFromFile(samFile);
                genomeCoverageDiagram.writeOutputFile();
            }
        }
        else 
        {
            DetailedAlignmentFile alignmentFile;
            switch(options.getInputFormat())
            {
                case "psl":
                {
                    System.out.println("\nOpening PSL file");
                    alignmentFile = new PSLFile(options.getInputFilename());
                    break;
                }
                case "coords":
                {
                    System.out.println("\nOpening coords file");
                    alignmentFile = new MummerFile(options.getInputFilename(), MummerAlignment.SHOW_COORDS);
                    break;
                }
                case "tiling":
                {
                    System.out.println("\nOpening tiling file");
                    alignmentFile = new MummerFile(options.getInputFilename(), MummerAlignment.SHOW_TILING);
                    break;                   
                }
                case "paf":
                {
                    System.out.println("\nOpening PAF file");
                    alignmentFile = new PAFFile(options.getInputFilename());
                    break;
                }
                case "blast":
                {
                    System.out.println("\nOpening BLAST file");
                    alignmentFile = new BlastFile(options);
                    break;
                }
                default:
                {
                    System.out.println("Did not recognise input format");
                    System.exit(2);
                    return;
                }
            }
            
            switch(options.getDiagramType())
            {
                case "coveragemap":
                {
                    System.out.println("Building coverage map diagram");
                    CoverageMapDiagram coverageDiagram = new CoverageMapDiagram(options);
                    System.out.println("Making bitmaps");
                    coverageDiagram.makeBitmapsFromFile(alignmentFile, options.getOutputDirectory());
                    System.out.println("Writing " + options.getOutputFormat() + " files");
                    coverageDiagram.writeOutputFile();
                    break;
                }
                case "alignment":
                {
                    System.out.println("Building alignment diagram");
                    AlignmentDiagram pslAlignmentDiagram = new AlignmentDiagram(options, alignmentFile);
                    System.out.println("Writing LaTeX files");
                    pslAlignmentDiagram.writeOutputFile(options.getOutputFilePath());
                    break;
                }
                case "contigalignment":
                {
                    System.out.println("Building contig alignment diagram");
                    ContigAlignmentDiagram contigAlignmentDiagram = new ContigAlignmentDiagram(alignmentFile, options);
                    System.out.println("Writing " + options.getOutputFormat() + " files");
                    contigAlignmentDiagram.writeOutputFile(options);
                    break;
                }
                case "genomecoverage":
                {
                    System.out.println("Building genome coverage diagram");
                    GenomeCoverageDiagram genomeCoverageDiagram = new GenomeCoverageDiagram(options);
                    System.out.println("Writing output files");
                    genomeCoverageDiagram.makeBitmapsFromFile(alignmentFile);
                    genomeCoverageDiagram.writeOutputFile();
                    break;
                }
                default:
                {
                    System.out.println("Did not recognise diagram type" + options.getDiagramType());
                    System.exit(2);
                    return;
                }
            }
        }
        System.out.println("Done");
    }
}