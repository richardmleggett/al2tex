// Alvis
//
// Alignment Diagrams in LaTeX and SVG
//
// Copyright 2018 Richard Leggett, Samuel Martin
// samuel.martin@earlham.ac.uk
// 
// This is free software, supplied without warranty.

package Alvis.Diagrams;

import Alvis.AlignmentFiles.PileupFile;
import Alvis.DiagramOptions;
import Alvis.DomainInfo;
import Alvis.DomainInfoFile;
import java.io.*;

public class PileupCoverageDiagram {
    DiagramOptions options;
    PileupFile pileupFile;
    PileupImage pileupImage;
    DomainInfoFile diFile = null;
        
    public PileupCoverageDiagram(DiagramOptions o, PileupFile f) {
        options = o;
        pileupFile = f;
                
        pileupImage = new PileupImage(pileupFile.getTotalNumberOfContigs(),
                                      pileupFile.getNumberOfChromosomes(),
                                      pileupFile.getHighestPosition(),
                                      pileupFile.getHighestCoverage(),
                                      options);        
    }
    
    public void addDomainInfo(String filename) {
        diFile = new DomainInfoFile(filename);
    }
    
    public void makeBitmaps() {
        int currentChromosome = -1;
        int chrStart = 0;
        int chrEnd = 0;
        int clCtr = 0;
        
        for (int i=0; i<pileupFile.getTotalNumberOfContigs(); i++) {
            PileupContig c = pileupFile.getContig(i);
            DomainInfo di = null;
            
            if (c.getChromosome() != currentChromosome) {
                if (currentChromosome != -1) {
                    pileupImage.labelSet("Chr "+Integer.toString(currentChromosome));
                }
                
                pileupImage.storeStart();
                
                if ((currentChromosome > 0) && (currentChromosome < pileupFile.getNumberOfChromosomes())) {
                    pileupImage.addChromosomeBreak();
                }
                
                currentChromosome = c.getChromosome();
            }
            
            if (diFile != null) {
                di = diFile.getDomainInfo(c.getId());
            }
                                    
            pileupImage.addContig(c, di);
        }

        if (currentChromosome != -1) {
            pileupImage.labelSet("Chr "+Integer.toString(currentChromosome));
        }    

        this.saveImage();    
    }

    public void saveImage() {
        System.out.println("Saving file "+options.getOutputFilename());
        pileupImage.saveImageFile(options.getOutputFilename());        
    }
}
