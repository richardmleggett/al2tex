package al2tex;

import java.io.*;

public class PileupCoverageDiagram {
    DiagramOptions options;
    PileupFile pileupFile;
    PileupImage pileupImage;
        
    public PileupCoverageDiagram(DiagramOptions o, PileupFile f) {
        options = o;
        pileupFile = f;
                
        pileupImage = new PileupImage(pileupFile.getTotalNumberOfContigs(),
                                      pileupFile.getNumberOfChromosomes(),
                                      pileupFile.getHighestPosition(),
                                      pileupFile.getHighestCoverage(),
                                      options);        
    }
    
    public void makeBitmaps() {
        int currentChromosome = -1;
        int chrStart = 0;
        int chrEnd = 0;
        int clCtr = 0;
        
        for (int i=0; i<pileupFile.getTotalNumberOfContigs(); i++) {
            PileupContig c = pileupFile.getContig(i);
            
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
                                    
            pileupImage.addContig(c);
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
