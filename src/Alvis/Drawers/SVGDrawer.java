// Alvis
//
// Alignment Diagrams in LaTeX and SVG
//
// Copyright 2018 Richard Leggett, Samuel Martin
// samuel.martin@earlham.ac.uk
// 
// This is free software, supplied without warranty.

package Alvis.Drawers;

import Alvis.Diagrams.CoverageMapImage;
import Alvis.Diagrams.HeatMapScale;
import java.awt.Color;
import java.util.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
/**
 *
 * @author martins
 */
public class SVGDrawer implements Drawer
{
    protected String m_filename;
    protected BufferedWriter m_bw;
    private int m_gradientCounter;
    private HashMap<String, Color> m_colourMap;
    private double m_scale;
    private boolean m_landscape;
    private int m_pageNumber;
    private String m_filenamePrefix;
    
    private static int m_longPageLength;
    private static int m_shortPageLength;
    private static int m_borderSize;
    private int m_pageHeight;
    
    public SVGDrawer(String f, boolean landscape, double scale, int longPageLength, int shortPageLength, int borderSize) 
    {
        m_pageNumber = 1;
        m_filenamePrefix = f;
        m_gradientCounter = 0;
        m_landscape = landscape;
        m_filename = m_filenamePrefix + m_pageNumber + ".svg";
        m_colourMap = new HashMap();
        m_scale = scale;
        
        m_longPageLength = longPageLength;
        m_shortPageLength = shortPageLength;
        
        m_borderSize = borderSize;
    }
    
    public void openFile() 
    {
        try 
        {
            m_bw = new BufferedWriter(new FileWriter(m_filename));
            writeSVGHeader();
            writeContigAlignmentScript();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }
    }
    
    public void closeFile() 
    {
        try 
        {
            writeSVGFooter();
            m_bw.flush();
            m_bw.close();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }
    }
    
    public void writeSVGHeader()
    {
        try
        {
            //TODO: How big canvas?
            if(m_landscape)
            {
                m_bw.write("<svg version='1.1' height='" + m_shortPageLength + "' width='" + m_longPageLength + 
                            "' xmlns='http://www.w3.org/2000/svg'>");
                m_pageHeight = m_shortPageLength;
            }
            else
            {
                m_bw.write("<svg version='1.1' height='" + m_longPageLength + "' width='" + m_shortPageLength + 
                            "' xmlns='http://www.w3.org/2000/svg'>");  
                m_pageHeight = m_longPageLength;
            }
            m_bw.newLine();
            m_bw.write("<style>");
            m_bw.newLine();
            m_bw.write("\t.default { font-family: sans-serif;");
            m_bw.newLine();
            m_bw.write("\t           font-size: 24px; }");
            m_bw.newLine();
            m_bw.write("</style>");
            m_bw.newLine();
            
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }
    }
    
    public void writeSVGFooter()
    {
        try
        {
            m_bw.write("</svg>");
            m_bw.newLine();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }
    }
    
    public void openPicture(double x, double y)
    {
        return;
    }
    
    public void closePicture()
    {
        return;
    }
         
    public void newPage()
    {
        closeFile();
        m_filename = m_filenamePrefix + (++m_pageNumber) + ".svg";
        openFile();
        return;
    }
    
    
    public void drawLine(double x1, double y1, double x2, double y2, String colour, boolean dashed)
    {
        try
        {
            x1 *= m_scale;
            y1 *= m_scale;
            x2 *= m_scale;
            y2 *= m_scale;
            
            x1 += m_borderSize;
            x2 += m_borderSize;
            
            y1 = m_pageHeight - y1;
            y2 = m_pageHeight - y2;
            
            Color jcolour =  m_colourMap.get(colour);
            String colourString = colour;
            if(jcolour != null)
            {
                int r = jcolour.getRed();
                int g = jcolour.getGreen();
                int b = jcolour.getBlue();
                colourString = "rgb(" + Integer.toString(r) + "," + Integer.toString(g) + "," + Integer.toString(b) + ")";
            }
            String dashString = dashed ? "stroke-dasharray='4'" : "";
            m_bw.write("<line x1='" + Double.toString(x1) + "' y1='" + Double.toString(y1) + 
                         "' x2='" + Double.toString(x2) + "' y2='" + Double.toString(y2) + 
                         "' style='stroke:" + colourString + ";stroke-width:2' " + dashString + "/>");
            m_bw.newLine();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }
    }
    
