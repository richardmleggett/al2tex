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

public class MummerAlignmentDiagram {
    private static final int NUM_DIVIDERS = 8;
    private MummerFile alignmentFile;
    private DiagramOptions options;
    private int targetCounter = 0;
 
   public MummerAlignmentDiagram(DiagramOptions o, MummerFile f) {
        options = o;
        alignmentFile = f;
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
            MummerAlignment a = alignmentFile.getAlignment(i);            

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
                tfw.drawTargetBar(a.getTargetName());
            }

            // Now parse each alignment

            // Output line representing the whole contig
            int contigStart = a.getTargetStart() - a.getQueryStart();
            int contigEnd = a.getTargetEnd() + (a.getQuerySize() - a.getQueryEnd());
            tfw.outputAlignmentLine(contigStart, contigEnd);

            System.out.println("Line from "+contigStart+" to "+contigEnd);
            
            // Now output the blocks
            int from = a.getTargetStart() -1;
            int to = a.getTargetEnd();
            tfw.outputAlignmentBox(from, to);
            
            System.out.println("Box from "+from +" to "+to);

            // Label the contig
            // Filter velvet contig names, if neccessary
            String contigName = a.getQueryName();
            if (contigName.matches("NODE_(\\d+)_length_(\\S+)_cov_(\\S+)")) {
                String subs[] = contigName.split("_");
                contigName="NODE\\_"+subs[1];
            }
            tfw.outputContigLabel(contigName);
            tfw.nextLine();
            
            previousTarget = a.getTargetName();
        }

        tfw.closePicture();
        tfw.closeFile();

    }
}
