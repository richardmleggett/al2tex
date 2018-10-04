/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package al2tex;

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
    private final String m_outputFilename;
    private int m_highestCoverage;
    private final HashMap<String, int[]> m_coverages;
    private final DiagramOptions m_options;
    static final private int ROWS_BETWEEN_TARGETS = 50;
    static final private int ALIGNMENT_BIN_SIZE = 10;
    private final HeatMapScale m_heatMapScale;
    
    public GenomeCoverageImage(DiagramOptions options, String outputFilename) 
    {
        m_options = options;
        m_coverages = new HashMap();
        m_heatMapScale = new HeatMapScale();
        m_outputFilename = outputFilename;
        m_highestCoverage = 0;
    }
    
    public void addAlignment(Alignment a) 
    {
        String targetName = a.getTargetName();
        if(!m_coverages.containsKey(targetName))
        {
            int targetSize = a.getTargetSize() / ALIGNMENT_BIN_SIZE;
            if(a.getTargetSize() % ALIGNMENT_BIN_SIZE != 0)
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
                int coverageIndex = i/ALIGNMENT_BIN_SIZE;
                coverage[coverageIndex]++;               
                if (coverage[coverageIndex] > m_highestCoverage) 
                {
                    m_highestCoverage = coverage[coverageIndex];
                }
            }
        }
    } 
    
    public void saveImageFile() 
    {
        m_heatMapScale.setHeatMapSize(m_highestCoverage);
        m_heatMapScale.saveHeatmap(m_options.getOutputDirectory() + "/images/genome_coverage_heatmap_scale.png");
        
        int imageWidth = 8000;
        Object[] targetNames = m_coverages.keySet().toArray();
        Arrays.sort(targetNames);
        
        // calculate number of rows
        int nRows = 0;
        for(Object key: targetNames)
        {
            int rowsForTarget = (m_coverages.get(key.toString()).length) / imageWidth;
            nRows += rowsForTarget;
            // add the last row. Is it even worth checking this condition?
            if(m_coverages.get(key.toString()).length % imageWidth != 0)
            {
                nRows++;
            }
            nRows += ROWS_BETWEEN_TARGETS;
        }
        // get rid of the last gap
        nRows -= ROWS_BETWEEN_TARGETS;
        
        int imageHeight = nRows * (m_options.getRowHeight() + m_options.getRowSpacer());
        
        System.out.println("Width " + imageWidth + " Height " + imageHeight);
        BufferedImage bImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        
        Graphics g=bImage.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, imageWidth, imageHeight);
        
        int cumulativeTotal = 0;
        for(Object key: targetNames)
        {
            int[] coverage = m_coverages.get(key.toString());
            for(int i = 0; i < coverage.length; i++)
            {
                if (coverage[i] >= 0) 
                {
                    int offset = i;
                    int yo = offset / imageWidth;
                    int x = offset % imageWidth;
                   
                    yo += cumulativeTotal / imageWidth;
                    yo = yo * (m_options.getRowHeight() + m_options.getRowSpacer());
                    
                    for (int y=0; y<m_options.getRowHeight(); y++) 
                    {
                        int pixelColour = coverage[i] == 0 ? Color.GRAY.getRGB() : m_heatMapScale.getRGBColour(coverage[i]);
                        bImage.setRGB(x, yo + y, pixelColour);
                    }
                }
            }
            cumulativeTotal += coverage.length;
            cumulativeTotal += ROWS_BETWEEN_TARGETS * imageWidth;
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
}
