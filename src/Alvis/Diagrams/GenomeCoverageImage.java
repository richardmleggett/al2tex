// Alvis
//
// Alignment Diagrams in LaTeX and SVG
//
// Copyright 2018 Richard Leggett, Samuel Martin
// samuel.martin@earlham.ac.uk
// 
// This is free software, supplied without warranty.

package Alvis.Diagrams;

import Alvis.AlignmentFiles.Alignment;
import Alvis.DiagramOptions;
import java.io.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import java.util.*;

/**
 *
 * @author martins
 */
public class GenomeCoverageImage 
{
    static final private int ROWS_BETWEEN_TARGETS = 20;
    
    private final String m_outputFilename;
    private int m_highestCoverage;
    private final HashMap<String, int[]> m_coverages;
    private final HashMap<String, Integer> m_contigLengths;
    private final DiagramOptions m_options;
    private final HeatMapScale m_heatMapScale;
    private int m_height;
    private int m_width;
    private int m_binSize;
    
    public GenomeCoverageImage(DiagramOptions options, String outputFilename) 
    {
        m_options = options;
        m_coverages = new HashMap();
        m_contigLengths = new HashMap();
        m_heatMapScale = new HeatMapScale();
        m_outputFilename = outputFilename;
        m_highestCoverage = 0;
        m_binSize = m_options.getBinSize();
    }
    
    public void addAlignment(Alignment a) 
    {
        String targetName = a.getTargetName();
        if(!m_contigLengths.containsKey(targetName))
        {
            m_contigLengths.put(targetName, a.getTargetSize());
        }
        if(!m_coverages.containsKey(targetName))
        {
            int targetSize = a.getTargetSize() / m_binSize;
            if(a.getTargetSize() % m_binSize != 0)
            {
                targetSize++;
            }
            m_coverages.put(targetName, new int[targetSize]);
        }
        int[] coverage = m_coverages.get(targetName);
        for (int b=0; b<a.getBlockCount(); b++) 
        {
            int from = a.getBlockTargetStart(b);
            int to = from + a.getBlockSize(b);
            for (int i=from; i<to; i++) 
            {
                int coverageIndex = i/m_binSize;
                try
                {
                    coverage[coverageIndex]++;               
                    if (coverage[coverageIndex] > m_highestCoverage) 
                    {
                        m_highestCoverage = coverage[coverageIndex];
                    }
                }
                catch(ArrayIndexOutOfBoundsException e)
                {
                    System.out.println("WARNING: Out of bounds alignment for target " + a.getTargetName() + ":  " + i + "/" + a.getTargetSize());
                    break;
                }
            }
        }
    } 
    
    public void saveImageFile() 
    {
        m_heatMapScale.setHeatMapSize(m_highestCoverage);
        m_heatMapScale.setHeatMapDrawHeight(15);
        m_heatMapScale.setHeatMapDrawLength(150);
        m_heatMapScale.saveHeatmap(m_options.getOutputDirectory() + "images/genome_coverage_heatmap_scale.png");
        
        m_width = 8000;
        Object[] targetNames = m_coverages.keySet().toArray();
        Arrays.sort(targetNames);
        
        // calculate number of rows
        int nRows = 0;
        for(Object key: targetNames)
        {
            int rowsForTarget = (m_coverages.get(key.toString()).length) / m_width;
            nRows += rowsForTarget;
            // add the last row. Is it even worth checking this condition?
            if(m_coverages.get(key.toString()).length % m_width != 0)
            {
                nRows++;
            }
            nRows += ROWS_BETWEEN_TARGETS;
        }
        // get rid of the last gap
        nRows -= ROWS_BETWEEN_TARGETS;
        
        m_height = nRows * (m_options.getRowHeight() + m_options.getRowSpacer());
        
        System.out.println("Width " + m_width + " Height " + m_height);
        BufferedImage bImage = new BufferedImage(m_width, m_height, BufferedImage.TYPE_INT_RGB);
        
        Graphics g=bImage.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, m_width, m_height);
        
        int cumulativeTotal = 0;
        for(Object key: targetNames)
        {
            int[] coverage = m_coverages.get(key.toString());
            for(int i = 0; i < coverage.length; i++)
            {
                if (coverage[i] >= 0) 
                {
                    int offset = i;
                    int yo = offset / m_width;
                    int x = offset % m_width;
                   
                    yo += cumulativeTotal / m_width;
                    yo = yo * (m_options.getRowHeight() + m_options.getRowSpacer());
                    
                    for (int y=0; y<m_options.getRowHeight(); y++) 
                    {
                        int pixelColour = coverage[i] == 0 ? Color.GRAY.getRGB() : m_heatMapScale.getRGBColour(coverage[i]);
                        bImage.setRGB(x, yo + y, pixelColour);
                    }
                }
            }
            cumulativeTotal += coverage.length;
            cumulativeTotal += ROWS_BETWEEN_TARGETS * m_width;
        }
             
        try 
        {
            ImageIO.write(bImage, "PNG", new File(m_outputFilename));
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
    
    public HeatMapScale getScale()
    {
        return m_heatMapScale;
    }
    
    public String getFilename()
    {
        return m_outputFilename;
    }
    
    public int getHeight()
    {
        return m_height;
    }
    
    public int getWidth()
    {
        return m_width;
    }
    
    public HashMap<String, int[]> getCoverages()
    {
        return m_coverages;
    }
    
    public int getRowsBetweenTargets()
    {
        return ROWS_BETWEEN_TARGETS;
    }
    
    public int getTargetSize(String targetName)
    {
        if(m_contigLengths.containsKey(targetName))
        {
            return m_contigLengths.get(targetName);
        }
        else
        {
            return -1;
        }
    }
}
