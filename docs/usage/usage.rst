Usage
==========

al2tex is run from the command line etc.

Here are the options


Input Formats
-------------

al2tex accepts a variety of alignment formats. These are specified by the option ``-inputfmt <format>``, where ``<format>`` is one of the following:

- paf
- psl
- blast
- coords
- tiling
- sam
- pileup

If blast is given as the format, the input file must have been created by blast using the tabular option, i.e. with "-outfmt 6" specified. Furthermore, the following fields must be present in some order:

- qseqid
- sseqid
- qstart
- qend
- sstart
- send

The parameter passed to blast after the ``-outfmt`` option must also be given to al2tex after the -blastfmt option (e.g. ``-blastfmt '6 qseqid sseqid qstart qend sstart ssend'``).

Diagrams
---------

The type of diagram is specified with the ``-type`` option etc.

Output Formats
--------------

al2tex can currently output most diagrams in two formats: SVG and laTeX. These are specified by the ``-outputfmt <format>`` option, where ``<format>`` is one of ``tex`` and ``svg``. Note that if ``tex`` is specified, the user must compile the .tex file that is created to obtain a pdf. Currently the diagrams are drawn with the tikz library, so the user must have this installed.

The following table shows the accepted input and output formats for each diagram.

+---------------------------+----------------------------------------------------+-------------------+
|                           |                      Input Formats                 |  Output Formats   |
|                           +-------+--------+--------+-----+--------+-----+-----+---------+---------+
|                           | blast | coords | tiling | paf | pileup | psl | sam |   svg   |   tex   |
+===========================+=======+========+========+=====+========+=====+=====+=========+=========+
| Alignment Diagram         |   ✓   |   ✓    |   ✓    |  ✓  |        |  ✓  |     |    ✓    |    ✓    |
+---------------------------+-------+--------+--------+-----+--------+-----+-----+---------+---------+
| Contig Alignment Diagram  |   ✓   |   ✓    |   ✓    |  ✓  |        |  ✓  |     |    ✓    |    ✓    |
+---------------------------+-------+--------+--------+-----+--------+-----+-----+---------+---------+
| Coverage Map Diagram      |   ✓   |   ✓    |   ✓    |  ✓  |        |  ✓  |  ✓  |    ✓    |    ✓    |
+---------------------------+-------+--------+--------+-----+--------+-----+-----+---------+---------+
| Genome Coverage Diagram   |   ✓   |   ✓    |   ✓    |  ✓  |        |  ✓  |  ✓  |    ✓    |         |
+---------------------------+-------+--------+--------+-----+--------+-----+-----+---------+---------+
| Pileup Coverage Diagram   |       |        |        |     |    ✓   |     |     |         |    ✓    |
+---------------------------+-------+--------+--------+-----+--------+-----+-----+---------+---------+

