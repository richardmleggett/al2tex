/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package al2tex;

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
    protected String filename;
    protected BufferedWriter bw;
    private int gradientCounter;
    private HashMap<String, Color> colourMap;
    private double m_scale = 4;
    
    public SVGDrawer(String f) 
    {
        filename = f + ".svg";
        colourMap = new HashMap();
        gradientCounter = 0;
    }
    
    public void openFile() {
        try 
        {
            bw = new BufferedWriter(new FileWriter(filename));
            writeSVGHeader();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }
    }
    
    public void closeFile() {
        try 
        {
            writeSVGFooter();
            bw.flush();
            bw.close();
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
            bw.write("<svg version='1.1' height='5000' width='2000' xmlns='http://www.w3.org/2000/svg'>");
            bw.newLine();
            bw.write("<style>");
            bw.newLine();
            bw.write("\t.default { font-family: sans-serif; text-anchor: middle; }");
            bw.newLine();
            bw.write("</style>");
            bw.newLine();
            
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
            bw.write("</svg>");
            bw.newLine();
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
    
    public void drawLine(double x1, double y1, double x2, double y2, String colour, boolean dashed)
    {
        try
        {
            x1 *= m_scale;
            y1 *= m_scale;
            x2 *= m_scale;
            y2 *= m_scale;
            
            Color jcolour =  colourMap.get(colour);
            String colourString = colour;
            if(jcolour != null)
            {
                int r = jcolour.getRed();
                int g = jcolour.getGreen();
                int b = jcolour.getBlue();
                colourString = "rgb(" + Integer.toString(r) + "," + Integer.toString(g) + "," + Integer.toString(b) + ")";
            }
            bw.write("<line x1='" + Double.toString(x1) + "' y1='" + Double.toString(y1) + 
                         "' x2='" + Double.toString(x2) + "' y2='" + Double.toString(y2) + 
                         "' style='stroke:" + colourString + ";stroke-width:2' />");
            bw.newLine();
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
            
            Color colour =  colourMap.get(borderColour);
            String colourString = borderColour;
            if(colour != null)
            {
                int r = colour.getRed();
                int g = colour.getGreen();
                int b = colour.getBlue();
                colourString = "rgb(" + Integer.toString(r) + "," + Integer.toString(g) + "," + Integer.toString(b) + ")";
            }
            bw.write("<rect x='" + Double.toString(x) + "' y='" + Double.toString(y) + 
                         "' width='" + Double.toString(width) + "' height='" + Double.toString(height) + 
                         "' style='stroke:" + colourString + ";stroke-width:2;fill-opacity:0.0' />");
            bw.newLine();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }       
    }
    
    public void drawText(double x, double y, String text)
    {
        try
        {
            x *= m_scale;
            y *= m_scale;
            
            bw.write(   "<text x='" + Double.toString(x) + "' y='" + Double.toString(y) + "' class='default'>" 
                        + text + "</text>" ); 
            bw.newLine();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }         
    }
    
    public void drawTextRotated(double x, double y, String text, int angle)
    {
        try
        {
            x *= m_scale;
            y *= m_scale;
            bw.write("<text x='" + Double.toString(x) + "' y='" + Double.toString(y) + 
                     "' transform='rotate(" + Integer.toString(angle) + "," + Double.toString(x) + "," + Double.toString(y) + 
                     ")' class='default'>" + text + "</text>" ); 
            bw.newLine();
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
            
            bw.write("<image href='" + filename + "' x='" + Double.toString(x) + "' y='" + Double.toString(y) + "'" + 
                        " width='" + Double.toString(width) + "' height='" + Double.toString(height) + "'" +
                        " preserveAspectRatio='none'/>");
            bw.newLine();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }         
    }
    
    public void defineColour(String name, int red, int green, int blue)
    {
        Color newColour = new Color(red, green, blue);
        colourMap.put(name, newColour);
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
    
    public void drawNewPage()
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
            
            Color colour = colourMap.get(fillColour);
            int r = colour.getRed();
            int g = colour.getGreen();
            int b = colour.getBlue();
            
            int rightRed = r + ((255 - r) * (100 - fillRightPC)/100);
            int rightGreen = g + ((255 - g) * (100 - fillRightPC)/100);
            int rightBlue = b + ((255 - b) * (100 - fillRightPC)/100);
            
            int leftRed = r + ((255 - r) * (100 - fillLeftPC)/100);
            int leftGreen = g + ((255 - g) * (100 - fillLeftPC)/100);
            int leftBlue = b + ((255 - b) * (100 - fillLeftPC)/100);           
            
            String gradientName = "grad" + Integer.toString(gradientCounter++);
            
            bw.write("<defs>");
            bw.newLine();
            bw.write("\t<linearGradient id='" + gradientName + "' x1='" + 0 + "%' y1='0%' x2='" + 100 + "%' y2='0%'>");
            bw.newLine();
            bw.write("\t\t<stop offset='0%' style='stop-color:rgb(" + leftRed + "," + leftGreen + "," + leftBlue + ");stop-opacity:1' />");
            bw.newLine();
            bw.write("\t\t<stop offset='100%' style='stop-color:rgb(" + rightRed + "," + rightGreen + "," + rightBlue + ");stop-opacity:1' />");
            bw.newLine();
            bw.write("\t</linearGradient>");
            bw.newLine();
            bw.write("</defs>");
            bw.newLine();
            bw.write("<rect x='" + Double.toString(x) + "' y='" + Double.toString(y) + 
                         "' width='" + Double.toString(width) + "' height='" + Double.toString(height) + 
                         "' style='stroke:rgb(" + Integer.toString(r) + "," + Integer.toString(g) + "," + Integer.toString(b) +
                         ");stroke-width:2' fill='url(#" + gradientName + ")'/>");
            bw.newLine();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }          
    }
    
    public void drawKeyContig(double x, double y, double width, double height, String colour, String name)
    {
        x*= m_scale;
        y *= m_scale;
        width *= m_scale;
        height *= m_scale;
        drawAlignment(x, y, width, height, colour, colour, 0, 100);
        drawText(x + (width/2), y + 2.5 * height, name);
    }
    
    public void drawCurve(double startx, double starty, double endx, double endy, double controlx1, double controly1, double controlx2, double controly2)
    {
        return;
    }
    
    public void newPage()
    {
        // what is pages?
        return;
    }
    
    public int  getMaxAlignmentsPerPage()
    {
        return Integer.MAX_VALUE;
    }
    
    public void drawCoverageMap(CoverageMapImage coverageMap, double x, double y)
    {
        int nRows = coverageMap.getNumberOfRows();
        int targetSize = coverageMap.getTargetSize();
        String targetName = coverageMap.getTargetName();
        String filename = coverageMap.getFilename();
        
        int height = 100;
        int width = 100;
        
        x += 10;

        // draw x-axis labels
        double rowSize = (double)targetSize / (double)nRows;
        for (int r=0; r<nRows; r+=50) 
        {
            int rowY = (int)((double)r * ((double)height / (double)nRows));
            drawText(x - 10, y + rowY, Integer.toString((int)(r*rowSize)));
        }

        int rowY = (int)((double)(nRows) * ((double)height / (double)nRows));
        drawText(x - 10, y + rowY, Integer.toString(targetSize));
        
        // draw the image
        drawImage(x, y, width, height, filename, "");

        // draw the text labels
        int textxPos = 5 + width / 2;
        drawText(x + textxPos, y - 5, "Each row represents "+(int)rowSize+" nt");
        drawText(x + textxPos, y - 10, targetName);
        drawTextRotated(x - 25, y + (height/2), "Position in genome (nt)", 90);
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

            drawText(x + pos, y + imageHeight + 4, Integer.toString(num));
            drawLine(x + pos, y + imageHeight + 1, x + pos, y + imageHeight - 1, "black", false);
        }

        drawImage(x, y, imageWidth, imageHeight, filename, "[anchor=south west, inner sep=0pt, outer sep=0pt]");
        drawText(x + imageWidth + 15, y + imageHeight, targetName);   
    }
    
    public void drawScale(HeatMapScale heatMapScale, double x, double y)
    {
        int height = 4;
        int width = 50;
        
        drawImage(x, y, width, height, "heatmap.png", "[anchor=south west, inner sep=0pt, outer sep=0pt]");
        drawText(x + (width/2), y + 10, "Coverage");
        drawText(x, y + 10, "0");
        drawText(x + width, y + 10, Integer.toString(heatMapScale.getHeatMapSize()));           
    }
    
    public int getPageHeight()
    {
        return 500;
    }
}
