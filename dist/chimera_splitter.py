#!/usr/bin/python

import sys, getopt, random
import time
import errno

chimeraFilename = "chimeras.txt"
inputFilename = ""
outputFilename = ""

def printHelp():
	print "chimeraSplitter.py -i <input fasta> -c <chimera file> -o <output fasta>"
	print "\tSplits input reads that appear in the chimera file at the join location indicated. Writes all reads to file given by -o."
	print "\tThe chimera file must be the file 'chimeras.txt' as output by Alvis."
	print "\tSequences in the input fasta file must be on one line."


try:
	opts, args = getopt.getopt(sys.argv[1:],"hi:c:o:")
except getopt.GetoptError:
	print "Option not recognised."
	print "chimeraSplitter.py -i <input fasta> -c <chimera file> -o <output fasta>"
	sys.exit(2)
for opt, arg in opts:
	if opt == "-h":
		printHelp()
		sys.exit()
	elif opt in ("-i"):
		inputFilename = arg
	elif opt in ("-o"):
		outputFilename = arg
	elif opt in ("-c"):
		chimeraFilename = arg

if not inputFilename or not outputFilename:
	print "Error: Must provide an input fasta file and an output filename.\n"
	printHelp()
	sys.exit()

chimeras = dict()
# try to open and read the file
try:
	with open(chimeraFilename, 'r') as chimeraFile:
		# organize the alignments, and filter out poor quality ones
		for line in chimeraFile:
			fields = line.split()
			queryName = fields[0]
			splittingPoint = int(fields[1])
			chimeras[queryName] = splittingPoint

except (OSError, IOError) as e: 
	if getattr(e, 'errno', 0) == errno.ENOENT:
		print "Could not open file " + chimeraFilename
		sys.exit(2)
try:
	# write the new fasta file with chimeras split into seperate reads		
	with open(inputFilename, 'r') as fastaFile:
		with open(outputFilename, 'w') as outputFile:
			count = 1
			isChimera = False
			readID = ''
			headerFields = ""
			for line in fastaFile:
				if count % 2 == 1:
					headerFields = line.split()
					readID = headerFields[0][1:]
					# Check to see if this read is in the list of chimeras 
					if readID in chimeras.keys():
						isChimera = True
					else:
						isChimera = False
						outputFile.write(line)
				else:
					if isChimera:
						splittingPoint = chimeras[readID]
						chimeras.pop(readID)
						header1 = ">" + readID + "_chimera_1 "
						header2 = ">" + readID + "_chimera_2 "
						for j in xrange(1, len(headerFields)):
							header1 += " " + headerFields[j]
							header2 += " " + headerFields[j]
						header1 +="\n"
						header2 +="\n"
						outputFile.write(header1)
						outputFile.write(line[:splittingPoint] + "\n")
						outputFile.write(header2)
						outputFile.write(line[splittingPoint:])
					else:
						outputFile.write(line)	
				count += 1	
except (OSError, IOError) as e: 
	if getattr(e, 'errno', 0) == errno.ENOENT:
		print "Could not open file " + fastaFile
		sys.exit(2)

if len(chimeras.keys()) > 0:
	missingReads = ""
	for read in chimeras.keys():
		missingReads += read + ", "
	print "Could not find chimeric reads " + missingReads
