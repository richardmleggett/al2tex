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

public class PSLAlignmentDiagram {
    private static final int NUM_DIVIDERS = 8;
    private PSLFile alignmentFile;
    private DiagramOptions options;
    private int targetCounter = 0;
 
   public PSLAlignmentDiagram(DiagramOptions o, PSLFile f) {
        options = o;
        alignmentFile = f;
        
        f.sortByTargetStart();
   }
 
   public void writeTexFile(String filename) {
        String previousTarget = new String("");
        String finalTarget = alignmentFile.getAlignment(alignmentFile.getNumberOfAlignments() - 1).getTargetName();
        TexFileWriter tfw = new TexFileWriter(filename + "_alignment.tex");
        int page = 1;
        int row = 0;

        tfw.openFile();

        // Go through alignments
        for (int i=0; i<alignmentFile.getNumberOfAlignments(); i++) {
            PSLAlignment a = alignmentFile.getAlignment(i);            

            row++;

            // Look for different target - in which case, start a new page
            if ((i > 0) && (a.getTargetName().compareTo(previousTarget) != 0)) {
                row = 1;
                page++;
                targetCounter++;

                if ((options.getMaxTargets() > 0) && (targetCounter >= options.getMaxTargets())) {
                    break;
                }
            }
            // We put the reference genome on the top of every page
            else if (row > tfw.getRowsPerPage()) {
                row = 1;
                page++;
            }

            if (row == 1) {
                if (page > 1) {
                    tfw.closePicture();
                    tfw.outputGapBetweenPictures();
                }
                
                tfw.newTarget(a.getTargetSize(), alignmentFile.getTargetHitCount(a.getTargetName()));
                
                tfw.openPicture();
                tfw.drawDividers(NUM_DIVIDERS);
                String targetName = options.filterName(a.getTargetName());
                tfw.drawTargetBar(targetName);                
            }

            // Now parse each alignment

            // Output line representing the whole contig
            int contigStart = a.getTargetStart() - a.getQueryStart();
            int contigEnd = a.getTargetEnd() + (a.getQuerySize() - a.getQueryEnd());
            tfw.outputAlignmentLine(contigStart, contigEnd);

            // Now output the blocks
            for (int j=0; j<a.getBlockCount(); j++) {        
                int from = a.getBlockTargetStart(j);
                int to = from + a.getBlockSize(j);

                if (from < a.getTargetStart()) {
                    System.out.println("Something went wrong - from < tStart");
                    System.exit(-1);
                }

                if (to > a.getTargetEnd()) {
                    System.out.println("Something went wrong - to ("+to+") > tEnd ("+a.getTargetEnd()+") with from ("+from+")");
                    System.exit(-1);
                }

                tfw.outputAlignmentBox(from, to);
            }

            // Label the contig

            // Get rid of dodgy characters!
            String contigName = options.filterName(a.getQueryName()); 

            // Filter velvet contig names, if neccessary
            if (contigName.matches("NODE_(\\d+)_length_(\\S+)_cov_(\\S+)")) {
                String subs[] = contigName.split("_");
                contigName="NODE\\_"+subs[1];
            }
            tfw.outputContigLabel(contigName);
            
            if (i < (alignmentFile.getNumberOfAlignments() - 1)) {
                tfw.nextAlignment();
            }

            previousTarget = a.getTargetName();
        }

        tfw.closePicture();
        tfw.closeFile();
    }
}
