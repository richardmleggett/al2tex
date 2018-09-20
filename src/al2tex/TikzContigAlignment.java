/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package al2tex;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map;
import java.awt.Color;


/**
 *
 * @author martins
 */
public class TikzContigAlignment extends TikzPicture
{
    private int m_contigDrawLength;
    private int m_contigDrawHeight;
    private int m_refContigDrawHeight;
    private int m_numKeysPerLine;
    private int m_minYStart;
    private TreeMap<String, String> m_coloursMap;
    private int m_colourCounter;
    private boolean m_coloursSet;
    
    public TikzContigAlignment(String f)
    {
        super(f);
        m_contigDrawLength = 2000;
        m_contigDrawHeight = 100;
        m_refContigDrawHeight = 50;
        m_coloursMap = new TreeMap();
        m_coloursSet = false;
        m_colourCounter = 0;
        m_numKeysPerLine = 7;
        m_minYStart = 0;
    }
    
    public void setColourForContig(String refContig, Color colour)
    {
        if(m_coloursMap.keySet().contains(refContig))
        {
            System.out.print("Already set colour for contig " + refContig + ".\n");
            return;
        }
        m_coloursMap.put(refContig, "colour_" + m_colourCounter);
        try 
        {
            
            int red = colour.getRed();
            int green = colour.getGreen();
            int blue = colour.getBlue();
            bw.write("\\definecolor{colour_" + m_colourCounter + "}{RGB}{" + red + "," + green + "," + blue + "}");
            bw.newLine();
        }
        catch (IOException e) 
        {
            System.out.println(e);
        }
        m_colourCounter++;
        m_coloursSet = true;
    }
    
    void drawContig(ArrayList<DetailedAlignment> alignments, int x, int y)
    {
        if(alignments.size() <= 0)
        {
            return;
        }
        assert(m_coloursSet);
        try 
        {
            String contigName = alignments.get(0).getQueryName().replace("_", "\\_");
            int contigLength = alignments.get(0).getQuerySize();
            
            // fill in the alignments
            //int bpLength = alignments.get(0).getQueryContigLength();
            double alignmentHeight = (double)m_contigDrawHeight / alignments.size();
            for(int i = 0; i < alignments.size(); i++)
            {
                DetailedAlignment alignment = alignments.get(i);
                assert(alignment.getQueryName().equals(contigName));
                double start = (double)alignment.getQueryStart() * m_contigDrawLength / contigLength;
                double end = (double)alignment.getQueryEnd() * m_contigDrawLength / contigLength;
                    
                double refStartPC = 100 * (double)alignment.getTargetStart() / alignment.getTargetSize();
                double refEndPC = 100 * (double)alignment.getTargetEnd() / alignment.getTargetSize();
                if(alignment.isReverseAlignment())
                {
                    double temp = refStartPC;
                    refStartPC = refEndPC;
                    refEndPC = temp;                            
                }
                
                String colour = m_coloursMap.get(alignment.getTargetName());
                bw.write(  "\\shade[draw=" + colour + ", thick, "
                                + "left color=" + colour + "!" + (int)refStartPC + "!white,"
                                + "right color="+ colour + "!" + (int)refEndPC + "!white] "
                                + "(" + (x + start) + "," + (y + (i * alignmentHeight)) + ") rectangle "
                                + "(" + (x + end) + "," + (y + ((i+1) * alignmentHeight)) + ");");
                bw.newLine();
            }
            
            // draw the contig rectangle
            bw.write("\\draw (" + x + "," + y + ") node[anchor=north] {0} -- " + 
                    "(" + (x + m_contigDrawLength) +","+ y + ") node[anchor=north] {" + contigLength + "} -- " +
                    "(" + (x + m_contigDrawLength) + "," + (y + m_contigDrawHeight) + ") -- " +
                    "(" + x + "," + (y + m_contigDrawHeight) + ") -- cycle ;");
            bw.newLine();
            bw.write("\\node at (" + x + "," + (y + m_contigDrawHeight/2) + ") [anchor=east] {" + contigName + "};");
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }
    }
    
