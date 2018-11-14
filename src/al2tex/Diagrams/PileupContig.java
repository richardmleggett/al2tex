package al2tex.Diagrams;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.util.regex.*;

public class PileupContig {
    String identifier;
    int chromosome;
    public final static int MAX_SIZE = 20000;
    int[] coverage = new int[MAX_SIZE];
    int highestCoverage = 0;
    int size = 0;
    
    public PileupContig(String id) {
        identifier = id;
        
        //System.out.println("New contig "+id);

        if (id.startsWith("chr")) {
            int u = id.indexOf('_');
            String c = id.substring(3, u);
            chromosome = Integer.parseInt(c);
            //System.out.println("Chromosome is "+chromosome);
        } else {
            chromosome = 0;
            //System.out.println("Invalid id: "+id);
            //System.exit(0);
        }
        
        for (int i=0; i<MAX_SIZE; i++) {
            coverage[i] = 0;
        }
    }
    
    public void addPositionInfo(int p, int c) {
        if (p >= MAX_SIZE) {
            System.out.println("Error: position "+p+" too high!");
            System.exit(0);
        }
        
        if (p > size) {
            size = p;
        }
        
        if (c > highestCoverage) {
            highestCoverage=c;
        }
                
        coverage[p] = c;
    }
    
    public void setSize(int s) {
        if (s < size) {
            System.out.println("Error: Size too small!");
            System.exit(0);
        }
        
        size = s;
    }
    
    public String getId() {
        return identifier;
    }
    
    public int getChromosome() {
        return chromosome;
    }
    
    public int getSize() {
        return size;
    }
    
    public int getCoverage(int p) {
        return coverage[p];
    }
    
    public int getHighestCoverage() {
        return highestCoverage;
    }
}
