package Alvis;

import java.util.*;
import java.io.*;
import java.lang.*;

public class DomainInfoFile {
    private Hashtable<String,DomainInfo> domainInfo = new Hashtable();

    public DomainInfoFile(String filename) {
        String line;
        
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(filename));

            while ((line = br.readLine()) != null) {
                String[] fields = line.split("\t");
                DomainInfo di = new DomainInfo(fields[0],
                                               Integer.parseInt(fields[1]),
                                               Integer.parseInt(fields[2]),
                                               Integer.parseInt(fields[3]),
                                               Integer.parseInt(fields[4]));
                domainInfo.put(fields[0], di);
            }
            
            br.close();
        } catch (Exception ioe) {
            System.out.println("Exception:");
            System.out.println(ioe);
        }
    }
    
    public DomainInfo getDomainInfo(String s) {
        return domainInfo.get(s);
    }
}
