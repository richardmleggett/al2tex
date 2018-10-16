/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package al2tex;

import java.awt.Color;
import java.util.*;

/**
 *
 * @author martins
 */
public class ContigAlignmentDiagram 
{
    private TreeMap<String, ArrayList<DetailedAlignment>> m_alignmentMap;
    Set<String> m_refNames;
    private double m_minAlignmentProp;
    private final static int MAX_ALIGNMENTS_PER_CONTIG = 20;
    private final static int NUM_COORD_MARKS = 4;
    
    private int m_contigDrawLength;
    private int m_contigDrawHeight;
    private int m_refContigDrawHeight;
    private int m_numKeysPerLine;
    private TreeMap<String, String> m_coloursMap;
    private int m_colourCounter;
    private boolean m_coloursSet;
    private Drawer m_drawer;
    
    public ContigAlignmentDiagram(DetailedAlignmentFile alignmentFile, DiagramOptions options)
    {
        m_alignmentMap = new TreeMap<String, ArrayList<DetailedAlignment>>();
        m_minAlignmentProp = options.getMinPAFAlignmentProp();
        m_refNames = new LinkedHashSet();
        m_contigDrawLength = 2000;
        m_contigDrawHeight = 100;
        m_refContigDrawHeight = 50;
        m_coloursMap = new TreeMap();
        m_coloursSet = false;
        m_colourCounter = 0;
        m_numKeysPerLine = 7;
        
        if(options.getOutputFormat().equals("tex"))
        {
            m_drawer = new TikzDrawer(options.getOutputFilePath());
        }
        else
        {
            m_drawer = new SVGDrawer(options.getOutputFilePath(), true, 1, 3508, 2480);
        }
      
        // iterate through all the alignments and group by contig name
        // filter out really small alignments
        for(int i = 0; i < alignmentFile.getNumberOfAlignments(); ++i)
        {
            DetailedAlignment alignment = alignmentFile.getAlignment(i);
            int alignmentLength = alignment.getQueryEnd() - alignment.getQueryStart();
            if((double)alignmentLength / alignment.getQuerySize() >= m_minAlignmentProp)
            {
                String name = alignment.getQueryName();
                if(!m_alignmentMap.containsKey(name))
                {
                    m_alignmentMap.put(name, new ArrayList());
                }
                m_alignmentMap.get(name).add(alignment);
                m_refNames.add(alignment.getTargetName());
            }
        }
        
        // sort each array of alignments by start pos
        for(List<DetailedAlignment> detailedAlignments : m_alignmentMap.values())
        {
            if(detailedAlignments.size() > MAX_ALIGNMENTS_PER_CONTIG)
            {
                Collections.sort(detailedAlignments, DetailedAlignment.compareByQueryAlignmentLength);
                List<DetailedAlignment> entriesToRemove = detailedAlignments.subList(0, detailedAlignments.size() - MAX_ALIGNMENTS_PER_CONTIG);
                detailedAlignments.removeAll(entriesToRemove);
            }
            Collections.sort(detailedAlignments, DetailedAlignment.compareByQueryStart);
        }
    }
    
