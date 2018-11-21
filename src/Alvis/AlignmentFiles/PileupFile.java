package Alvis.AlignmentFiles;

import Alvis.Diagrams.PileupContig;
import java.util.*;
import java.io.*;
import java.lang.*;

public class PileupFile {
    public final static int MAX_CONTIGS=1024;
    public final static int MAX_CHROMOSOMES=50;
    private ArrayList<PileupContig> contigs = new ArrayList();
    private int[] nContigsPerChromosome = new int[MAX_CHROMOSOMES];
    private int highestCoverage = 0;
    private int highestPosition = 0;
    private int nChromosomes = 0;
    
    public PileupFile(String pileupFilename, String listFilename) {
        String line;

        loadLengthsFile(listFilename);        
        
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(pileupFilename));

            while ((line = br.readLine()) != null) {
                String[] fields = line.split("\t");
                String id = fields[0];
                int position = Integer.parseInt(fields[1]);
                int count = Integer.parseInt(fields[2]);
                PileupContig contig = getContigById(id);
                
                if (contig == null) {
                    System.out.println("Error: null contig");
                    System.exit(0);
                }
                               
                if (position >= PileupContig.MAX_SIZE) {
                    System.out.println("Error in " + line);
                }
                
                contig.addPositionInfo(position, count);
                
                if (count > highestCoverage) {
                    highestCoverage = count;
                }
                
                if (position > highestPosition) {
                    highestPosition = position;
                }                
            }                                             
        } catch (Exception ioe) {
            System.out.println("Exception:");
            System.out.println(ioe);
        }
    }
    
    public int getHighestCoverage() {
        return highestCoverage;
    }
    
    public int getHighestPosition() {
        return highestPosition;
    }
    
    public int getTotalNumberOfContigs() {
        return contigs.size();
    }
    
    public int getNumberOfContigs(int i) {
        System.out.println("Number of contigs for "+i+" is "+nContigsPerChromosome[i]);
        return nContigsPerChromosome[i];
    }

    public PileupContig getContig(int i) {
        return contigs.get(i);
    }
    
    public int getNumberOfChromosomes() {
        return nChromosomes;
    }

    public void loadLengthsFile(String filename) {
        String line;
        
        for (int i=0; i<MAX_CHROMOSOMES; i++) {
            nContigsPerChromosome[i] = 0;
        }        
        
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(filename));

            while ((line = br.readLine()) != null) {
                String[] fields = line.split("\t");
                PileupContig c = new PileupContig(fields[0]);
                c.setSize(Integer.parseInt(fields[1]));
                contigs.add(c);
                nContigsPerChromosome[c.getChromosome()]++;
                if (c.getChromosome() > nChromosomes) {
                    nChromosomes = c.getChromosome();
                }                
            }
            
            br.close();
        } catch (Exception ioe) {
            System.out.println("Exception:");
            System.out.println(ioe);
        }
        
        System.out.println(contigs.size() + " contigs read.");        
    }
    
    public PileupContig getContigById(String id) {
        PileupContig foundContig = null;
        for (int i=0; i<contigs.size(); i++) {
            PileupContig p = contigs.get(i);
            if (p.getId().equals(id)) {
                foundContig = p;
                break;
            }
        }
        
        return foundContig;
    }
}
