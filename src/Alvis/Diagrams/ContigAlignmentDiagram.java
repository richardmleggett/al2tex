// Alvis
//
// Alignment Diagrams in LaTeX and SVG
//
// Copyright 2018 Richard Leggett, Samuel Martin
// samuel.martin@earlham.ac.uk
// 
// This is free software, supplied without warranty.

package Alvis.Diagrams;

import Alvis.Drawers.SVGDrawer;
import Alvis.Drawers.TikzDrawer;
import Alvis.Drawers.Drawer;
import Alvis.AlignmentFiles.DetailedAlignment;
import Alvis.AlignmentFiles.DetailedAlignmentFile;
import Alvis.AlignmentFilters.*;
import Alvis.Drawers.ColourGenerator;
import Alvis.DiagramOptions;
import java.awt.Color;
import java.util.*;
import java.io.*;
import javax.imageio.*;
import java.awt.image.*;

/**
 *
 * @author martins
 */
public class ContigAlignmentDiagram 
{
    private final static int MAX_ALIGNMENTS_PER_CONTIG = 20;
    private final static int NUM_COORD_MARKS = 4;
    private final static int MAX_NUM_KEYS_PER_PAGE = 7;
 
    private TreeMap<String, ArrayList<DetailedAlignment>> m_alignmentMap;
    Set<String> m_refNames;
    Set<String> m_chimeras;
    private double m_minAlignmentProp;
    private int m_contigDrawLength;
    private int m_contigDrawHeight;
    private int m_refContigDrawHeight;
    private int m_numKeysPerLine;
    private TreeMap<String, String> m_coloursMap;
    private int m_colourCounter;
    private boolean m_coloursSet;
    private Drawer m_drawer;
    private boolean m_detailedDiagram = false;
    private boolean m_constantKey = true;
    private final int m_keyOffset = 100;
    private boolean m_drawChimeraIcon = true;
    
