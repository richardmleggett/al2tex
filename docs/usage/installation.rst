Installation
==============

Alvis requires Java to run, and a LaTeX compiler to create PDFs from the .tex files that Alvis produces, or a browser to view the .svg files that Alvis produces. First, download the repository from https://github.com/SR-Martin/alvis.git.

A .jar file is included with Alvis, and is all that is necessary to run Alvis. This can be found at ``/path/to/Alvis/dist/alvis.jar``, usage instructions are in the :doc:`usage` section.

If you wish to compile Alvis yourself, a Makefile is in the ``src`` directory. To create the .jar, in the terminal, type::

	cd /path/to/Alvis/src
	make
	jar cvf ../dist/Alvis.jar Alvis/Alvis.class
