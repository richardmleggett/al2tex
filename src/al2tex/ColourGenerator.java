/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package al2tex;

import java.util.*;
import java.awt.Color;

/**
 *
 * @author martins
 */
public class ColourGenerator {
    ArrayList<Color> m_colours;
    int m_numberColours;
    
    ColourGenerator(int n, boolean randomStart, float saturation, float brightness) 
    {
        assert(n>0);
        assert(0 <= saturation && saturation < 1.);
        assert(0 <= brightness && brightness < 1.);
        // generate the colours based on n evenly spaced hue's.
        m_colours = new ArrayList();
        m_numberColours = n;
        Random rng = new Random();
        int startHue = randomStart ? rng.nextInt(360) : 0;
        int delta = 360 / n;
        // find some number that is relatively prime to n
        // so that the order of colours is interesting
        int relativePrime = rng.nextInt(n) + 1;
        while(gcd(n, relativePrime) != 1)
        {
            relativePrime = rng.nextInt(n) + 1;
        }
        for(int i = 0; i < n; i++)
        {
            float hue = (float)((startHue + i * delta * relativePrime) % 360) /  360;
            m_colours.add(Color.getHSBColor(hue, saturation, brightness));
        }
    }
    
    public Color getColour(int i)
    {
        assert(0<= i && i < m_numberColours);
        return m_colours.get(i);
    }
    
    //srsly?
    private static int gcd(int p, int q) 
    {
        if (q == 0) {
            return p;
        }
        return gcd(q, p % q);
    }
    
}