    public void drawRectangle(double x, double y, double width, double height, String borderColour)
    {
         try
        {   
            x *= m_scale;
            y *= m_scale;
            width *= m_scale;
            height *= m_scale;
            
            y = m_pageHeight - y;
            y -= height;
            
            x += m_borderSize;
            
            Color colour =  m_colourMap.get(borderColour);
            String colourString = borderColour;
            if(colour != null)
            {
                int r = colour.getRed();
                int g = colour.getGreen();
                int b = colour.getBlue();
                colourString = "rgb(" + Integer.toString(r) + "," + Integer.toString(g) + "," + Integer.toString(b) + ")";
            }
            m_bw.write("<rect x='" + Double.toString(x) + "' y='" + Double.toString(y) + 
                         "' width='" + Double.toString(width) + "' height='" + Double.toString(height) + 
                         "' style='stroke:" + colourString + ";stroke-width:2;fill-opacity:0.0' />");
            m_bw.newLine();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }       
    }
    
    public void drawFilledRectangle(double x, double y, double width, double height, String fillColour, String borderColour)
    {
        try
        {   
            x *= m_scale;
            y *= m_scale;
            width *= m_scale;
            height *= m_scale;
            
            y = m_pageHeight - y;
            y -= height;
            
            x += m_borderSize;
            
            Color bColour =  m_colourMap.get(borderColour);
            String colourString = borderColour;
            if(bColour != null)
            {
                int r = bColour.getRed();
                int g = bColour.getGreen();
                int b = bColour.getBlue();
                colourString = "rgb(" + Integer.toString(r) + "," + Integer.toString(g) + "," + Integer.toString(b) + ")";
            }
            
            Color fColour =  m_colourMap.get(fillColour);
            String fillColourString = fillColour;
            if(fColour != null)
            {
                int r = fColour.getRed();
                int g = fColour.getGreen();
                int b = fColour.getBlue();
                fillColourString = "rgb(" + Integer.toString(r) + "," + Integer.toString(g) + "," + Integer.toString(b) + ")";
            }
            m_bw.write("<rect x='" + Double.toString(x) + "' y='" + Double.toString(y) + 
                         "' width='" + Double.toString(width) + "' height='" + Double.toString(height) + 
                         "' style='stroke:" + colourString + ";stroke-width:2;fill:" + fillColourString + ";fill-opacity:1.0' />");
            m_bw.newLine();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }    
    }
    
    public void drawText(double x, double y, String text, Anchor anchor, String colour)
    {
        try
        {
            x *= m_scale;
            y *= m_scale;
            
            y = m_pageHeight - y;
            x += m_borderSize;
            
            String anchorString = "";
            switch(anchor)
            {
                case ANCHOR_LEFT:
                {
                    anchorString = "text-anchor='start'";
                    break;
                }
                case ANCHOR_MIDDLE:
                {
                    anchorString = "text-anchor='middle'";
                    break;
                }
                case ANCHOR_RIGHT:
                {
                    anchorString = "text-anchor='end'";
                    break;
                }
            }
                      
            m_bw.write("<text x='" + Double.toString(x) + "' y='" + Double.toString(y) + "' class='default' " + anchorString + ">" 
                        + text + "</text>" ); 
            m_bw.newLine();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }         
    }
    