    public void writeTexFile(DiagramOptions options)
    {
        if(m_refNames.isEmpty())
        {
            System.out.println( "Something went wrong! No alignments were found in " + options.getInputFilename() +                  
                                ". Did you specify the correct format?");
            System.exit(0);
        }
        m_drawer.openFile();
        
        // generate and write the colours
        int refNumber = m_refNames.size();
        ColourGenerator colourGenerator = new ColourGenerator(refNumber, 0.7f, 0.7f);
        Iterator<String> iter = m_refNames.iterator();
        int colourCount = 0;
        while(iter.hasNext())
        {
            setColourForContig(iter.next(), colourGenerator.getColour(colourCount));
            colourCount++;
        }
        
        // do the picture
        m_drawer.openPicture(0.1,0.1);
        
        if(options.getAlignmentQueryName() != null && options.getAlignmentRefName() != null)
        {
            // do the stuff
            System.out.println("Drawing contig in detail.");
            String queryName = options.getAlignmentQueryName();
            String refName = options.getAlignmentRefName();
            ArrayList<DetailedAlignment> alignmentsToDraw = new ArrayList();
            ArrayList<DetailedAlignment> queryAlignments = m_alignmentMap.get(queryName);
            if(queryAlignments == null)
            {
                System.out.println("Could not find query " + queryName + " in file " + options.getInputFilename());
                System.exit(0);
            }
            for(int i = 0; i < queryAlignments.size(); i++)
            {
                DetailedAlignment alignment = queryAlignments.get(i);
                if(alignment.getTargetName().equals(refName))
                {
                    alignmentsToDraw.add(alignment);
                }
            }
            if(alignmentsToDraw.isEmpty())
            {
                System.out.println("Could not find alignments between " + queryName + " and " + refName + " in file " + options.getInputFilename());
                System.exit(0);
            }
            drawAlignmentDiagram(alignmentsToDraw, 0, 7 * 175);
        }
        else
        {        
            drawKey(200,25);
            int keyOffset = 100;
            int i = 0;
            for(ArrayList<DetailedAlignment> DetailedAlignments : m_alignmentMap.values())
            {
                // make a new diagram on a new page
                // TODO: 175 is a magic number
                int y = keyOffset + i * 175;
                if(y > m_drawer.getPageHeight() && i > m_drawer.getMaxAlignmentsPerPage())
                {
                    m_drawer.closePicture();
                    m_drawer.newPage();
                    m_drawer.openPicture(0.1,0.1);
                    drawKey(200,25);
                    i = 0;
                    y = keyOffset;
                }

                drawContig(DetailedAlignments, 150, y );
                i++;
            }
        }
        m_drawer.closePicture();
        m_drawer.closeFile();
    }
    
    public void setColourForContig(String refContig, Color colour)
    {
        if(m_coloursMap.keySet().contains(refContig))
        {
            System.out.print("Already set colour for contig " + refContig + ".\n");
            return;
        }
        m_coloursMap.put(refContig, "colour_" + m_colourCounter);

        int red = colour.getRed();
        int green = colour.getGreen();
        int blue = colour.getBlue();
        m_drawer.defineColour("colour_" + m_colourCounter, red, green, blue);
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

        String contigName = alignments.get(0).getQueryName().replace("_", "\\_");
        int contigLength = alignments.get(0).getQuerySize();

        // fill in the alignments
        //int bpLength = alignments.get(0).getQueryContigLength();
        double alignmentHeight = (double)m_contigDrawHeight / alignments.size();
        double height = alignmentHeight;
        for(int i = 0; i < alignments.size(); i++)
        {
            DetailedAlignment alignment = alignments.get(i);
            assert(alignment.getQueryName().equals(contigName));
            double start = (double)alignment.getQueryStart() * m_contigDrawLength / contigLength;
            double end = (double)alignment.getQueryEnd() * m_contigDrawLength / contigLength;
            double width = end - start;

            double refStartPC = 100 * (double)alignment.getTargetStart() / alignment.getTargetSize();
            double refEndPC = 100 * (double)alignment.getTargetEnd() / alignment.getTargetSize();
            if(alignment.isReverseAlignment())
            {
                double temp = refStartPC;
                refStartPC = refEndPC;
                refEndPC = temp;                            
            }

            String colour = m_coloursMap.get(alignment.getTargetName());
            m_drawer.drawAlignment(x + start, y + (i * alignmentHeight), width, height, colour, colour, (int)refStartPC, (int)refEndPC);
        }

        // draw the contig rectangle
        m_drawer.drawRectangle(x, y, m_contigDrawLength, m_contigDrawHeight, "black");
        m_drawer.drawText(x - 100, (y + m_contigDrawHeight/2), contigName);
        for(int i = 0; i <= NUM_COORD_MARKS; i++)
        {
            double xMark = x + i * (m_contigDrawLength / NUM_COORD_MARKS);
            String coord = Integer.toString(i * (contigLength / NUM_COORD_MARKS));
            m_drawer.drawLine(xMark, y - 5, xMark, y + 5, "black", false);
            m_drawer.drawText(xMark, y - 15, coord);
        }
}
    
