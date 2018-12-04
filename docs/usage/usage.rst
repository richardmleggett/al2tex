Usage
==========

alvis is run from the command line etc. Something about jars, maybe make a run script?

The following options are mandatory.

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

The following options are optional.

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
| ``-blastfmt <format string>``                | Format string as given to blast. Required   |
|                                              | if ``-inputfmt`` is ``blast``.              |
+----------------------------------------------+---------------------------------------------+
| ``-tsize <int>``                             | Size (in bp) of target contig. Required if  |
|                                              | ``inputfmt`` is ``sam``.                    |
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

If blast is given as the format, the input file must have been created by blast using the tabular option, i.e. with "-outfmt 6" specified. Furthermore, the following fields must be present in some order:

- ``qseqid``
- ``sseqid``
- ``qstart``
- ``qend``
- ``sstart``
- ``send``

The parameter passed to blast after the ``-outfmt`` option must also be given to alvis after the -blastfmt option (e.g. ``-blastfmt '6 qseqid sseqid qstart qend sstart ssend'``).

SAM
....
When using a SAM file, the ``-tSize`` option must be set to the size of the target.

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


