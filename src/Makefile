JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
        Alvis/AlignmentFiles/Alignment.java \
        Alvis/AlignmentFiles/AlignmentFile.java \
        Alvis/Diagrams/CoverageMapImage.java \
        Alvis/Drawers/Drawer.java \
        Alvis/Drawers/TikzDrawer.java \
        Alvis/Drawers/SVGDrawer.java \
        Alvis/Drawers/ColourGenerator.java \
        Alvis/AlignmentFilters/OverlapFilter.java \
        Alvis/AlignmentFilters/MinPropFilter.java \
        Alvis/AlignmentFilters/ChimeraFilter.java \
        Alvis/AlignmentFilters/BestAlignmentPerQueryFilter.java \
        Alvis/AlignmentFilters/AlignmentFilter.java \
        Alvis/AlignmentFiles/SAMAlignment.java \
        Alvis/AlignmentFiles/AlignmentSorter.java \
        Alvis/AlignmentFiles/SAMFile.java \
        Alvis/AlignmentFiles/PileupFile.java \
        Alvis/AlignmentFiles/PSLFile.java \
        Alvis/AlignmentFiles/PSLAlignment.java \
        Alvis/AlignmentFiles/PAFFile.java \
        Alvis/AlignmentFiles/PAFAlignment.java \
        Alvis/AlignmentFiles/MummerFile.java \
        Alvis/AlignmentFiles/MummerAlignment.java \
        Alvis/AlignmentFiles/DetailedAlignmentFile.java \
        Alvis/AlignmentFiles/DetailedAlignment.java \
        Alvis/AlignmentFiles/BlastFile.java \
        Alvis/AlignmentFiles/BlastAlignment.java \
        Alvis/Diagrams/PileupImage.java \
        Alvis/Diagrams/PileupCoverageDiagram.java \
        Alvis/Diagrams/PileupContig.java \
        Alvis/Diagrams/HeatMapScale.java \
        Alvis/Diagrams/GenomeCoverageImage.java \
        Alvis/Diagrams/GenomeCoverageDiagram.java \
        Alvis/Diagrams/CoverageMapDiagram.java \
        Alvis/Diagrams/ContigAlignmentDiagram.java \
        Alvis/Diagrams/AlignmentDiagram.java \
        Alvis/Alvis.java \
        Alvis/DiagramOptions.java \
        Alvis/DomainInfo.java \
        Alvis/DomainInfoFile.java \

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class