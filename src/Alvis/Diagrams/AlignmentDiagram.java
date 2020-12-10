// Alvis
//
// Alignment Diagrams in LaTeX and SVG
//
// Copyright 2018 Richard Leggett, Samuel Martin
// samuel.martin@earlham.ac.uk
// 
// This is free software, supplied without warranty.

package Alvis.Diagrams;

import Alvis.AlignmentFilters.*;
import Alvis.Drawers.SVGDrawer;
import Alvis.Drawers.TikzDrawer;
import Alvis.Drawers.Drawer;
import Alvis.AlignmentFiles.DetailedAlignment;
import Alvis.AlignmentFiles.DetailedAlignmentFile;
import Alvis.AlignmentFiles.AlignmentSorter;
import java.util.Comparator;

import Alvis.DiagramOptions;

import java.util.ArrayList;
/**
 *
 * @author martins
 */
public class AlignmentDiagram {
    
    private static final int NUM_DIVIDERS = 8;
    private static final int MAX_OVERHANG = 100;
    private static final int MAX_ALIGNMENTS_PER_PAGE = 43;
    
    private static DiagramOptions options;
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
    private boolean m_isTexOut = true;
    private boolean m_displayNames = true;
    
    public static enum SortMethod {
        SORT_BY_TARGET_POS,
        SORT_BY_QUERY_POS
    }; 
    
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
            m_drawer = new SVGDrawer(filename, true, 1, 2608, 1580, 200, 24);
            m_xOffset = 50;
            m_yOffset = 50;
            m_isTexOut = false;
        }
        
        if(o.getFilter())
        {
            m_alignmentFile.filterAlignments(new MinPropFilter(o.getMinAlignmentProp()));
        }
        m_alignmentFile.sort(new Sorter());
        
        if(options.getOutputFormat().equals("tex"))
        {
            int estimated_file_size = 2 * m_alignmentFile.getNumberOfAlignments()/10;
            System.out.println("Estimated output file size is in the order of " + estimated_file_size + "KB.");
        }
    }
 
    public void writeOutputFile(String filename) 
    {
        int page = 1;
        int row = 0;
        
        if(m_alignmentFile.getNumberOfAlignments() == 0)
        {
            System.out.println("Could not find alignments. Finishing...");
            return;
        }

        m_drawer.openFile();
        // Go through alignments
        ArrayList<DetailedAlignment> alignmentsForQuery = new ArrayList();
        String lastQuery = m_alignmentFile.getAlignment(0).getQueryName();
        String lastTarget = m_alignmentFile.getAlignment(0).getTargetName();
        for (int i=0; i<m_alignmentFile.getNumberOfAlignments(); i++) 
        {
            DetailedAlignment a = m_alignmentFile.getAlignment(i);            
            row++;
            if(a.getTargetName().equals(lastTarget) && row < MAX_ALIGNMENTS_PER_PAGE)
            {
                if(a.getQueryName().equals(lastQuery))
                {
                    alignmentsForQuery.add(a);
                }
                else
                {
                    drawAlignmentsForQuery(alignmentsForQuery);
                    alignmentsForQuery.clear();
                    alignmentsForQuery.add(a);
                    lastQuery = a.getQueryName();
                }
            }
            else 
            {
                drawAlignmentsForQuery(alignmentsForQuery);
                alignmentsForQuery.clear();
                alignmentsForQuery.add(a);
                lastQuery = a.getQueryName();
                
                row = 1;
                page++;
            }

            if (row == 1) 
            {
                if (page > 1) 
                {
                    m_drawer.closePicture();
                    m_drawer.drawVerticalGap(10);
                    m_drawer.newPage();
                }
                
                newTarget(a.getTargetSize(), m_alignmentFile.getTargetHitCount(a.getTargetName()));
                
                m_drawer.openPicture(0.1, 0.1);
                
                drawDividers(NUM_DIVIDERS);
                String targetName = a.getTargetName();
                
                drawTargetBar(targetName);                
            }

            lastTarget = a.getTargetName();
        }
        drawAlignmentsForQuery(alignmentsForQuery);

        m_drawer.closePicture();
        m_drawer.closeFile();
    }

    private void newTarget(int s, int h) 
    {
        m_targetSize = s;
        int targetHits = h;

        // Calculate unit length
        assert(m_targetSize > 0 );
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
            
            double y3 = m_yOffset + m_y-(m_targetHeight / 2);
            m_drawer.drawText(xpos, y3, Integer.toString(num), Drawer.Anchor.ANCHOR_MIDDLE, Drawer.Align.ALIGN_MIDDLE, "black");
        }
        m_y -= m_yStep;
    }
    
    private void drawTargetBar(String name) 
    {
        m_drawer.drawFilledRectangle(m_xOffset, m_yOffset + m_y - m_targetHeight, m_targetWidth, m_targetHeight, "red", "red");
        double texty = m_yOffset + m_y - (m_targetHeight / 2);
        m_drawer.drawText(m_xOffset + m_targetWidth + 5, texty, name, Drawer.Anchor.ANCHOR_LEFT, Drawer.Align.ALIGN_LEFT, "red");
        m_y -= m_yStep;
    }
    
    private boolean drawAlignmentLine(int from, int to) 
    {
        boolean overhangRight = false;
        float x1 = (float)from * m_unit;
        float x2 = (float)to * m_unit;
        float y1 = (float)m_y - ((float)m_targetHeight / 2);
        
        if (from > to) {
            System.out.println("[drawAlignmentLine] Something went wrong - from > to!");
            System.exit(-1);
        }
        
        if (x1 < -MAX_OVERHANG) 
        {
            x1 = -MAX_OVERHANG;
            m_drawer.drawText(m_xOffset + x1 - 5, m_yOffset + y1, "+" + Integer.toString(-from), Drawer.Anchor.ANCHOR_RIGHT, Drawer.Align.ALIGN_RIGHT, "black");
        }
        
        if (x2 > (m_targetWidth + MAX_OVERHANG)) 
        {
            x2 = m_targetWidth + MAX_OVERHANG;
            overhangRight = true;
            m_drawer.drawText(m_xOffset + x2 + 5, m_yOffset + y1, "+" + Integer.toString(to - m_targetSize), Drawer.Anchor.ANCHOR_LEFT, Drawer.Align.ALIGN_LEFT, "black");
        }
    
        m_drawer.drawLine(m_xOffset + x1, m_yOffset + y1, m_xOffset + x2, m_yOffset + y1, "black", false);
        return overhangRight;
    }
    
    public void drawAlignmentBox(int from, int to) 
    {
        double x1 = (float)from * m_unit;
        double x2 = (float)to * m_unit;
        double y1 = m_y - m_targetHeight;
        
        assert(x1 >= 0);
        assert(x2 <= m_targetWidth);
        
        double width = x2 - x1;
        double height = m_targetHeight;

        if (from > to) 
        {
            System.out.println("[drawAlignmentBox] Something went wrong - from > to!");
            System.exit(-1);
        }
        
        m_drawer.drawFilledRectangle(m_xOffset + x1, m_yOffset + y1, width, height, "white", "blue");
    }
    
    private void drawAlignmentsForQuery(ArrayList<DetailedAlignment> alignments)
    {
        int maxY = m_y;
        boolean overhangRight = false;
        for(DetailedAlignment a : alignments)
        {
            // Draw line representing the whole contig
            int contigStart = a.getTargetStart() - a.getQueryStart();
            int contigEnd = a.getTargetEnd() + (a.getQuerySize() - a.getQueryEnd());
            if(drawAlignmentLine(contigStart, contigEnd))
            {
                overhangRight = true;
            }

            // Now draw the box
            for (int j=0; j<a.getBlockCount(); j++) 
            {        
                int from = a.getBlockTargetStart(j);
                int to = from + a.getBlockSize(j);

                if (from < a.getTargetStart()) 
                {
                    System.out.println("[drawAlignmentsForQuery] Something went wrong - from < tStart");
                    System.exit(-1);
                }

                if (to > a.getTargetEnd()) 
                {
                    System.out.println("[drawAlignmentsForQuery] Something went wrong - to ("+to+") > tEnd ("+a.getTargetEnd()+") with from ("+from+")");
                    System.exit(-1);
                }

                drawAlignmentBox(from, to);
            }

             m_y -= m_yStep;
        }
        
        String contigName = alignments.get(0).getQueryName(); 

        // Filter velvet contig names, if neccessary
        // this should probably be done by the alignmentFile object
        if (contigName.matches("NODE_(\\d+)_length_(\\S+)_cov_(\\S+)")) 
        {
            String subs[] = contigName.split("_");
            contigName="NODE_"+subs[1];
        }
        
        int minY = m_y;
        float x = m_xOffset + m_targetWidth + 5;
        if(overhangRight)
        {
            x += 250;
        }
        float y = ((minY + maxY) / 2) - (m_targetHeight / 2) + (m_yStep/2);
        if(m_displayNames)
        {
            m_drawer.drawText(x, m_yOffset + y, contigName, Drawer.Anchor.ANCHOR_LEFT, Drawer.Align.ALIGN_LEFT, "blue");
        }
        
        float dividerLineY = m_y + 5;
        if(!m_isTexOut)
        {
            // y tho? :(
            dividerLineY = m_y + 1.825f * m_yStep;
        }
        m_drawer.drawLine(m_xOffset, dividerLineY, m_xOffset + m_targetWidth, dividerLineY, "black", true);
    }
    
    
    // sorting is hard
    private static class Sorter implements AlignmentSorter
    {   
        public static Comparator compareGroups = new Comparator<ArrayList<DetailedAlignment>>()
        {
            public int compare(ArrayList<DetailedAlignment> list1, ArrayList<DetailedAlignment> list2) {
                DetailedAlignment alignment1 = list1.get(0);
                DetailedAlignment alignment2 = list2.get(0);
                int result = alignment1.getTargetName().compareTo(alignment2.getTargetName());
                if(result ==0)
                {
                    result = alignment1.getTargetStart() - alignment2.getTargetStart();
                }
                return result;
            }
        };
        
        public static Comparator compareForAlignmentDiagram = new Comparator<DetailedAlignment>()
        {
            public int compare(DetailedAlignment alignment1, DetailedAlignment alignment2) {
                int result = alignment1.getTargetName().compareTo(alignment2.getTargetName());
                if(result == 0)
                {
                    result = alignment1.getQueryName().compareTo(alignment2.getQueryName());
                    if(result == 0)
                    {
                        if(options.getAlignmentDiagramSortMethod() == SortMethod.SORT_BY_TARGET_POS)
                        {
                            result = alignment1.getTargetStart() - alignment2.getTargetStart();
                        }
                        else
                        {
                            result = alignment1.getQueryStart() - alignment2.getQueryStart();
                        }
                    }
                }

                return result;
            }
        };
            
        public ArrayList<DetailedAlignment> sort(ArrayList<DetailedAlignment> alignments)
        {
            if(alignments.isEmpty())
            {
                System.out.println("No alignments to sort.");
                return alignments;
            }
            // first sort into groups of query and target
            alignments.sort(compareForAlignmentDiagram);

            // sort the groups of alignments by target and then target start
            ArrayList<ArrayList<DetailedAlignment>> metaList = new ArrayList();
            ArrayList<DetailedAlignment> currentQueryGroup = new ArrayList(); 
            String lastQuery = alignments.get(0).getQueryName();
            //pack
            for(DetailedAlignment alignment : alignments)
            {
                if(alignment.getQueryName().equals(lastQuery))
                {
                    currentQueryGroup.add(alignment);
                }
                else
                {
                    metaList.add(currentQueryGroup);
                    // new arrayList, don't clear because shallow copy etc.
                    currentQueryGroup = new ArrayList();
                    lastQuery = alignment.getQueryName();
                    currentQueryGroup.add(alignment);
                }
            }
            if(!currentQueryGroup.isEmpty())
            {
                metaList.add(currentQueryGroup);
            }
            // sort
            metaList.sort(compareGroups);

            // unpack
            ArrayList<DetailedAlignment> sortedList = new ArrayList();
            for(ArrayList<DetailedAlignment> list : metaList)
            {
                sortedList.addAll(list);
            }
            return sortedList;
        }
    }
    
}