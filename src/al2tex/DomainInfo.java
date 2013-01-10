package al2tex;

public class DomainInfo {
    private String geneId = "";
    private int nbarcStart = 0;
    private int nbarcEnd = 0;
    private int lrrStart = 0;
    private int lrrEnd = 0;
    
    public DomainInfo(String i, int ns, int ne, int ls, int le) {
        geneId = i;
        nbarcStart = ns;
        nbarcEnd = ne;
        lrrStart = ls;
        lrrEnd = le;
    }
    
    public String getGeneId() {
        return geneId;
    }
    
    public int getNBARCStart() {
        return nbarcStart;
    }
    
    public int getNBARCEnd() {
        return nbarcEnd;
    }
    
    public int getLRRStart() {
        return lrrStart;
    }
    
    public int getLRREnd() {
        return lrrEnd;
    }
}
