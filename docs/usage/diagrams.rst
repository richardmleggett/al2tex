Diagrams
=================

Alignment Diagram
-----------------

The alignment diagram draws alignments between a query sequence (drawn in red), and target (or reference) sequences. Each target sequence is represented by a line underneath the query sequence, with the alignment drawn as a box aligned to the query sequence.


.. image:: images/alignment_example.png

This diagram type accepts the following input formats

- paf
- psl
- coords

and outputs the following formats

- tex.

Contig Alignment Diagram
------------------------

For each target contig, the Contig Alignment Diagram draws the most prominent alignments inside a rectange representing the query contig. The alignments are colour coded by target contig, and shaded to give an indication of the position in the target, and the orientation of the alignment.

.. image:: images/contig_alignment_example.png

This diagram type accepts the following input formats

- paf
- psl
- coords

and outputs the following formats

- tex
- svg.

If in addition, the user specifies a query contig and a reference contig, a detailed diagram containing only these alignments is produced.

.. image:: images/detailed_contig_alignment_example.png

Coverage Map Diagram
--------------------

Alignments are binned based on their position in the target contigs. For each target contig a heatmap image is produced showing the coverage. These are arranged in a tex or svg file.

.. image:: images/coverage_map_example.png

This diagram type accepts the following input formats

- paf
- psl
- coords
- sam

and outputs the following formats

- tex
- svg.