    public void drawKey(int x, int y)
    {
        assert(m_coloursSet);
        // TODO: do this in a sensible way, based on how many reference sequences there are.

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
            m_drawer.drawKeyContig(xPos, yPos, 75, 15, colour, name);
            i++;
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
        
        // draw the "zoom lines"
        int refContigDrawLength = (int)(m_contigDrawLength * refLengthProp);
        double minRefX = (double)minRef * refContigDrawLength / refLength;
        double maxRefX = (double)maxRef * refContigDrawLength / refLength;
        m_drawer.drawLine(0, zoomRefContigYStart, (refContigXStart + minRefX), refContigYEnd, "black", true);
        m_drawer.drawLine(m_contigDrawLength, zoomRefContigYStart, (refContigXStart + maxRefX), refContigYEnd, "black", true);
        m_drawer.drawText(0, zoomRefContigYStart - 20, Integer.toString(minRef));
        m_drawer.drawText(m_contigDrawLength, zoomRefContigYStart - 20, Integer.toString(maxRef));

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
            double refWidth = refEnd - refStart;
            double refHeight = refContigYEnd - refContigYStart;

            String colour = m_coloursMap.get(alignment.getTargetName());
            double refStartPC = 100 * (double)alignment.getTargetStart() / alignment.getTargetSize();
            double refEndPC = 100 * (double)alignment.getTargetEnd() / alignment.getTargetSize();
            m_drawer.drawAlignment(refStart, refContigYStart, refWidth, refHeight, colour, "black", (int)refStartPC, (int)refEndPC);

            //draw the alignments on the zoomed ref
            double zoomRefStart = x + (double)(alignment.getTargetStart() - minRef) * m_contigDrawLength / zoomRefLength;
            double zoomRefEnd = x + (double)(alignment.getTargetEnd() - minRef) * m_contigDrawLength / zoomRefLength;
            double zoomRefWidth = zoomRefEnd - zoomRefStart;
            double zoomRefHeight = zoomRefContigYEnd - zoomRefContigYStart;
            m_drawer.drawAlignment(zoomRefStart, zoomRefContigYStart, zoomRefWidth, zoomRefHeight, colour, "black", (int)refStartPC, (int)refEndPC);

            if(alignment.isReverseAlignment())
            {
                double temp = zoomRefEnd;
                zoomRefEnd = zoomRefStart;
                zoomRefStart = temp;
            }
            double controlY = zoomRefContigYEnd + 50;
            // draw curves from query to ref
            m_drawer.drawCurve(queryStart, y, zoomRefStart, zoomRefContigYEnd, queryStart, controlY, zoomRefStart, controlY);
            m_drawer.drawCurve(queryEnd, y, zoomRefEnd, zoomRefContigYEnd, queryEnd, controlY, zoomRefEnd, controlY);

            // draw dashed lines on the query contig at the beginning and end of each alignment
            m_drawer.drawLine(queryStart, y, queryStart, (y + m_contigDrawHeight), "black", true);
            m_drawer.drawLine(queryEnd, y, queryEnd, (y + m_contigDrawHeight), "black", true);  
        }

        // draw the outlines of the alignments on the ref again, because they might have been drawn over :(
        for(int i = 0; i < alignments.size(); i++)
        {
            DetailedAlignment alignment = alignments.get(i);
            double refStart = x + (double)(alignment.getTargetStart() - minRef) * m_contigDrawLength / zoomRefLength;
            double refEnd = x + (double)(alignment.getTargetEnd() - minRef) * m_contigDrawLength / zoomRefLength;
            m_drawer.drawLine(refStart, zoomRefContigYStart, refStart, zoomRefContigYEnd, "black", false);
            m_drawer.drawLine(refEnd, zoomRefContigYStart, refEnd, zoomRefContigYEnd, "black", false);     
        }
    }
    
    public void drawReferenceContig(int x, int y, String refName, int refStart, int refEnd, boolean drawLabels, double width)
    {
        assert(0. <= width && width <= 1.);
        double drawLength = m_contigDrawLength * width;
        // draw the contig rectangle

        m_drawer.drawRectangle(x, y, drawLength, m_refContigDrawHeight, "black");
        if(drawLabels)
        {
            m_drawer.drawText(x, y - 20, Integer.toString(refStart));
            m_drawer.drawText(x +  drawLength, y - 20, Integer.toString(refEnd));
            m_drawer.drawText(x - 50, (y + m_refContigDrawHeight/2), refName);
        }
    }   
}
