/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package al2tex;

import java.util.*;

/**
 *
 * @author martins
 */
public class ContigAlignmentDiagram 
{
    private TreeMap<String, ArrayList<PAFAlignment>> m_alignmentMap;
    Set<String> m_refNames;
    private TikzContigAlignment m_tca;
    private double m_minAlignmentProp;
    
    public ContigAlignmentDiagram(PAFFile pafFile, String outFilename, double minAlignmentProp)
    {
        m_alignmentMap = new TreeMap<String, ArrayList<PAFAlignment>>();
        m_minAlignmentProp = minAlignmentProp;
        m_refNames = new LinkedHashSet();
        // iterate through all the alignments and group by contig name
        // filter out really small alignments
        for(int i = 0; i < pafFile.getNumberOfAlignments(); ++i)
        {
            PAFAlignment alignment = pafFile.getAlignment(i);
            if((double)alignment.getQueryAlignmentLength() / alignment.getQueryContigLength() >= m_minAlignmentProp)
            {
                String name = alignment.getQueryName();
                if(!m_alignmentMap.containsKey(name))
                {
                    m_alignmentMap.put(name, new ArrayList<PAFAlignment>());
                }
                m_alignmentMap.get(name).add(alignment);
                m_refNames.add(alignment.getRefName());
            }
        }
        
        // sort each array of alignments by start pos
        for(ArrayList<PAFAlignment> PAFAlignments : m_alignmentMap.values())
        {
            Collections.sort(PAFAlignments, PAFAlignment.compareByQueryStart);
        }
        
        // create TikzPicture object
        m_tca = new TikzContigAlignment(outFilename);
    }
    
    public void writeTexFile(DiagramOptions options)
    {
        m_tca.openFile();
        
        // write the colours
        int refNumber = m_refNames.size();
        ColourGenerator colourGenerator = new ColourGenerator(refNumber, true, 0.9f, 0.7f);
        Iterator<String> iter = m_refNames.iterator();
        int colourCount = 0;
        while(iter.hasNext())
        {
            m_tca.setColourForContig(iter.next(), colourGenerator.getColour(colourCount));
            colourCount++;
        }
        
        // do the picture
        m_tca.openPicture();
        
        if(options.getAlignmentQueryName() != null && options.getAlignmentRefName() != null)
        {
            // do the stuff
            System.out.println("Drawing contig in detail.");
            String queryName = options.getAlignmentQueryName();
            String refName = options.getAlignmentRefName();
            ArrayList<PAFAlignment> alignmentsToDraw = new ArrayList();
            ArrayList<PAFAlignment> queryAlignments = m_alignmentMap.get(queryName);
            for(int i = 0; i < queryAlignments.size(); i++)
            {
                PAFAlignment alignment = queryAlignments.get(i);
                if(alignment.getRefName().equals(refName))
                {
                    alignmentsToDraw.add(alignment);
                }
            }
            m_tca.drawAlignmentDiagram(alignmentsToDraw, 0, 7 * 175);
        }
        else
        {        
            m_tca.drawKey(100,0);
            int i = 7;
            for(ArrayList<PAFAlignment> PAFAlignments : m_alignmentMap.values())
            {
                // make a new diagram on a new page
                // TODO: 8 is a magic number
                if(i == 0)
                {
                    m_tca.closePicture();
                    m_tca.writeNewPage();
                    m_tca.openPicture();
                    m_tca.drawKey(100,0);
                    i = 7;
                }

                m_tca.drawContig(PAFAlignments, 0, i * 175 );
                i--;
            }
        }
        m_tca.closePicture();
        m_tca.closeFile();
    }
    
}
