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
public class TikzPicture 
{
    protected String filename;
    protected BufferedWriter bw;
    
    public TikzPicture(String f) {
        filename = f;
        
        if (!filename.endsWith(".tex") && !filename.endsWith(".Tex") && !filename.endsWith(".TEX")) {
            filename += ".tex";
        }
    }
    
    public void openFile() {
        try {
            bw = new BufferedWriter(new FileWriter(filename));
            writeTexHeader();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    public void closeFile() {
        try {
            writeTexFooter();
            bw.flush();
            bw.close();
        } catch (IOException e) {
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
    
    public void openPicture() {
        try {
            bw.write("\\begin{tikzpicture}[x=0.1mm,y=0.1mm]"); bw.newLine();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    public void closePicture() {
        try {
            bw.write("\\end{tikzpicture}"); bw.newLine();
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
    
    public void writeNewPage() {
        try {
            bw.write("\\newpage"); bw.newLine();
        } catch (IOException e) {
            System.out.println(e);
        }       
    }
    
}
