import sys, array
from optparse import OptionParser , IndentedHelpFormatter
from collections import deque

import pysam

class COORD(object):
	def __init__(self, chr, start, stop, dir, name):
		self.chr = chr
		self.start = start
		self.stop = stop
		self.dir = dir
		self.name = name

def parseBAM(CONFIG, ALLCOORD, size):
        samfile = pysam.Samfile(CONFIG.bam, "rb" )
	
	outname = CONFIG.output + ".out"
	if CONFIG.type == "CDT":
		outname = CONFIG.output + ".cdt"
	out = open(outname, 'w')
	if CONFIG.type == "CDT":
		out.write("YORF\tNAME\t")
		for x in range(0, size):
			out.write(str(x))
			if(x != size - 1):
				out.write("\t")
		out.write("\n")

	for coord in ALLCOORD:
		out.write(coord.name + "\t")
		if CONFIG.type == "CDT":
			out.write(coord.name + "\t")

		length = (coord.stop - coord.start)
		TAG = array.array('i', (0,) * length)

		#FETCH coordinate start minus shift to stop plus shift
		iter = samfile.fetch(coord.chr, coord.start - CONFIG.shift - 1, coord.stop + CONFIG.shift + 1)
		for x in iter:
			#Gbrowse is 1-based
			#FivePrime = x.pos + 1
			#BED-BAM is 0-based
			if x.is_read1:
				FivePrime = x.pos
				
				#Adjust reverse strand alignments for five-prime pileup
				if x.is_reverse:
					#Gbrowse is 1-based
					FivePrime += x.rlen
					#SHIFT DATA HERE IF NECCESSARY
					FivePrime -= CONFIG.shift
				else:
					FivePrime += CONFIG.shift
				FivePrime -= coord.start
	
				#check for strandedness here
				if CONFIG.strand == 'F':
					if not x.is_reverse and coord.dir == '-':
						FivePrime = -999
	        	                elif x.is_reverse and coord.dir == '+':
						FivePrime = -999
				elif CONFIG.strand == 'R':
					if not x.is_reverse and coord.dir == '+':
						FivePrime = -999
					elif x.is_reverse and coord.dir == '-':
						FivePrime = -999
				
				#Increment Final Array keeping track of pileup
				if FivePrime >= 0 and FivePrime < len(TAG):
					TAG[FivePrime] += 1
		if coord.dir == "-":
			TAG.reverse()
		#output final pileup array here
		for z in range(len(TAG)):
			out.write(str(TAG[z]))
			if z + 1 != len(TAG):
				out.write("\t")
		out.write("\n")
	samfile.close()
	out.close()

#Test to make sure all coordinates fall within appropriate chromsomal bounds as defined by BAM file
def confirmCoord(bam, allcoord):
	#open BAM file
	samfile = pysam.Samfile( bam, "rb" )
	print "Testing to confirm valid coordinates...";
	REF = samfile.references
	LEN = samfile.lengths
	width = len(allcoord)
	i = 0
	while i < width and i >= 0:
		coord = allcoord[i]
		FAIL = 0
		CHROMCHECK = 0
		for x in range(len(REF)):
			if coord.chr == REF[x]:
				CHROMCHECK = 1
				if coord.stop > LEN[x]:
					print "Invalid Coordinate!!!\nCoordinate: ",coord.chr," ",coord.start," ",coord.stop,"\nGenome: ",REF[x]," ",LEN[x]
					FAIL = 1
					x = len(REF) + 1
		if CHROMCHECK == 0:
			print "Chromosome Not Found!!!\n",coord.chr," ",coord.start," ",coord.stop
			allcoord.pop(i)
			width -= 1
		elif FAIL == 1:
			allcoord.pop(i)
			width -= 1
		else:
			i += 1
	print len(allcoord)," Coordinates to Extract"
	if(len(allcoord) < 1):
		print "No Coordinates to Extract. Quitting..."
		sys.exit(1);
	samfile.close()