    public ContigAlignmentDiagram(DetailedAlignmentFile alignmentFile, DiagramOptions options)
    {
        m_alignmentMap = new TreeMap();
        m_minAlignmentProp = options.getMinAlignmentProp();
        m_refNames = new LinkedHashSet();
        m_chimeras = new LinkedHashSet();
        m_contigDrawLength = 2000;
        m_contigDrawHeight = 100;
        m_refContigDrawHeight = 50;
        m_coloursMap = new TreeMap();
        m_coloursSet = false;
        m_colourCounter = 0;
        m_numKeysPerLine = 7;
        
        if(options.getAlignmentQueryName() != null && options.getAlignmentRefName() != null)
        {
            m_detailedDiagram = true;
        }
        
        String filename = options.getOutputFilePath() + "_contigalignment";
        if(options.getOutputFormat().equals("tex"))
        {
            m_drawer = new TikzDrawer(filename, true);
        }
        else
        {
            int width = m_detailedDiagram ? 2500 : 3508;
            int height = m_detailedDiagram ? 1400 : 2480;
            m_drawer = new SVGDrawer(filename, true, 1, width, height);
        }
       
        // filter out really small alignments
        if(options.getFilter())
        {
            alignmentFile.filterAlignments(new MinPropFilter(m_minAlignmentProp));
        }
        
        ChimeraFilter chimeraFilter = new ChimeraFilter(0.05f, 0.8f);
        if(options.getFindChimeras())
        {
            alignmentFile.filterAlignments(chimeraFilter);
        }
        else
        {
            m_chimeras = alignmentFile.getChimeras(chimeraFilter);
        }
        if(options.getPrintChimeras())
        {
            chimeraFilter.writeChimeraFile(options.getOutputDirectory() + "/chimeras.txt");
        }
        
        // Iterate through all the alignments and group by contig name.
        // Keep track of the different references for the key(s).
        for(int i = 0; i < alignmentFile.getNumberOfAlignments(); ++i)
        {
            DetailedAlignment alignment = alignmentFile.getAlignment(i);
            
            String queryName = alignment.getQueryName();
            if(!m_alignmentMap.containsKey(queryName))
            {
                m_alignmentMap.put(queryName, new ArrayList());
            }
            m_alignmentMap.get(queryName).add(alignment);
            m_refNames.add(alignment.getTargetName());
        }
        
        if(m_refNames.size() > MAX_NUM_KEYS_PER_PAGE)
        {
            m_constantKey = false;
            
            // split up alignments that contain too many references
            ArrayList<String> keys = new ArrayList();
            for(String queryName : m_alignmentMap.keySet())
            {
                keys.add(queryName);
            }
            
            // is this safe..?
            for(int i = 0; i < keys.size(); i++)
            {
                String queryName = keys.get(i);
                LinkedHashSet<String> refs = new LinkedHashSet();
                ArrayList<DetailedAlignment> list1 = new ArrayList();       
                ArrayList<DetailedAlignment>  list2 = new ArrayList();
                
                // keep alignments with the target one of the first MAX_NUM_KEYS_PER_PAGE targets in one list
                // put everything else in another
                for(DetailedAlignment alignment : m_alignmentMap.get(queryName))
                {
                    String targetName = alignment.getTargetName();
                    if(refs.contains(targetName))
                    {
                        list1.add(alignment);
                    }
                    else
                    {
                        if(refs.size() < MAX_NUM_KEYS_PER_PAGE)
                        {
                            refs.add(targetName);
                            list1.add(alignment);
                        }
                        else
                        {
                            list2.add(alignment);
                        }
                    }
                }
                
                if(!list2.isEmpty())
                {
                    m_alignmentMap.put(queryName, list1);
                    
                    String newQueryName = queryName + "_split";
                    m_alignmentMap.put(newQueryName, list2);
                    keys.add(newQueryName);
                }
            }
        }
        
        // sort each array of alignments by start pos
        for(List<DetailedAlignment> detailedAlignments : m_alignmentMap.values())
        {
            if(detailedAlignments.size() > MAX_ALIGNMENTS_PER_CONTIG)
            {
                Collections.sort(detailedAlignments, DetailedAlignment.compareByQueryAlignmentLength);
                List<DetailedAlignment> entriesToRemove = detailedAlignments.subList(MAX_ALIGNMENTS_PER_CONTIG, detailedAlignments.size());
                detailedAlignments.removeAll(entriesToRemove);
            }
            Collections.sort(detailedAlignments, DetailedAlignment.compareByQueryStart);
        }
        
        // Copy the chimera image
        BufferedImage chimeraImg = null;
        try 
        {
            chimeraImg = ImageIO.read(new File("Resources/chimera.png")); 
            File outputfile = new File(options.getOutputDirectory() + "/images/chimera.png");
            ImageIO.write(chimeraImg, "png", outputfile);
        } 
        catch (IOException e) 
        {
            m_drawChimeraIcon = false;
        }             
    }
    