    public void drawTextRotated(double x, double y, String text, int angle, Anchor anchor)
    {
        try
        {
            x *= m_scale;
            y *= m_scale;
            y = m_pageHeight - y;
            x += m_borderSize;
            
            String anchorString = "";
            switch(anchor)
            {
                case ANCHOR_LEFT:
                {
                    anchorString = "text-anchor='start'";
                    break;
                }
                case ANCHOR_MIDDLE:
                {
                    anchorString = "text-anchor='middle'";
                    break;
                }
                case ANCHOR_RIGHT:
                {
                    anchorString = "text-anchor='end'";
                    break;
                }
            }
            
            m_bw.write("<text x='" + Double.toString(x) + "' y='" + Double.toString(y) + 
                     "' transform='rotate(" + Integer.toString(angle) + "," + Double.toString(x) + "," + Double.toString(y) + 
                     ")' class='default' " + anchorString + ">" + text + "</text>" ); 
            m_bw.newLine();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }         
    }
    
    public void drawImage(double x, double y, double width, double height, String filename, String optionsString)
    {
        try
        {
            x *= m_scale;
            y *= m_scale;
            width *= m_scale;
            height *= m_scale;
            
            y = m_pageHeight - y;
            y -= height;
            x += m_borderSize;
            
            m_bw.write("<image href='" + filename + "' x='" + Double.toString(x) + "' y='" + Double.toString(y) + "'" + 
                        " width='" + Double.toString(width) + "' height='" + Double.toString(height) + "'" +
                        " preserveAspectRatio='none'/>");
            m_bw.newLine();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }         
    }
    
    public void defineColour(String name, int red, int green, int blue)
    {
        Color newColour = new Color(red, green, blue);
        m_colourMap.put(name, newColour);
    }
    
    public void drawVerticalGap(int y)
    {
        return;
    }
    
    public void drawHorizontalGap(int x)
    {
        return;
    }
    
    public void drawNewline()
    {
        return;
    }
    
    public void drawAlignment(double x, double y, double width, double height, String fillColour, String borderColour, int fillLeftPC, int fillRightPC)
    {
           try
        {
            x *= m_scale;
            y *= m_scale;
            width *= m_scale;
            height *= m_scale;
            
            y = m_pageHeight - y;
            y -= height;
            
            x += m_borderSize;
            
            Color colour = m_colourMap.get(fillColour);
            int r = colour.getRed();
            int g = colour.getGreen();
            int b = colour.getBlue();
            
            int rightRed = r + ((255 - r) * (100 - fillRightPC)/100);
            int rightGreen = g + ((255 - g) * (100 - fillRightPC)/100);
            int rightBlue = b + ((255 - b) * (100 - fillRightPC)/100);
            
            int leftRed = r + ((255 - r) * (100 - fillLeftPC)/100);
            int leftGreen = g + ((255 - g) * (100 - fillLeftPC)/100);
            int leftBlue = b + ((255 - b) * (100 - fillLeftPC)/100);           
            
            String gradientName = "grad" + Integer.toString(m_gradientCounter++);
            
            m_bw.write("<defs>");
            m_bw.newLine();
            m_bw.write("\t<linearGradient id='" + gradientName + "' x1='" + 0 + "%' y1='0%' x2='" + 100 + "%' y2='0%'>");
            m_bw.newLine();
            m_bw.write("\t\t<stop offset='0%' style='stop-color:rgb(" + leftRed + "," + leftGreen + "," + leftBlue + ");stop-opacity:1' />");
            m_bw.newLine();
            m_bw.write("\t\t<stop offset='100%' style='stop-color:rgb(" + rightRed + "," + rightGreen + "," + rightBlue + ");stop-opacity:1' />");
            m_bw.newLine();
            m_bw.write("\t</linearGradient>");
            m_bw.newLine();
            m_bw.write("</defs>");
            m_bw.newLine();
            m_bw.write("<rect x='" + Double.toString(x) + "' y='" + Double.toString(y) + 
                         "' width='" + Double.toString(width) + "' height='" + Double.toString(height) + 
                         "' stroke='rgb(" + Integer.toString(r) + "," + Integer.toString(g) + "," + Integer.toString(b) +
                         ")' stroke-width='2' fill='url(#" + gradientName + ")' />");
            m_bw.newLine();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }       
    }
    