    public void drawKey(int x, int y)
    {
        assert(m_coloursSet);
        // TODO: do this in a sensible way, based on how many reference sequences there are.
        try 
        {
            int i = 0;
            int j = -1;
            double delta = (double)m_contigDrawLength / m_numKeysPerLine;
            for(Map.Entry<String, String> entry : m_coloursMap.entrySet())
            {
                if(i % m_numKeysPerLine == 0)
                {
                    i = 0;
                    j++;
                }
                                
                double xPos = x + i * delta;
                double yPos = y + j * m_contigDrawHeight;
                String colour = entry.getValue();
                String name = entry.getKey().replace("_", "\\_");
                bw.write("\\node (rect) at (" + xPos + "," + yPos + ") " + 
                            "[shade, left color=white, right color=" + colour + ", draw=" + colour + 
                            ", minimum width=50, minimum height=15, label=below:"+ name + "]{};");
                bw.newLine();
                i++;
            }
            
            m_minYStart = y + (j+1) * m_contigDrawHeight;
        }
        catch (IOException e) 
        {
            System.out.println(e);
        }
    }
    
    public void drawAlignmentDiagram(ArrayList<DetailedAlignment> alignments, int x, int y)
    {
        if(alignments.size() <= 0)
        {
            return;
        }
        drawContig(alignments, x, y);
        String refName = alignments.get(0).getTargetName();
        int refLength = alignments.get(0).getTargetSize();
        
        int refContigYStart = y - 3 * 150; // TODO: Magic numbers
        int refContigYEnd = refContigYStart + m_refContigDrawHeight;
        
        double refLengthProp = 0.7;
        int refContigXStart = (int)((1-refLengthProp) * m_contigDrawLength * 0.5);
        drawReferenceContig(refContigXStart, refContigYStart, refName, 0, refLength, true, refLengthProp);
        int minRef = refLength;
        int maxRef = 0;        
        for(int i = 0; i < alignments.size(); i++)
        {
            DetailedAlignment alignment = alignments.get(i);
            if(alignment.getTargetStart() < minRef)
            {
                minRef = alignment.getTargetStart();
            }
            if(alignment.getTargetEnd() > maxRef)
            {
                maxRef = alignment.getTargetEnd();
            }               
        }
        int zoomRefContigYStart = refContigYStart + 150;
        int zoomRefContigYEnd = zoomRefContigYStart + m_refContigDrawHeight;
                
        drawReferenceContig(x, zoomRefContigYStart, refName, minRef, maxRef, false, 1);
        
        try
        {
            // draw the "zoom lines"
            int refContigDrawLength = (int)(m_contigDrawLength * refLengthProp);
            double minRefX = (double)minRef * refContigDrawLength / refLength;
            double maxRefX = (double)maxRef * refContigDrawLength / refLength;
            bw.write("\\draw[dashed] ( 0," + zoomRefContigYStart + ") -- (" + (refContigXStart + minRefX) + "," + refContigYEnd + ") node[anchor=south, yshift=1ex] {" + minRef + "};");
            bw.newLine();
            bw.write("\\draw[dashed] (" + m_contigDrawLength + "," + zoomRefContigYStart + ") -- (" + (refContigXStart + maxRefX) + "," + refContigYEnd + ") node[anchor=south, yshift=1ex] {" + maxRef + "};");
            bw.newLine();
                    
            double zoomRefLength = maxRef - minRef;
            int queryLength = alignments.get(0).getQuerySize();
            for(int i = 0; i < alignments.size(); i++)
            {
                DetailedAlignment alignment = alignments.get(i);
                double queryStart = x + (double)alignment.getQueryStart() * m_contigDrawLength / queryLength;
                double queryEnd = x + (double)alignment.getQueryEnd() * m_contigDrawLength / queryLength;
                                
                // draw the alignments on the ref
                double refStart = refContigXStart + (double)alignment.getTargetStart() * refContigDrawLength / refLength;
                double refEnd = refContigXStart + (double)alignment.getTargetEnd() * refContigDrawLength / refLength;
                
                String colour = m_coloursMap.get(alignment.getTargetName());
                double refStartPC = 100 * (double)alignment.getTargetStart() / alignment.getTargetSize();
                double refEndPC = 100 * (double)alignment.getTargetEnd() / alignment.getTargetSize();
                bw.write(  "\\shade[draw, "
                                + "left color=" + colour + "!" + (int)refStartPC + "!white,"
                                + "right color="+ colour + "!" + (int)refEndPC + "!white] "
                                + "(" + refStart + "," + refContigYStart + ") rectangle "
                                + "(" + refEnd + "," + refContigYEnd + ");");
                bw.newLine();
                
                //draw the alignments on the zoomed ref
                double zoomRefStart = x + (double)(alignment.getTargetStart() - minRef) * m_contigDrawLength / zoomRefLength;
                double zoomRefEnd = x + (double)(alignment.getTargetEnd() - minRef) * m_contigDrawLength / zoomRefLength;
                bw.write(  "\\shade[draw, "
                                + "left color=" + colour + "!" + (int)refStartPC + "!white,"
                                + "right color="+ colour + "!" + (int)refEndPC + "!white] "
                                + "(" + zoomRefStart + "," + zoomRefContigYStart + ") rectangle "
                                + "(" + zoomRefEnd + "," + zoomRefContigYEnd + ");");
                bw.newLine();
                
                if(alignment.isReverseAlignment())
                {
                    double temp = zoomRefEnd;
                    zoomRefEnd = zoomRefStart;
                    zoomRefStart = temp;
                }
                double controlY = zoomRefContigYEnd + 50;
                // draw curves from query to ref
                bw.write("\\draw (" + queryStart + ", " + y + ") .. controls " + 
                            "(" + queryStart + ", " + controlY + ") and (" + zoomRefStart + ", " + controlY + ") .. " +
                            "(" + zoomRefStart + ", " + zoomRefContigYEnd + ");");
                bw.newLine();
                bw.write("\\draw (" + queryEnd + ", " + y + ") .. controls " + 
                            "(" + queryEnd + ", " + controlY + ") and (" + zoomRefEnd + ", " + controlY + ") .. " +
                            "(" + zoomRefEnd + ", " + zoomRefContigYEnd + ");");
                bw.newLine();
                
                // draw dashed lines on the query contig at the beginning and end of each alignment
                bw.write("\\draw[dashed] (" + queryStart + "," + y + ") -- (" + queryStart + "," + (y + m_contigDrawHeight) + ");");
                bw.newLine();
                bw.write("\\draw[dashed] (" + queryEnd + "," + y + ") -- (" + queryEnd + "," + (y + m_contigDrawHeight) + ");");
                bw.newLine();
            }
            
            // draw the outlines of the alignments on the ref again, because they might have been drawn over :(
            for(int i = 0; i < alignments.size(); i++)
            {
                DetailedAlignment alignment = alignments.get(i);
                double refStart = x + (double)(alignment.getTargetStart() - minRef) * m_contigDrawLength / zoomRefLength;
                double refEnd = x + (double)(alignment.getTargetEnd() - minRef) * m_contigDrawLength / zoomRefLength;
                bw.write("\\draw (" + refStart + "," + zoomRefContigYStart + ") -- (" + refStart + "," + zoomRefContigYEnd + ");");
                bw.write("\\draw (" + refEnd + "," + zoomRefContigYStart + ") -- (" + refEnd + "," + zoomRefContigYEnd + ");");       
            }
        }
        catch (IOException e) 
        {
            System.out.println(e);
        }
    }
    
    public void drawReferenceContig(int x, int y, String refName, int refStart, int refEnd, boolean drawLabels, double width)
    {
        try
        {
            assert(0. <= width && width <= 1.);
            double drawLength = m_contigDrawLength * width;
            // draw the contig rectangle
            String startBPLabel = drawLabels ?  "node[anchor=north] {" + refStart + "}" : "";
            String endBPLabel = drawLabels ?  "node[anchor=north] {" + refEnd + "}" : "";
            bw.write("\\draw (" + x + "," + y + ") " + startBPLabel + " -- " + 
                    "(" + (x + drawLength) +","+ y + ") " + endBPLabel + " -- " +
                    "(" + (x + drawLength) + "," + (y + m_refContigDrawHeight) + ") -- " +
                    "(" + x + "," + (y + m_refContigDrawHeight) + ") -- cycle ;");
            bw.newLine();
            if(drawLabels)
            {
                bw.write("\\node at (" + x + "," + (y + m_refContigDrawHeight/2) + ") [anchor=east] {" + refName + "};");
            }
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }
    }
    
    public int getContigDrawLength() { return m_contigDrawLength; }
    public int getContigDrawHeight() { return m_contigDrawHeight; }
    public int getMinYForContig() { return m_minYStart; }
}