#Load up BED File function here
def loadBED(bed, ALLCOORD):
	reader = open(bed, "r")
	size = 0
	for line in reader:
		if "track" in line:
			continue
		elif "browser" in line:
			continue
		else:
			line = line.rstrip('\n')
			array = line.split()
			#test to confirm coordinates are integers and file seems bed format
			if len(array) < 2 or len(array) > 11:
				print "ERROR!!! Invalid Line: ",line
			elif not(array[1].isdigit()) or not(array[2].isdigit()) or (int(array[1]) < 0) or (int(array[2]) < 0):
			        print "ERROR!!! Non-Positive Integer Coordinates!",line
			elif(int(array[1]) > int(array[2])):
				print "ERROR!!! Start coordinate larger than stop coordinate!",line
			elif(len(array) >= 6 and array[5] != "+" and array[5] != "-"):
                                print "ERROR!!! Invalid Strand Type! (+ or - only)",line
			else:
				dir = "+"
				if len(array) >= 6:
					dir = array[5]
				NAME = ""
				if len(array) < 3:
					NAME = array[0] + "_" + array[1] + "_" + array[2] + "_" + dir
				else:
					NAME = array[3]
				ALLCOORD.append(COORD(array[0], int(array[1]), int(array[2]), dir, NAME))
				if int(array[2]) - int(array[1]) > size:
					size = int(array[2]) - int(array[1])
	reader.close()
	#Test to make sure > 0 Coordinates to Extract
        print len(ALLCOORD)," Coordinates Loaded";
        if len(ALLCOORD) < 1:
                print "No Coordinates to Extract. Quitting..."
                sys.exit(1)
	return size

usage = '''
Required:
BAM_File and BED_File

example usages:
python %prog -B BAM_File -C BED_File
'''.lstrip()

class CustomHelpFormatter(IndentedHelpFormatter):
	def format_description(self, description):
		return description

def run():
	parser = OptionParser(usage='%prog [options] -s BAM_File -b BED_File', description=usage, formatter=CustomHelpFormatter())
	parser.add_option('-B', "--bam", action='store', type='string', dest='bam', default='', help='Input BAM File.')
	parser.add_option('-C', "--bed", action='store', type='string', dest='bed', default='', help='Input BED File.')
	parser.add_option('-o', "--output", action='store', type='string', dest='output', default='output_file', help='Destination Output File. Default \'output_file\'')
	parser.add_option('-t', "--type", action='store', type='string', dest='type', default='TAB', help='Output file type (CDT or TAB). Default TAB.')
	parser.add_option('-S', "--shift", action='store', type='int', dest='shift', default=0, help='Tag Shift. Default 0.')
	parser.add_option('-s', "--strand", action='store', type='string', dest='strand', default='C', help='Strand to Examine (F-forward, R-reverse, C-combined). Default C.')

	(options, args) = parser.parse_args()
	options.type.lower()

	if options.bam == '':
		parser.print_help()
		parser.error('No BAM File Loaded!!!.')
	if options.bed == '':
        	parser.print_help()
	        parser.error('No BED File Loaded!!!.')
	if (options.shift < 0) or (options.shift != int(options.shift)):
		parser.print_help()
		parser.error('Tag Shift is not valid integer >= 0!!!')
	if (options.strand != 'C') and (options.strand != 'F') and (options.strand != 'R'):
		parser.print_help()
		parser.error('Strand to Examine is Invalid!!!.')
	if (options.type != "CDT") and (options.type != "TAB"):
		parser.print_help()
		parser.error('Invalid output type!!!')

	#Load in BED Coordinates
	ALLCOORD = []
	LENGTH = loadBED(options.bed, ALLCOORD)

	#Test that all BED Coordinates exist within parameters of mapped BAM File
	confirmCoord(options.bam, ALLCOORD)

	#Extract all tags, transform as applicable, and output
	parseBAM(options, ALLCOORD, LENGTH)

	print "Program Complete"

if __name__ == "__main__":
	run()