    // TODO: Remove this code duplication!
    public void drawAlignment(double x, double y, double width, double height, String fillColour, String borderColour, int fillLeftPC, int fillRightPC, String alignmentID)
    {
         try
        {
            x *= m_scale;
            y *= m_scale;
            width *= m_scale;
            height *= m_scale;
            
            y = m_pageHeight - y;
            y -= height;
            
            x += m_borderSize;
            
            Color colour = m_colourMap.get(fillColour);
            int r = colour.getRed();
            int g = colour.getGreen();
            int b = colour.getBlue();
            
            int rightRed = r + ((255 - r) * (100 - fillRightPC)/100);
            int rightGreen = g + ((255 - g) * (100 - fillRightPC)/100);
            int rightBlue = b + ((255 - b) * (100 - fillRightPC)/100);
            
            int leftRed = r + ((255 - r) * (100 - fillLeftPC)/100);
            int leftGreen = g + ((255 - g) * (100 - fillLeftPC)/100);
            int leftBlue = b + ((255 - b) * (100 - fillLeftPC)/100);           
            
            String gradientName = "grad" + Integer.toString(m_gradientCounter++);
            
            m_bw.write("<defs>");
            m_bw.newLine();
            m_bw.write("\t<linearGradient id='" + gradientName + "' x1='" + 0 + "%' y1='0%' x2='" + 100 + "%' y2='0%'>");
            m_bw.newLine();
            m_bw.write("\t\t<stop offset='0%' style='stop-color:rgb(" + leftRed + "," + leftGreen + "," + leftBlue + ");stop-opacity:1' />");
            m_bw.newLine();
            m_bw.write("\t\t<stop offset='100%' style='stop-color:rgb(" + rightRed + "," + rightGreen + "," + rightBlue + ");stop-opacity:1' />");
            m_bw.newLine();
            m_bw.write("\t</linearGradient>");
            m_bw.newLine();
            m_bw.write("</defs>");
            m_bw.newLine();
            m_bw.write("<rect x='" + Double.toString(x) + "' y='" + Double.toString(y) + 
                         "' width='" + Double.toString(width) + "' height='" + Double.toString(height) + 
                         "' stroke='rgb(" + Integer.toString(r) + "," + Integer.toString(g) + "," + Integer.toString(b) +
                         ")' stroke-width='2' fill='url(#" + gradientName + ")'" + 
                         " id='alignment" + alignmentID +"'" + 
                         " onclick=\"showAlignment('box" + alignmentID + "', 'alignment" + alignmentID + "')\" />");
            m_bw.newLine();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }          
    }
    
    public void drawKeyContig(double x, double y, double width, double height, String colour, String name)
    {
        drawAlignment(x, y, width, height, colour, colour, 0, 100);
        drawText(x + (width/2), y + 1.3 * height, name, Drawer.Anchor.ANCHOR_MIDDLE, "black");
    }
    
    public void drawCurve(double startx, double starty, double endx, double endy, double controlx1, double controly1, double controlx2, double controly2)
    {
               try
        {
            startx *= m_scale;
            starty *= m_scale;
            endx *= m_scale;
            endy *= m_scale;
            controlx1 *= m_scale;
            controlx2*= m_scale;
            controly1 *= m_scale;
            controly2 *= m_scale;
            
            starty = m_pageHeight - starty;
            endy = m_pageHeight - endy;
            controly1 = m_pageHeight - controly1;
            controly2 = m_pageHeight - controly2;
            
            startx += m_borderSize;
            endx += m_borderSize;
            controlx1 += m_borderSize;
            controlx2 += m_borderSize;
            
            
            m_bw.write("<path d='M" + Double.toString(startx) + " " + Double.toString(starty) + 
                        " C " + Double.toString(controlx1) + " " + Double.toString(controly1) + 
                        ", " + Double.toString(controlx2) + " " + Double.toString(controly2) + 
                        ", " + Double.toString(endx) + " " + Double.toString(endy) + 
                         "' style='stroke: black ;stroke-width:2' fill='transparent' />");
            m_bw.newLine();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }
 
    }

