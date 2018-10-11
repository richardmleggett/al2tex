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
    
    public TikzDrawer(String f) {
        filename = f + ".tex";
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
            bw.write("\\documentclass[a4paper,11pt,oneside]{article}"); bw.newLine();
            bw.write("\\usepackage{graphicx}"); bw.newLine();
            bw.write("\\usepackage{url}"); bw.newLine();
            bw.write("\\usepackage{subfigure}"); bw.newLine();
            bw.write("\\usepackage{rotating}"); bw.newLine();
            bw.write("\\usepackage{xcolor}"); bw.newLine();
            bw.write("\\usepackage{tikz}"); bw.newLine();
            bw.write("\\usepackage[landscape,top=3cm, bottom=3cm, left=3cm, right=3cm]{geometry}"); bw.newLine();
            bw.write("\\begin{document}"); bw.newLine();
            bw.write("\\sffamily"); bw.newLine();
            bw.write("\\scriptsize"); bw.newLine();
            bw.write("\\noindent"); bw.newLine();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    public void openPicture(double x, double y) {
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
    
    public void newPage() {
        try {
            bw.write("\\newpage"); bw.newLine();
        } catch (IOException e) {
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
    
    public void drawText(double x, double y, String text)
    {
        try
        {
            text = text.replace("_", "\\string_");
            bw.write("\\node at (" + x  + "," + y + ") {" + text + "};" ); 
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
            text = text.replace("_", "\\string_");
            bw.write("\\node[rotate=" + angle + "] at (" + x  + "," + y + ") {" + text + "};" ); 
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
    
    public void drawNewPage()
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
    
    public int  getMaxAlignmentsPerPage()
    {
        return 7;
    }
}
