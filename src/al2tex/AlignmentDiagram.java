/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package al2tex;

import java.io.IOException;
import java.util.Comparator;

import al2tex.AlignmentFilters.*;

/**
 *
 * @author martins
 */
public class AlignmentDiagram {
    
    private static final int NUM_DIVIDERS = 8;
    private static final int MAX_OVERHANG = 200;
    
    private DiagramOptions options;
    private int targetCounter = 0;
    private DetailedAlignmentFile m_alignmentFile;
    private Drawer m_drawer;
    
    private int m_targetWidth = 1800;                                   // Width of reference (target) bar, in mm
    private int m_targetHeight = 20;
    private int m_yStep = (int)((float)m_targetHeight * 1.5);           // Allow for gap
    private int m_rowsPerPage = (1400 - (2 * m_yStep)) / m_yStep;
    private int m_targetSize;
    private float m_unit;
    private int m_y;
    private int m_pictureHeight; 
    private int m_xOffset = 0;
    private int m_yOffset = 0;
 
   public AlignmentDiagram(DiagramOptions o, DetailedAlignmentFile f) 
   {
        options = o;
        m_alignmentFile = f;
        String filename = options.getOutputFilePath() + "_alignmentDiagram";
        
        if(options.getOutputFormat().equals("tex"))
        {
            m_drawer = new TikzDrawer(filename, true);
        }
        else
        {
            m_drawer = new SVGDrawer(filename, true, 1, 2408, 1580);
            m_xOffset = 50;
            m_yOffset = 50;
        }
        
        if(o.getFilter())
        {
            f.filterAlignments(new MinPropFilter(0.005));
        }
        f.sortAlignments(compareForAlignmentDiagram);
   }
 
  public void writeOutputFile(String filename) 
  {
        String previousTarget = new String("");
        //TexFileWriter tfw = new TexFileWriter(filename + "_alignment");
        int page = 1;
        int row = 0;

        m_drawer.openFile();
        //tfw.openFile();

        // Go through alignments
        for (int i=0; i<m_alignmentFile.getNumberOfAlignments(); i++) 
        {
            DetailedAlignment a = m_alignmentFile.getAlignment(i);            
            row++;

            // Look for different target - in which case, start a new page
            if ((i > 0) && (a.getTargetName().compareTo(previousTarget) != 0)) 
            {
                row = 1;
                page++;
                targetCounter++;

                if ((options.getMaxTargets() > 0) && (targetCounter >= options.getMaxTargets())) 
                {
                    break;
                }
            }
            // We put the reference genome on the top of every page
            else if (row > m_rowsPerPage)//tfw.getRowsPerPage()) 
            {
                row = 1;
                page++;
            }

            if (row == 1) 
            {
                if (page > 1) 
                {
                    //tfw.closePicture();
                    //tfw.outputGapBetweenPictures();
                    m_drawer.closePicture();
                    m_drawer.drawVerticalGap(10);
                    m_drawer.newPage();
                }
                
                //tfw.newTarget(a.getTargetSize(), m_alignmentFile.getTargetHitCount(a.getTargetName()));
                newTarget(a.getTargetSize(), m_alignmentFile.getTargetHitCount(a.getTargetName()));
                
                //tfw.openPicture(0.1,0.1);
                m_drawer.openPicture(0.1, 0.1);
                
                //tfw.drawDividers(NUM_DIVIDERS);
                drawDividers(NUM_DIVIDERS);
                String targetName = options.filterName(a.getTargetName());
                
                //tfw.drawTargetBar(targetName);
                drawTargetBar(targetName);                
            }

            // Now parse each alignment

            // Output line representing the whole contig
            int contigStart = a.getTargetStart() - a.getQueryStart();
            int contigEnd = a.getTargetEnd() + (a.getQuerySize() - a.getQueryEnd());
            drawAlignmentLine(contigStart, contigEnd);
            //tfw.outputAlignmentLine(contigStart, contigEnd);

            // Now output the blocks
            for (int j=0; j<a.getBlockCount(); j++) 
            {        
                int from = a.getBlockTargetStart(j);
                int to = from + a.getBlockSize(j);

                if (from < a.getTargetStart()) 
                {
                    System.out.println("Something went wrong - from < tStart");
                    System.exit(-1);
                }

                if (to > a.getTargetEnd()) 
                {
                    System.out.println("Something went wrong - to ("+to+") > tEnd ("+a.getTargetEnd()+") with from ("+from+")");
                    System.exit(-1);
                }

                //tfw.outputAlignmentBox(from, to);
                drawAlignmentBox(from, to);
            }

            // Label the contig

            // Get rid of dodgy characters!
            String contigName = options.filterName(a.getQueryName()); 

            // Filter velvet contig names, if neccessary
            // this should probably be done by the alignmentfile object
            if (contigName.matches("NODE_(\\d+)_length_(\\S+)_cov_(\\S+)")) 
            {
                String subs[] = contigName.split("_");
                contigName="NODE\\_"+subs[1];
            }
            contigName = contigName.replace("\\_", "\\string_");
            
            double y = m_y - (m_targetHeight / 2);
            m_drawer.drawText(m_xOffset + m_targetWidth + 5, m_yOffset + y, contigName, Drawer.Anchor.ANCHOR_LEFT, "blue");
            //tfw.outputContigLabel(contigName);
            
            if (i < (m_alignmentFile.getNumberOfAlignments() - 1)) 
            {
                //tfw.nextAlignment();
                m_y -= m_yStep;
            }

            previousTarget = a.getTargetName();
        }

        //tfw.closePicture();
        //tfw.closeFile();
        m_drawer.closePicture();
        m_drawer.closeFile();
    }