    public int  getMaxAlignmentsPerPage()
    {
        return 12;
    }
    
    public void drawCoverageMap(CoverageMapImage coverageMap, double x, double y)
    {
        int nRows = coverageMap.getNumberOfRows();
        int targetSize = coverageMap.getTargetSize();
        String targetName = coverageMap.getTargetName();
        String filename = coverageMap.getFilename();
        
        int height = 100;
        int width = 100;
        x += 30;

        // draw x-axis labels
        double rowSize = (double)targetSize / (double)nRows;
        for (int r=0; r<nRows; r+=50) 
        {
            int rowY = height - (int)((double)r * ((double)height / (double)nRows));
            drawText(x - 2, y + rowY, Integer.toString((int)(r*rowSize)), Drawer.Anchor.ANCHOR_RIGHT, "black");
        }

        int rowY = height - (int)((double)(nRows) * ((double)height / (double)nRows));
        drawText(x - 2, y + rowY, Integer.toString(targetSize), Drawer.Anchor.ANCHOR_RIGHT, "black");
        
        // draw the image
        drawImage(x, y, width, height, filename, "");

        // draw the text labels
        int textxPos = width / 2;
        drawText(x + textxPos, y - 5, "Each row represents "+(int)rowSize+" nt", Drawer.Anchor.ANCHOR_MIDDLE, "black");
        drawText(x + textxPos, y - 10, targetName, Drawer.Anchor.ANCHOR_MIDDLE, "black");
        drawTextRotated(x - 35, y + (height/2), "Position in genome (nt)", 90, Drawer.Anchor.ANCHOR_MIDDLE);
        closePicture();  
    }
    
    public void drawCoverageLong(CoverageMapImage coverageMap, double x, double y, double imageWidth, double imageHeight, int num_dividers)
    {
        int targetSize = coverageMap.getTargetSize();
        String targetName = coverageMap.getTargetName();
        String filename = coverageMap.getFilename();
        double unit = (double)imageWidth / targetSize;                

        for (int i=0; i <= num_dividers; i++) 
        {
            int num = i == num_dividers ? targetSize: (int)((targetSize / num_dividers) * i);
            int pos = (int)((double)num * unit);

            drawText(x + pos, y + imageHeight + 4, Integer.toString(num), Drawer.Anchor.ANCHOR_MIDDLE, "black");
            drawLine(x + pos, y + imageHeight + 1, x + pos, y + imageHeight - 1, "black", false);
        }

        drawImage(x, y, imageWidth, imageHeight, filename, "[anchor=south west, inner sep=0pt, outer sep=0pt]");
        drawText(x + imageWidth + 2, y, targetName, Drawer.Anchor.ANCHOR_LEFT, "black");   
    }
    
    public void drawScale(HeatMapScale heatMapScale, double x, double y)
    {
        int height = heatMapScale.getHeatMapHeight();
        int width = heatMapScale.getHeatMapWidth();
        String filename = heatMapScale.getFilename();
        
        drawImage(x, y, width, height, filename, "[anchor=south west, inner sep=0pt, outer sep=0pt]");
        drawText(x + (width/2), y + height + 1, "Coverage", Drawer.Anchor.ANCHOR_MIDDLE, "black");
        drawText(x, y + height + 1, "0", Drawer.Anchor.ANCHOR_MIDDLE, "black");
        drawText(x + width, y + height + 1, Integer.toString(heatMapScale.getHeatMapSize()), Drawer.Anchor.ANCHOR_MIDDLE, "black");           
    }
    
    public int getPageHeight()
    {
        if(m_landscape)
        {
            return (int)(m_shortPageLength / m_scale) - 2 * m_borderSize;
        }
        else
        {
            return (int)(m_longPageLength / m_scale) - 2 * m_borderSize;
        }
    }
    