    public void writeOutputFile(DiagramOptions options)
    {
        if(m_refNames.isEmpty())
        {
            if(options.getFindChimeras())
            {
                System.out.println("No chimera's found in " + options.getInputFilename() + ".");
            }
            else
            {
                System.out.println( "Something went wrong! No alignments were found in " + options.getInputFilename() +                  
                                    ". Did you specify the correct format?");
            }
            System.exit(0);
        }
        m_drawer.openFile();
        
        if(m_constantKey || m_detailedDiagram)
        {
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
        }
        // do the picture
        m_drawer.openPicture(0.1,0.1);
        
        if(m_detailedDiagram)
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
            drawAlignmentDiagram(alignmentsToDraw, 40, 1200);
        }
        else
        {
            // Should we iterate through an array of reference names so that the references are always in alphabetical order?
            ArrayList<String> drawn = new ArrayList();
            int totalQueries = m_alignmentMap.size();
            while(drawn.size() < totalQueries)
            {
                int i = 0;
                ArrayList<String> alignmentsForPage = new ArrayList();
                LinkedHashSet<String> refsForPage = new LinkedHashSet();
                for(String queryName : m_alignmentMap.keySet())
                {
                    if(!drawn.contains(queryName))
                    {                    
                        ArrayList<DetailedAlignment> alignments = m_alignmentMap.get(queryName);
                        LinkedHashSet<String> newRefs = new LinkedHashSet();                  
 
                        for(DetailedAlignment alignment : alignments)
                        {
                            String targetName = alignment.getTargetName();
                            if(!refsForPage.contains(targetName))
                            {
                                newRefs.add(targetName);  
                            }
                        }
                        
                        if(refsForPage.size() + newRefs.size() <= MAX_NUM_KEYS_PER_PAGE)
                        {
                            refsForPage.addAll(newRefs);
                            alignmentsForPage.add(queryName);
                            drawn.add(queryName);
                            i++;                         
                            int y = m_keyOffset + i * 175;
                            if((y > m_drawer.getPageHeight() && i > m_drawer.getMaxAlignmentsPerPage()))
                            {                 
                                break;
                            }
                        }
                    }
                }
                
                if(alignmentsForPage.size() > 0)
                {
                    drawContigAlignmentPage(alignmentsForPage, refsForPage);
                    // new page stuff
                    if(drawn.size() < totalQueries)
                    {
                        m_drawer.closePicture();
                        m_drawer.newPage();
                        m_drawer.openPicture(0.1,0.1);
                    } 
                }
                else
                {
                    System.out.println("Error in ContigAlignmentDiagram:");
                    System.out.println("Uh-oh, this shouldn't happen! Bailing...");
                    System.exit(1);
                }
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
        m_drawer.drawText(x - 100, (y + m_contigDrawHeight/2), contigName, Drawer.Anchor.ANCHOR_MIDDLE, "black");
        for(int i = 0; i <= NUM_COORD_MARKS; i++)
        {
            double xMark = x + i * (m_contigDrawLength / NUM_COORD_MARKS);
            String coord = Integer.toString(i * (contigLength / NUM_COORD_MARKS));
            m_drawer.drawLine(xMark, y - 5, xMark, y + 5, "black", false);
            m_drawer.drawText(xMark, y - 15, coord, Drawer.Anchor.ANCHOR_MIDDLE, "black");
        }
        
        // Draw the Chimera logo
        if(m_drawChimeraIcon && m_chimeras.contains(alignments.get(0).getQueryName()))
        {
            int chimerax = x + m_contigDrawLength + 30;
            int chimeray = y + (m_contigDrawHeight / 2);
            int size = 5;
            if(m_drawer instanceof SVGDrawer)
            {
                size = 40;
                chimeray -= 20;
            }
            m_drawer.drawImage(chimerax, chimeray, size, size, "images/chimera.png", "");
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
        int refContigXStart = x + (int)((1-refLengthProp) * m_contigDrawLength * 0.5);
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
        m_drawer.drawLine(x, zoomRefContigYStart, (refContigXStart + minRefX), refContigYEnd, "black", true);
        m_drawer.drawLine(x + m_contigDrawLength, zoomRefContigYStart, (refContigXStart + maxRefX), refContigYEnd, "black", true);
        m_drawer.drawText(x, zoomRefContigYStart - 20, Integer.toString(minRef), Drawer.Anchor.ANCHOR_MIDDLE, "black");
        m_drawer.drawText(x + m_contigDrawLength, zoomRefContigYStart - 20, Integer.toString(maxRef), Drawer.Anchor.ANCHOR_MIDDLE, "black");

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
            m_drawer.drawText(x, y - 20, Integer.toString(refStart), Drawer.Anchor.ANCHOR_MIDDLE, "black");
            m_drawer.drawText(x +  drawLength, y - 20, Integer.toString(refEnd), Drawer.Anchor.ANCHOR_MIDDLE, "black");
            m_drawer.drawText(x - 50, (y + m_refContigDrawHeight/2), refName, Drawer.Anchor.ANCHOR_MIDDLE, "black");
        }
    }   
    
    private void drawContigAlignmentPage(ArrayList<String> queryNames, LinkedHashSet<String> refNames)
    {
        // generate new key colours for the refs on tnis page
        if(!m_constantKey)
        {             
            // add the colours for the keys etc.
            m_coloursMap.clear();
            ColourGenerator colourGenerator = new ColourGenerator(refNames.size(), 0.7f, 0.7f);
            Iterator<String> iter = refNames.iterator();
            int colourCount = 0;
            while(iter.hasNext())
            {
                setColourForContig(iter.next(), colourGenerator.getColour(colourCount));
                colourCount++;
            }
        }
                            
        // draw the key
        drawKey(200,25);
        // draw the alignments
        int j = 0;
        for(String key : queryNames)
        {
            ArrayList<DetailedAlignment> detailedAlignments = m_alignmentMap.get(key);
            int drawY = m_keyOffset + j * 175;
            drawContig(detailedAlignments, 150, drawY);
            j++;
        }      
    }
}
