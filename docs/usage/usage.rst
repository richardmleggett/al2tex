Usage
==========

Alvis can be run from the command line using the .jar file. In the terminal, navigate to the dist directory in the Alvis project, and type e.g: ::

	Java -jar alvis.jar -type alignment -inputfmt paf -outputfmt tex \
		-in /path/to/alignments/alignments.paf -outdir /path/to/out/ -out prefix

This will create the file prefix_alignment.tex in the directory /path/to/out. The file prefix_alignment.tex can then be compiled by your favourite tex compiler and viewed as a pdf. The options used in this command form the minimum set required for Alvis to work, and are described in the following table.

+-------------------------+---------------------------------------+
| ``-type <diagram>``     |   Type of diagram to be produced.     |
+-------------------------+---------------------------------------+
| ``-inputfmt <format>``  |   Format of the alignment file to use.|
+-------------------------+---------------------------------------+
| ``-outputfmt <format>`` |   Format to output the diagram in.    |
+-------------------------+---------------------------------------+
| ``-in <path>``          |  Full file path to alignment file.    |
+-------------------------+---------------------------------------+
| ``-outdir <dir>``       |  Directory to write output to.        |
+-------------------------+---------------------------------------+
| ``-out <prefix>``       |  Prefix to use for output file names. |
+-------------------------+---------------------------------------+

The following are optional.

+----------------------------------------------+---------------------------------------------+
| ``-filter``                                  | Filter small alignments to remove noise.    |
+----------------------------------------------+---------------------------------------------+
| ``-minAlignmentProp <int>``                  | Minimum size of alignments, as a percent of |
|                                              | the query length, to keep when filtering.   |
+----------------------------------------------+---------------------------------------------+
| ``-chimeras``                                | Only display alignments from chimeras.      |
+----------------------------------------------+---------------------------------------------+
| ``-alignmentQueryName <query sequence id>``  | ID of query sequence for exapnded contig    |
|                                              | alignment diagram.                          |
+----------------------------------------------+---------------------------------------------+
| ``-alignmentTargetName <target sequence id>``| ID of target sequence for expanded contig   |
|                                              | alignment diagram.                          |
+----------------------------------------------+---------------------------------------------+
| ``-blastfmt <format string>``                | Format string as given to blast. Can be used|
|                                              | if ``-inputfmt`` is ``blast``.              |
+----------------------------------------------+---------------------------------------------+
| ``-tsizes <"string">``                       | List of target names and sizes, separated by|
|                                              | whitspace. Required if ``inputfmt`` is      |
|                                              | ``sam``.                                    |
+----------------------------------------------+---------------------------------------------+
| ``-binsize <int>``                           | Size (in bp) of bins for coverage diagrams. |
+----------------------------------------------+---------------------------------------------+
| ``-coverageType  <type>``                    | The type of coverage heatmap to produce.    |
|                                              | Must be either "square" or "long".          |
+----------------------------------------------+---------------------------------------------+


Diagrams
---------

The type of diagram is specified with the ``-type <diagram>`` option etc. The currently available diagrams are:

- ``alignment``
- ``contigAlignment``
- ``coveragemap``
- ``genomecoverage``
- ``pileup``

Detailed information for each diagram can be found in the :doc:`diagrams` section.

Input Formats
-------------

Alvis accepts a variety of alignment formats. These are specified by the option ``-inputfmt <format>``, where ``<format>`` is one of the following:

- ``paf``
- ``psl``
- ``blast``
- ``coords``
- ``tiling``
- ``sam``
- ``pileup``

Coords
......

A coords file can be created from a mummer .delta file using the ``show-coords`` command in the MUMer package. For this file to work with alvis, the ``-B`` option must be specified (see `here <http://mummer.sourceforge.net/manual/#coords/>`_ for more details).

Tiling
......

Similarly, a tiling file can be created from a mummer .delta file using the ``show-tiling`` command. In this case, the ``-a`` option must be specified (see `here <http://mummer.sourceforge.net/manual/#tiling/>`_ for more details).

Blast
.....

If blast is given as the format, the input file must have been created by blast using the tabular option. By default, Alvis will assume that the blast file was not created with a user defined format, but with just ``-outfmt 6``. If a user defined format was used, the parameter passed to blast after the ``-outfmt`` option must also be given to Alvis after the -blastfmt option. For example, the following command could be used: ::

	Java -jar alvis.jar -type alignment -inputfmt blast -outputfmt tex \
		-in /path/to/alignments/alignments.blast -outdir /path/to/out/ -out prefix \
		-blastfmt '6 qseqid sseqid qstart qend sstart ssend'

if the file alignment.blast was created with:: 

	blastn -db nt -query query.fa -out alignments.blast -outfmt '6 qseqid sseqid qstart qend sstart ssend'

If a user defined format is used, the following fields must be present in some order:

- ``qseqid``
- ``sseqid``
- ``qstart``
- ``qend``
- ``sstart``
- ``send``

SAM
....
When using a SAM file, Alvis will attempt to find the target contig sizes from the header section. If this unavailable, the user can supply these values through the ``-tsizes`` option, by typing a space-separaed list of target names and their sizes. E.g. ``-tsizes 'Chr1 34964571 Chr2 22037565 Chr3 25499034 Chr4 20862711 Chr5 31270811'``.

Output Formats
--------------

alvis can currently output most diagrams in two formats: SVG and laTeX. These are specified by the ``-outputfmt <format>`` option, where ``<format>`` is one of ``tex`` and ``svg``. Note that if ``tex`` is specified, the user must compile the .tex file that is created to obtain a pdf. Currently the diagrams are drawn with the tikz library, so the user must have this installed.

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

Filtering
----------

The user can filter alignments using the ``-filter`` option. This will cause alvis to ignore all alignments with length less than ``-minAlignmentProp`` % of the reference contig size (set to 0.5% by default). Note that this option is currently only used by the alignment diagram and the contig alignment diagram.

When using the ``-chimera`` option in conjunction with the contig alignment diagram, alvis will display only those alignments that it thinks could be a chimera.

.. image:: images/chimera_example.png


