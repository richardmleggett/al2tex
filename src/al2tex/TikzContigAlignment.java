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
    
    void drawContig(ArrayList<PAFAlignment> alignments, int x, int y)
    {
        if(alignments.size() <= 0)
        {
            return;
        }
        assert(m_coloursSet);
        try 
        {
            String contigName = alignments.get(0).getQueryName();
            int contigLength = alignments.get(0).getQueryContigLength();
            
            // fill in the alignments
            int bpLength = alignments.get(0).getQueryContigLength();
            double alignmentHeight = (double)m_contigDrawHeight / alignments.size();
            for(int i = 0; i < alignments.size(); i++)
            {
                PAFAlignment alignment = alignments.get(i);
                assert(alignment.getQueryName().equals(contigName));
                double start = (double)alignment.getQueryStartPos() * m_contigDrawLength / bpLength;
                double end = (double)alignment.getQueryEndPos() * m_contigDrawLength / bpLength;
                    
                double refStartPC = 100 * (double)alignment.getRefStartPos() / alignment.getRefContigLength();
                double refEndPC = 100 * (double)alignment.getRefEndPos() / alignment.getRefContigLength();
                if(alignment.isReverseAlignment())
                {
                    double temp = refStartPC;
                    refStartPC = refEndPC;
                    refEndPC = temp;                            
                }
                
                String colour = m_coloursMap.get(alignment.getRefName());
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
            double delta = (double)m_contigDrawLength / m_coloursMap.size();
            for(Map.Entry<String, String> entry : m_coloursMap.entrySet())
            {
                double xPos = x + i * delta;
                double yPos = y;
                String colour = entry.getValue();
                String name = entry.getKey();
                bw.write("\\node (rect) at (" + xPos + "," + yPos + ") " + 
                            "[shade, left color=white, right color=" + colour + ", draw=" + colour + 
                            ", minimum width=50, minimum height=15, label=below:"+ name + "]{};");
                bw.newLine();
                i++;
            }
        }
        catch (IOException e) 
        {
            System.out.println(e);
        }
    }
    
    public void drawAlignmentDiagram(ArrayList<PAFAlignment> alignments, int x, int y)
    {
        if(alignments.size() <= 0)
        {
            return;
        }
        drawContig(alignments, x, y);
        String refName = alignments.get(0).getRefName();
        int refLength = alignments.get(0).getRefContigLength();
        
        int refContigYStart = y - 2 * 175; // TODO: Magic numbers
        int refContigYEnd = refContigYStart + m_refContigDrawHeight;
        
        drawReferenceContig(0, refContigYStart, refName, 0, refLength, true);
        int minRef = refLength;
        int maxRef = 0;        
        for(int i = 0; i < alignments.size(); i++)
        {
            PAFAlignment alignment = alignments.get(i);
            if(alignment.getRefStartPos() < minRef)
            {
                minRef = alignment.getRefStartPos();
            }
            if(alignment.getRefEndPos() > maxRef)
            {
                maxRef = alignment.getRefEndPos();
            }               
        }
        int zoomRefContigYStart = refContigYStart + 100;
        int zoomRefContigYEnd = zoomRefContigYStart + m_refContigDrawHeight;
                
        drawReferenceContig(0, zoomRefContigYStart, refName, minRef, maxRef, false);
        
        try
        {
            // draw the "zoom lines"
            double minRefX = (double)minRef * m_contigDrawLength / refLength;
            double maxRefX = (double)maxRef * m_contigDrawLength / refLength;
            bw.write("\\draw[dashed] ( 0," + zoomRefContigYStart + ") -- (" + minRefX + "," + refContigYEnd + ") node[anchor=north, yshift=-4ex] {" + minRef + "};");
            bw.newLine();
            bw.write("\\draw[dashed] (" + m_contigDrawLength + "," + zoomRefContigYStart + ") -- (" + maxRefX + "," + refContigYEnd + ") node[anchor=north, yshift=-4ex] {" + maxRef + "};");
            bw.newLine();
                    
            double zoomRefLength = maxRef - minRef;
            int queryLength = alignments.get(0).getQueryContigLength();
            for(int i = 0; i < alignments.size(); i++)
            {
                PAFAlignment alignment = alignments.get(i);
                double queryStart = x + (double)alignment.getQueryStartPos() * m_contigDrawLength / queryLength;
                double queryEnd = x + (double)alignment.getQueryEndPos() * m_contigDrawLength / queryLength;
                                
                // draw the alignments on the ref
                double refStart = x + (double)alignment.getRefStartPos() * m_contigDrawLength / refLength;
                double refEnd = x + (double)alignment.getRefEndPos() * m_contigDrawLength / refLength;
                
                String colour = m_coloursMap.get(alignment.getRefName());
                double refStartPC = 100 * (double)alignment.getRefStartPos() / alignment.getRefContigLength();
                double refEndPC = 100 * (double)alignment.getRefEndPos() / alignment.getRefContigLength();
                bw.write(  "\\shade[draw, "
                                + "left color=" + colour + "!" + (int)refStartPC + "!white,"
                                + "right color="+ colour + "!" + (int)refEndPC + "!white] "
                                + "(" + refStart + "," + refContigYStart + ") rectangle "
                                + "(" + refEnd + "," + refContigYEnd + ");");
                bw.newLine();
                
                //draw the alignments on the zoomed ref
                double zoomRefStart = x + (double)(alignment.getRefStartPos() - minRef) * m_contigDrawLength / zoomRefLength;
                double zoomRefEnd = x + (double)(alignment.getRefEndPos() - minRef) * m_contigDrawLength / zoomRefLength;
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
                PAFAlignment alignment = alignments.get(i);
                double refStart = x + (double)(alignment.getRefStartPos() - minRef) * m_contigDrawLength / zoomRefLength;
                double refEnd = x + (double)(alignment.getRefEndPos() - minRef) * m_contigDrawLength / zoomRefLength;
                bw.write("\\draw (" + refStart + "," + zoomRefContigYStart + ") -- (" + refStart + "," + zoomRefContigYEnd + ");");
                bw.write("\\draw (" + refEnd + "," + zoomRefContigYStart + ") -- (" + refEnd + "," + zoomRefContigYEnd + ");");       
            }
        }
        catch (IOException e) 
        {
            System.out.println(e);
        }
    }
    
    public void drawReferenceContig(int x, int y, String refName, int refStart, int refEnd, boolean drawLabels)
    {
        try
        {
            // draw the contig rectangle
            String startBPLabel = drawLabels ?  "node[anchor=north] {" + refStart + "}" : "";
            String endBPLabel = drawLabels ?  "node[anchor=north] {" + refEnd + "}" : "";
            bw.write("\\draw (" + x + "," + y + ") " + startBPLabel + " -- " + 
                    "(" + (x + m_contigDrawLength) +","+ y + ") " + endBPLabel + " -- " +
                    "(" + (x + m_contigDrawLength) + "," + (y + m_refContigDrawHeight) + ") -- " +
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
}
