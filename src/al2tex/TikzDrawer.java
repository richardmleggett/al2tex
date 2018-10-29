/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package al2tex;

import java.util.*;
import java.io.*;

/**
 *
 * @author martins
 */
public class TikzDrawer implements Drawer
{
    protected String filename;
    protected BufferedWriter bw;
    private boolean m_landscape;
    
    public TikzDrawer(String f, boolean landscape) {
        filename = f + ".tex";
        m_landscape = landscape;
    }
    
    public void openFile() {
        try 
        {
            bw = new BufferedWriter(new FileWriter(filename));
            writeTexHeader();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }
    }
    
    public void closeFile() {
        try 
        {
            writeTexFooter();
            bw.flush();
            bw.close();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }
    }
    
    protected void writeTexHeader() {
        try {
            String pageOrientation = m_landscape ? "landscape" : "portrait";            
            bw.write("\\documentclass[a4paper,11pt,oneside]{article}"); bw.newLine();
            bw.write("\\usepackage{graphicx}"); bw.newLine();
            bw.write("\\usepackage{url}"); bw.newLine();
            bw.write("\\usepackage{subfigure}"); bw.newLine();
            bw.write("\\usepackage{rotating}"); bw.newLine();
            bw.write("\\usepackage{xcolor}"); bw.newLine();
            bw.write("\\usepackage{tikz}"); bw.newLine();
            bw.write("\\usepackage[" + pageOrientation + ",top=3cm, bottom=3cm, left=3cm, right=3cm]{geometry}"); bw.newLine();
            bw.write("\\begin{document}"); bw.newLine();
            bw.write("\\sffamily"); bw.newLine();
            bw.write("\\scriptsize"); bw.newLine();
            bw.write("\\noindent"); bw.newLine();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    public void openPicture(double x, double y) 
    {
        try 
        {
            bw.write("\\begin{tikzpicture}[x=" + Double.toString(x) + "mm, y=" + Double.toString(y) + "mm]"); 
            bw.newLine();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }
    }
    
    public void closePicture() {
        try {
            bw.write("\\end{tikzpicture}"); 
            bw.newLine();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    public void writeTexFooter() {
        try {
            bw.write("\\end{document}"); bw.newLine();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    public void newPage() 
    {
        try 
        {
            bw.write("\\newpage"); 
            bw.newLine();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }       
    }
    
    public void drawLine(double x1, double y1, double x2, double y2, String colour, boolean dashed)
    {
        try
        {
            String dashedString = dashed ? ", dashed" : "";
            bw.write("\\draw[" + colour + dashedString + "] ( " + x1 + "," + y1 + ") -- (" + x2 + "," + y2 + ");");
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
            bw.write("\\draw[" + borderColour + "] (" + x + "," + y + ") -- " + 
                    "(" + (x + width) +","+ y + ") -- " +
                    "(" + (x + width) + "," + (y + height) + ") -- " +
                    "(" + x + "," + (y + height) + ") -- cycle ;");
            bw.newLine();
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
            bw.write("\\filldraw[ fill=" + fillColour + ", draw=" + borderColour + "] (" + x + "," + y + ") rectangle " + 
                    "(" + (x + width) +","+ (y + height) + ");");
            bw.newLine();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }       
    }
    
    public void drawAlignment(double x, double y, double width, double height, String fillColour, String borderColour, int fillLeftPC, int fillRightPC)
    {
         try
        {
            bw.write(  "\\shade[draw=" + borderColour + ", thick, "
                            + "left color=" + fillColour + "!" + (int)fillLeftPC + "!white,"
                            + "right color="+ fillColour + "!" + (int)fillRightPC + "!white] "
                            + "(" + x  + "," + y + ") rectangle "
                            + "(" + (x + width) + "," + (y + height) + ");");
            bw.newLine();
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
            text = text.replace("_", "\\string_");
            String anchorString = "";
            switch(anchor)
            {
                case ANCHOR_LEFT:
                {
                    anchorString = "[anchor=west]";
                    break;
                }
                case ANCHOR_RIGHT:
                {
                    anchorString = "[anchor= east]";
                    break;
                }
            }
            bw.write("\\node " + anchorString + " at (" + x  + "," + y + ") {\\color{" + colour + "}" + text + "};" ); 
            bw.newLine();
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
            String anchorString = "";
            switch(anchor)
            {
                case ANCHOR_LEFT:
                {
                    anchorString = " ,anchor=west";
                    break;
                }
                case ANCHOR_RIGHT:
                {
                    anchorString = " ,anchor= east";
                    break;
                }
            }
            text = text.replace("_", "\\string_");
            bw.write("\\node[rotate=" + angle + anchorString + "] at (" + x  + "," + y + ") {" + text + "};" ); 
            bw.newLine();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }         
    }
    
    public void defineColour(String name, int red, int green, int blue)
    {
         try
        {
            bw.write("\\definecolor{" + name + "}{RGB}{" + red + "," + green + "," + blue + "}");
            bw.newLine();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }         
    }
    
    public void drawKeyContig(double x, double y, double width, double height, String colour, String name)
    {
        try
        {
            bw.write("\\node (rect) at (" + x + "," + y + ") " + 
                            "[shade, left color=white, right color=" + colour + ", draw=" + colour + 
                            ", minimum width=" + width + ", minimum height=" + height + ", label=below:"+ name + "]{};");
            bw.newLine();
        }
        catch (IOException e) 
        {
            System.out.println(e);
        }   
    }
    
    public void drawCurve(double startx, double starty, double endx, double endy, double controlx1, double controly1, double controlx2, double controly2)
    {
         try
        {
            bw.write("\\draw (" + startx + ", " + starty + ") .. controls " + 
                            "(" + controlx1 + ", " + controly1 + ") and (" + controlx2 + ", " + controly2 + ") .. " +
                            "(" + endx + ", " + endy + ");");
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
            filename = filename.replace("_", "\\string_");
            bw.write("\\node" + optionsString + " at (" + x + "," + y + ") {\\includegraphics[width=" + width + "mm, height=" + height + "mm]{" +filename + "}};"); 
            bw.newLine();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        } 
    }
    
    public void drawVerticalGap(int y)
    {
        try
        {
            bw.write("\\vspace{" + Integer.toString(y) + "mm}"); 
            bw.newLine();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }     
    }
    
    public void drawHorizontalGap(int x)
    {
        try
        {
            bw.write("\\hspace{" + Integer.toString(x) + "mm}"); 
            bw.newLine();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }        
    }
    
    public void drawNewline()
    {
        try
        {
            bw.write("\\newline");
            bw.newLine();
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }        
    }
    
    public int  getMaxAlignmentsPerPage()
    {
        return 7;
    }
    
    public void drawCoverageMap(CoverageMapImage coverageMap, double x, double y)
    {
        int nRows = coverageMap.getNumberOfRows();
        int targetSize = coverageMap.getTargetSize();
        String targetName = coverageMap.getTargetName();
        String filename = coverageMap.getFilename();
        
        int height = 100;
        int width = 100;

        openPicture(1,1);

        // draw x-axis labels
        double rowSize = (double)targetSize / (double)nRows;
        for(int r=0; r<nRows; r+=50) 
        {
            int rowY = height - (int)((double)r * ((double)height / (double)nRows));
            drawText(x - 10, y + rowY, Integer.toString((int)(r*rowSize)), Drawer.Anchor.ANCHOR_MIDDLE, "black");
        }

        int rowY = height - (int)((double)(nRows) * ((double)height / (double)nRows));
        drawText(x - 10, y + rowY, Integer.toString(targetSize), Drawer.Anchor.ANCHOR_MIDDLE, "black");
        
        // draw the image
        drawImage(x, y, width, height, filename, "[anchor=south west, inner sep=0pt, outer sep=0pt]");

        // draw the text labels
        int textxPos = width / 2;
        drawText(x + textxPos, y - 5, "Each row represents "+(int)rowSize+" nt", Drawer.Anchor.ANCHOR_MIDDLE, "black");
        drawText(x + textxPos, y - 10, targetName, Drawer.Anchor.ANCHOR_MIDDLE, "black");
        drawTextRotated(x - 20, y + (height/2), "Position in genome (nt)", 90, Drawer.Anchor.ANCHOR_MIDDLE);
        closePicture();
        drawHorizontalGap(10);
    }
    
    public void drawCoverageLong(CoverageMapImage coverageMap, double x, double y, double imageWidth, double imageHeight, int num_dividers)
    {
        int targetSize = coverageMap.getTargetSize();
        String targetName = coverageMap.getTargetName();
        String filename = coverageMap.getFilename();
        double unit = (double)imageWidth / targetSize;                

        openPicture(1, 1);

        for (int i=0; i <= num_dividers; i++) 
        {
            int num = i == num_dividers ? targetSize: (int)((targetSize / num_dividers) * i);
            int pos = (int)((double)num * unit);

            drawText(x + pos, y + imageHeight + 2, Integer.toString(num), Drawer.Anchor.ANCHOR_MIDDLE, "black");
            drawLine(x + pos, y + imageHeight + 1, x + pos, y + imageHeight - 1, "black", false);
        }

        drawImage(x, y, imageWidth, imageHeight, filename, "[anchor=south west, inner sep=0pt, outer sep=0pt]");
        drawText(x + imageWidth + 10, y + (imageHeight/2), targetName, Drawer.Anchor.ANCHOR_MIDDLE, "black");
        closePicture();
        drawVerticalGap(5);
        drawNewline();        
    }
    
    public void drawScale(HeatMapScale heatMapScale, double x, double y)
    {
        int height = 4;
        int width = 50;
        
        openPicture(1,1);
        drawImage(x, y, width, height, "heatmap.png", "[anchor=south west, inner sep=0pt, outer sep=0pt]");
        drawText(x + (width/2), y + 5, "Coverage", Drawer.Anchor.ANCHOR_MIDDLE, "black");
        drawText(x, y + 5, "0", Drawer.Anchor.ANCHOR_MIDDLE, "black");
        drawText(x + width, y + 5, Integer.toString(heatMapScale.getHeatMapSize()), Drawer.Anchor.ANCHOR_MIDDLE, "black");
        closePicture();
        drawVerticalGap(5);
        drawNewline();          
    }
    
    public int getPageHeight()
    {
        return 175;
    }
}
