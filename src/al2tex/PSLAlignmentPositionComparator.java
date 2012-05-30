package al2tex;

import java.util.*;

public class PSLAlignmentPositionComparator implements Comparator<PSLAlignment> {
    @Override
    public int compare(PSLAlignment a, PSLAlignment b) {
        return a.getTargetStart() - b.getTargetStart();
    }
}