    private void newTarget(int s, int h) 
    {
        m_targetSize = s;
        int targetHits = h;

        // Calculate unit length
        m_unit = ((float)m_targetWidth / (float)m_targetSize);                

        // Calculate picture height
        if (targetHits > m_rowsPerPage) 
        {
            m_pictureHeight = (m_rowsPerPage + 2) * m_yStep;
        } 
        else 
        {
            m_pictureHeight = (targetHits + 2) * m_yStep;
        }        

        m_y = m_pictureHeight;        
    }
    
    private void drawDividers(int n) 
    {
        for (int j=0; j<=n; j++) 
        {
            int num = (m_targetSize * j) / n;
            int xpos = (int)((float)num * m_unit);
            xpos += m_xOffset;
            double y1 = m_yOffset;
            double y2 = m_yOffset + m_pictureHeight - m_yStep;
            m_drawer.drawLine(xpos, y1, xpos, y2, "black", true);
            //bw.write("\\draw [dashed] ("+pos+", 0) -- ("+pos+","+(pictureHeight - yStep)+");"); bw.newLine();
            double y3 = m_yOffset + m_y-(m_targetHeight / 2);
            m_drawer.drawText(xpos, y3, Integer.toString(num), Drawer.Anchor.ANCHOR_MIDDLE, "black");
            //bw.write("\\node at ("+pos+","+(y-(targetHeight / 2))+") {"+num+"};"); bw.newLine();
        }
        m_y -= m_yStep;
    }
    
    private void drawTargetBar(String name) 
    {
        m_drawer.drawFilledRectangle(m_xOffset, m_yOffset + m_y - m_targetHeight, m_targetWidth, m_targetHeight, "red", "red");
        double texty = m_yOffset + m_y - (m_targetHeight / 2);
        m_drawer.drawText(m_xOffset + m_targetWidth + 5, texty, name, Drawer.Anchor.ANCHOR_LEFT, "red");
        m_y -= m_yStep;
    }
    
    private void drawAlignmentLine(int from, int to) 
    {
        float x1 = (float)from * m_unit;
        float x2 = (float)to * m_unit;
        float y1 = (float)m_y - ((float)m_targetHeight / 2);
        
        if (from > to) {
            System.out.println("Something went wrong - from > to!");
            System.exit(-1);
        }

        
        if (x1 < -MAX_OVERHANG) 
        {
            x1 = -MAX_OVERHANG;
            m_drawer.drawText(m_xOffset - MAX_OVERHANG-10, m_yOffset + y1, "+" + Integer.toString(-from), Drawer.Anchor.ANCHOR_MIDDLE, "black");
        }
        
        if (x2 > (m_targetWidth + MAX_OVERHANG)) 
        {
            x2 = m_targetWidth + MAX_OVERHANG;
            m_drawer.drawText(m_xOffset + m_targetWidth + MAX_OVERHANG + 10, m_yOffset + y1, "+" + Integer.toString(to - m_targetSize), Drawer.Anchor.ANCHOR_MIDDLE, "black");
        }
        
        m_drawer.drawLine(m_xOffset + x1, m_yOffset + y1, m_xOffset + x2, m_yOffset + y1, "black", false);
    }
    
    public void drawAlignmentBox(int from, int to) 
    {
        double x1 = (float)from * m_unit;
        double x2 = (float)to * m_unit;
        double y1 = m_y - m_targetHeight;
        
        double width = x2 - x1;
        double height = m_targetHeight;

        if (from > to) 
        {
            System.out.println("Something went wrong - from > to!");
            System.exit(-1);
        }
        
        m_drawer.drawFilledRectangle(m_xOffset + x1, m_yOffset + y1, width, height, "white", "blue");
    }
    
    public static Comparator compareForAlignmentDiagram = new Comparator<DetailedAlignment>(){
        public int compare(DetailedAlignment alignment1, DetailedAlignment alignment2) {
            int result = alignment1.getTargetName().compareTo(alignment2.getTargetName());
            if(result == 0)
            {
                result = alignment1.getQueryName().compareTo(alignment2.getQueryName());
                if(result == 0)
                {
                    result = alignment1.getTargetStart() - alignment2.getTargetStart();
                }
            }
            
            return result;
        }
    };
    
}