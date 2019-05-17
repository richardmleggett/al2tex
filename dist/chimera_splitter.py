#!/usr/bin/python

import sys, getopt, random
import time
import errno

isFasta = True
chimeraFilename = "chimeras.txt"
inputFilename = ""
outputFilename = ""

def printHelp():
	print "chimeraSplitter.py -i <input fasta/fastq> -c <chimera file> -o <output fasta/fastq>"
	print "\tSplits input reads that appear in the chimera file at the join location indicated. Writes all reads to file given by -o."
	print "\tThe chimera file must be the file 'chimeras.txt' as output by Alvis."
	print "\tSequences in the input fasta file must be on one line."

try:
	opts, args = getopt.getopt(sys.argv[1:],"hi:c:o:")
except getopt.GetoptError:
	print "Option not recognised."
	print "chimeraSplitter.py -i <input fast/fastq> -c <chimera file> -o <output fasta/fastq>"
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
	with open (inputFilename, 'r') as inputFile:
		firstChar = inputFile.read(1)
		if firstChar == '>':
			isFasta = True
		elif firstChar == '@':
			isFasta = False
		else:
			print "Could not recognise format of " + inputFilename + "."
			print "Please ensure this is either fasta or fastq."
			sys.exit(2)
except (OSError, IOError) as e: 
	if getattr(e, 'errno', 0) == errno.ENOENT:
		print "Could not open file " + inputFilename
		sys.exit(2)

def splitFasta(inputFasta, outputFasta):
	count = 1
	isChimera = False
	readID = ""
	headerFields = ""
	for line in inputFasta:
		if count % 2 == 1:
			headerFields = line.split()
			readID = headerFields[0][1:]
			# Check to see if this read is in the list of chimeras 
			if readID in chimeras.keys():
				isChimera = True
			else:
				isChimera = False
		else:
			if isChimera:
				splittingPoint = chimeras[readID]
				if splittingPoint > len(line):
					print "Warning: line in chimeras.txt suggests chimera at position greater than sequence length."
					print "Ignoring this line."
					isChimera = False
			if isChimera:
				header1 = ">" + readID + "_chimera_1"
				header2 = ">" + readID + "_chimera_2"
				for j in xrange(1, len(headerFields)):
					header1 += " " + headerFields[j]
					header2 += " " + headerFields[j]
				outputFasta.write(header1 + "\n")
				outputFasta.write(line[:splittingPoint] + "\n")
				outputFasta.write(header2 + "\n")
				outputFasta.write(line[splittingPoint:])
			else:
				header = " ".join(headerFields)
				outputFasta.write(header + "\n")
				outputFasta.write(line)
		count += 1

def splitFastq(inputFastq, outputFastq):
	count = 1
	isChimera = False
	readID = ""
	headerFields = ""
	sequence1 = ""
	sequence2 = ""
	for line in inputFastq:	
		if count % 4 == 1:
			headerFields = line.split()
			readID = headerFields[0][1:]
			# Check to see if this read is in the list of chimeras 
			if readID in chimeras.keys():
				isChimera = True
			else:
				isChimera = False
		elif count % 4 == 2:
			if isChimera:
				splittingPoint = chimeras[readID]
				if splittingPoint > len(line):
					print "Warning: line in chimeras.txt suggests chimera at position greater than sequence length."
					print "Ignoring this line."
					sequence1 = line
					isChimera = False
				else:
					sequence1 = line[:splittingPoint] + "\n"
					sequence2 = line[splittingPoint:]
			else:
				sequence1 = line	
		elif count % 4 == 0:
			if isChimera:
				splittingPoint = chimeras[readID]
				header1 = "@" + readID + "_chimera_1"
				header2 = "@" + readID + "_chimera_2"
				for j in xrange(1, len(headerFields)):
					header1 += " " + headerFields[j]
					header2 += " " + headerFields[j]
				outputFastq.write(header1 + "\n")
				outputFastq.write(sequence1)	
				outputFastq.write("+\n")
				outputFastq.write(line[:splittingPoint] + "\n")
				outputFastq.write(header2 + "\n")
				outputFastq.write(sequence2)	
				outputFastq.write("+\n")
				outputFastq.write(line[splittingPoint:])
			else:
				header = " ".join(headerFields)
				outputFastq.write(header + "\n")
				outputFastq.write(sequence1)	
				outputFastq.write("+\n")
				outputFastq.write(line)	
		count += 1		

try:
	# write the new fasta file with chimeras split into seperate reads		
	with open(inputFilename, 'r') as inputFile:
		with open(outputFilename, 'w') as outputFile:
			if isFasta:
				splitFasta(inputFile, outputFile)
			else:
				splitFastq(inputFile, outputFile)
except (OSError, IOError) as e: 
	if getattr(e, 'errno', 0) == errno.ENOENT:
		print "Could not open file " + inputFilename
		sys.exit(2)