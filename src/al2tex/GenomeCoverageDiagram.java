/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package al2tex;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author martins
 */
public class GenomeCoverageDiagram extends TikzDrawer
{
     private DiagramOptions options;
    private int pictureWidth = 160;                  // Width of picture
    private int targetWidth = 100;                   // Width of reference (target) bar, in mm
    private int imageOffset = 7;
    private int targetHeight = targetWidth;
    private int rowHeight = 2;                       // Height of bar, in mm
    private int yStep = (int)(rowHeight * 1.5);      // Allow for gap
    private int pictureHeight = (int)(rowHeight * 2.5);                // Calculated later, based on number of alignments
    private int largestCoverage = 0;
    private HeatMapScale heatMapScale = new HeatMapScale();   // Heat map colours
    private ArrayList<CoverageMapImage> coverageMaps = new ArrayList();
    private CoverageMapImage.Type mapType;
    
    public GenomeCoverageDiagram(DiagramOptions o) 
    {
        super(o.getOutputFilePath() + "_genomeCoverageMap.tex");
        options = o;
        mapType = options.getCoverageMapImageType();
    }
    
    public void makeBitmapsFromFile(AlignmentFile alignmentFile) 
    { 
        String outputDirectory = options.getOutputDirectory();
        File imagesDir = new File(outputDirectory+"/images");
        imagesDir.mkdir();

        String filenamePrefix = outputDirectory + "/images/";
        String filename = filenamePrefix + "genome_coverage_image.png";
        GenomeCoverageImage mapImage = new GenomeCoverageImage(options, filename); 
        
        for(int i = 0; i < alignmentFile.getNumberOfAlignments(); ++i)
        {
            Alignment a = alignmentFile.getAlignment(i);
            mapImage.addAlignment(a);
        }

        mapImage.saveImageFile();
    }
}