    private void writeContigAlignmentScript()
    {
        try
        {
            m_bw.write( "<script type='text/javascript'><![CDATA[\n" +
                        "var currentBox = null;\n" +
                        "var currentAlignment = null;\n" +
                        "var currentBoxid = null;\n" + 
                        "document.addEventListener(\"click\", function() { \n" +
                        "   if(currentBox) {\n" +
                        "       currentBox.setAttribute('visibility', 'hidden');\n" +
                        "   }\n" +
                        "   currentBox = null;\n" +
                        "   currentBoxid = null;\n" +
                        "   if(currentAlignment) {\n" + 
                        "       currentAlignment.setAttribute('stroke-width', '2');\n" +
                        "   }\n" +
                        "   currentAlignment = null;\n" + 
                        "})\n" +
                        "function showAlignment(boxid, alignmentid) { \n" +
                        "   if(currentBox) {\n" +
                        "       currentBox.setAttribute('visibility', 'hidden');\n" +
                        "   }\n" +
                        "   if(currentAlignment) {\n" + 
                        "       currentAlignment.setAttribute('stroke-width', '2');\n" +
                        "   }\n" + 
                        "   if(boxid === currentBoxid) {\n" +
                        "       currentBoxid = null;\n" +
                        "       return;\n" +
                        "   }\n" + 
                        "   currentBoxid = boxid;\n" +
                        "   currentBox = document.getElementById(boxid);\n" +
                        "   currentBox.setAttribute('visibility', 'visible');\n" +
                        "   currentAlignment = document.getElementById(alignmentid);\n" + 
                        "   currentAlignment.setAttribute('stroke-width', '10');\n" + 
                        "   event.stopPropagation();\n" +
                        "}\n" +
                        "]]>\n" +
                        "</script>");
            m_bw.newLine();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }  
    }
    
    public void writeContigAlignmentBox(int x, int y, String alignmentID, String queryName, int queryStartPos, int queryEndPos, String targetName, int targetStartPos, int targetEndPos, String orientation)
    {
        try
        {
            y = m_pageHeight - y;
            int dy = 22;
            y -= 200;
            m_bw.write("<g id='box" + alignmentID + "' visibility='hidden'>");
            m_bw.newLine();
            m_bw.write("\t<rect x='" + x + "' y='" + y + "' width='400' height='210' style='fill:rgb(255,219,88);stroke-width:2;stroke:rgb(0,0,0)'"
                     + " rx='15' ry='15' /> ");
            m_bw.newLine();
            x += 10;
            y += 15;
            m_bw.write("\t<text x='" + x + "' y='" + (y + dy) + "' width='300' class='default' text-anchor='left'>Query ID: " + queryName + " </text>");
            m_bw.newLine();
            m_bw.write("\t<text x='" + x + "' y='" + (y + 2 * dy) + "' width='300' class='default' text-anchor='left'>Query Start pos: " + 
                       queryStartPos + " </text>");
            m_bw.newLine();
            m_bw.write("\t<text x='" + x + "' y='" + (y + 3 * dy) + "' width='300' class='default' text-anchor='left'>Query End  pos: " + 
                        queryEndPos + " </text>");
            m_bw.newLine();
            y -= 10;
            m_bw.write("\t<text x='" + x + "' y='" + (y + 5 * dy) + "' width='300' class='default' text-anchor='left'>Target ID: " + 
                        targetName + " </text>");
            m_bw.newLine();
            m_bw.write("\t<text x='" + x + "' y='" + (y + 6 * dy) + "' width='300' class='default' text-anchor='left'>Target Start pos: " + 
                        targetStartPos + " </text>");
            m_bw.newLine();
            m_bw.write("\t<text x='" + x + "' y='" + (y + 7 * dy) + "' width='300' class='default' text-anchor='left'>Target End  pos: " + 
                        targetEndPos + " </text>");
            m_bw.newLine();
            y -= 10;
            m_bw.write("\t<text x='" + x + "' y='" + (y + 9 * dy) + "' width='300' class='default' text-anchor='left'>Orientation: " + 
                        orientation + " </text>");
            m_bw.newLine();
            m_bw.write("</g>");
            m_bw.newLine();
        }
        catch (IOException e) 
        {
            System.out.println(e);
        }  
    }
}
