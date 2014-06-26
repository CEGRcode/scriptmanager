package scripts;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import objects.Peak;

@SuppressWarnings("serial")
public class GeneTrack extends JFrame {
	private JTextArea textArea;
	
	private File INPUT = null;
	private PrintStream OUT = null;
	
	private int SIGMA = 5;
	private int EXCLUSION = 20;
	private int UP_WIDTH = 10;
	private int DOWN_WIDTH = 10;
	private int FILTER = 1;
	
	//Arbitrarily set to 5 std deviations up and down for gaussian kernel smoothing
	private int NUM_STD = 5;
	
	public GeneTrack(File in, int s, int e, int u, int d, int f) {
		setTitle("BAM to TAB Progress");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 600, 800);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		INPUT = in;
		SIGMA = s;
		EXCLUSION = e;
		UP_WIDTH = u;
		DOWN_WIDTH = d;
		FILTER = f;
	}
	
	public void run() {
		String READ = "s" + SIGMA + "e" + EXCLUSION;
		String NAME = INPUT.getName().split("\\.")[0] + "_" + READ + ".gff";
		textArea.append(NAME + "\n");
		textArea.append("Sigma: " + SIGMA + "\nExclusion: " + EXCLUSION + "\nFilter: " + FILTER + "\nUpstream width of called Peaks: " + UP_WIDTH + "\nDownstream width of called Peaks: " + DOWN_WIDTH + "\n");
		
		try { OUT = new PrintStream(new File(NAME)); }
		catch (FileNotFoundException e) { e.printStackTrace(); }

		//Set genetrack parameters
		double[] gaussWeight = gaussKernel();
		int WIDTH = SIGMA * NUM_STD;
	
//		#2014-06-04 10:09:57.237;lane-I1-pairend.tot.bam;READ1
//		chrom	index	forward	reverse	value
//		chr1	31	0	1	1
//		chr1	32	0	1	1

		String currentChrom = "";
		int currentBP = 1;
		double[] F_OCC = new double[10000];
		double[] R_OCC = new double[10000];
		
		Scanner scan = null;
		try { scan = new Scanner(INPUT); }
		catch (FileNotFoundException e) { e.printStackTrace(); }
		
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			if(!line.contains("index") && line.contains("#")) {
				OUT.print("#" + getTimeStamp());
				String[] comment = line.split("\\;");
				for(int x = 1; x < comment.length; x++) {
					OUT.print(";" + comment[x]);
				}
				OUT.println();
			} else if(!line.contains("index")) {
				String[] temp = line.split("\t");
				if(currentChrom.equals("")) {
					//set chrom to new chrom
					currentChrom = temp[0];
					currentBP = 1;
				} else if(!temp[0].equals(currentChrom)) {
					//TODO
					//check F_OCC and R_OCC arrays for peaks, populate ArrayList, then dump;
					ArrayList<Peak> F_PEAKS = new ArrayList<Peak>();
					ArrayList<Peak> R_PEAKS = new ArrayList<Peak>();
					
					//call peaks
					callPeaks(F_OCC, F_PEAKS);
					callPeaks(R_OCC, R_PEAKS);
					
					//dump existing peaks
					parsePeaksbyExclusion(F_PEAKS);
					parsePeaksbyExclusion(R_PEAKS);
										
					//set chrom to new chrom
					currentChrom = temp[0];
					currentBP = 1;
					textArea.append("Processing: " + currentChrom + "\n");
				} else if(Integer.parseInt(temp[1]) > currentBP) {
					//check F_OCC and R_OCC arrays for peaks, populate ArrayList, then dump;
					ArrayList<Peak> F_PEAKS = new ArrayList<Peak>();
					ArrayList<Peak> R_PEAKS = new ArrayList<Peak>();
					
					//call peaks
					callPeaks(F_OCC, F_PEAKS);
					callPeaks(R_OCC, R_PEAKS);
					
					//dump existing peaks
					parsePeaksbyExclusion(F_PEAKS);
					parsePeaksbyExclusion(R_PEAKS);
					
				}
				// populate current genomic fragment
				if(Integer.parseInt(temp[2]) != 0) {
					double[] F_read = calcScore(Integer.parseInt(temp[2]), gaussWeight);
					for(int z = Integer.parseInt(temp[1]) - WIDTH- currentBP; z < Integer.parseInt(temp[1]) + WIDTH - currentBP; z++) {
						if(z >= 0) {
							F_OCC[z] += F_read[z];
						}
					}
				}
				if(Integer.parseInt(temp[3]) != 0) {
					double[] R_read = calcScore(Integer.parseInt(temp[2]), gaussWeight);
					for(int z = Integer.parseInt(temp[1]) - WIDTH- currentBP; z < Integer.parseInt(temp[1]) + WIDTH - currentBP; z++) {
						if(z >= 0) {
							R_OCC[z] += R_read[z];
						}
					}
				}
			}
		}
		scan.close();
		OUT.close();
		dispose();
	}
	
	private void callPeaks(double[] occ, ArrayList<Peak> peaks) {
		
	}
	
	private double[] calcScore(int read, double[] gaussWeight) {
		double[] score = new double[gaussWeight.length];
		for(int x = 0; x < score.length; x++) {
			score[x] = gaussWeight[x] * read;
		}
		return score;
	}

	public void parsePeaksbyExclusion(ArrayList<Peak> peaks) {
		//Sort by Peak Score
		Collections.sort(peaks, Peak.PeakScoreComparator);
		
		//TODO adjust x and y by the changing size of the peak array
		for(int x = 0; x < peaks.size(); x++) {
			for(int y = 0; y < peaks.size(); y++) {
				if(x != y) {
					if(peaks.get(x).getStop() > peaks.get(y).getStart() && peaks.get(x).getStart() < peaks.get(y).getStop()) {
						peaks.remove(y);
					} else if(peaks.get(x).getStart() < peaks.get(y).getStop() && peaks.get(x).getStop() > peaks.get(y).getStart()) {
						peaks.remove(y);
					}
				}
			}
		}
		
		//Output peaks that pass exclusion filtering
		for(int x = 0; x < peaks.size(); x++) {
			OUT.println(peaks.toString());
		}
	}
	
	public double getStd(double[] tag) {
		if(tag.length > 0) {
			double std = 0;
			double avg = 0;
			for(int x = 0; x < tag.length; x++) { avg += tag[x]; }
			avg /= tag.length;
			for(int x = 0; x < tag.length; x++) { std += Math.pow(tag[x] - avg, 2); }
			return Math.sqrt(std / (tag.length - 1));
		} else return Double.NaN;
	}
	
	private double[] gaussKernel() {
		double[] Garray = new double[(int) (SIGMA * NUM_STD * 2) + 1];
		for(int x = 0; x < Garray.length; x++) {
             double HEIGHT = Math.exp(-1 * Math.pow((x - (Garray.length / 2)), 2) / (2 * Math.pow(SIGMA, 2)));
             HEIGHT /= (SIGMA * Math.sqrt(2 * Math.PI));
             Garray[x] = HEIGHT;
		}
		return Garray;
     }
	
	private static String getTimeStamp() {
		Date date= new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}
}
	
/*# genetrack.py

def process_file(path, options):
    
    global WIDTH
    WIDTH = options.sigma * 5
    
    logging.info('Processing file "%s" with s=%d, e=%d' % (path, options.sigma, options.exclusion))
    
    output_path = get_output_path(path, options)
    
    reader = csv.reader(open(path,'rU'), delimiter='\t')
    writer = csv.writer(open(output_path, 'wt'), delimiter='\t')

    if options.format == 'idx':
        writer.writerow(('chrom', 'strand', 'start', 'end', 'value'))
    
    manager = ChromosomeManager(reader)
    
        
    while not manager.done:
        cname = manager.chromosome_name()
        if not options.chromosome or options.chromosome == cname: # Should we process this chromosome?
            logging.info('Loading chromosome %s' % cname)
            data = manager.load_chromosome()
            if not data:
                continue
            keys = make_keys(data)
            lo, hi = get_range(data)
            for chunk in get_chunks(lo, hi, size=options.chunk_size * 10 ** 6, overlap=WIDTH):
                (slice_start, slice_end), process_bounds = chunk
                window = get_window(data, slice_start, slice_end, keys)
                process_chromosome(cname, window, writer, process_bounds, options)
            if options.chromosome: # A specific chromosome was specified. We're done, so terminate
                break
            #process_chromosome(cname, list(data), writer, options)
        else:
            logging.info('Skipping chromosome %s' % cname)
            manager.skip_chromosome()

class Peak(object):
    def __init__(self, index, pos_width, neg_width):
        self.index = index
        self.start = index - neg_width
        self.end = index + pos_width
        self.value = 0
        self.deleted = False
        self.safe = False
    def __repr__(self):
        return '[%d] %d' % (self.index, self.value)

def is_int(i):
    try:
        int(i)
        return True
    except ValueError:
        return False
        
        
class ChromosomeManager(object):
    ''' Manages a CSV reader of an index file to only load one chrom at a time '''
    def __init__(self, reader):
        self.done = False
        self.reader = reader
        self.processed_chromosomes = []
        self.current_index = 0
        self.next_valid()
        
    def next(self):
        self.line = self.reader.next()
        
    def is_valid(self, line):
        if len(line) not in [4, 5, 9]:
            return False
        try:
            [int(i) for i in line[1:]]
            self.format = 'idx'
            return True
        except ValueError:
            try:
                if len(line) < 6:
                    return False
                [int(line[4]), int(line[5])]
                self.format = 'gff'
                return True
            except ValueError:
                return False
        
    def next_valid(self):
        ''' Advance to the next valid line in the reader '''
        self.line = self.reader.next()
        s = 0
        while not self.is_valid(self.line):
            self.line = self.reader.next()
            s += 1
        if s > 0:
            logging.info('Skipped initial %d line(s) of file' % s)
            
    def parse_line(self, line):
        if self.format == 'idx':
            return [int(line[1]), int(line[2]), int(line[3])]
        else:
            return [int(line[3]), line[6], line[5]]
            
    def chromosome_name(self):
        ''' Return the name of the chromosome about to be loaded '''
        return self.line[0]
        
    def load_chromosome(self, collect_data=True):
        ''' Load the current chromosome into an array and return it '''
        cname = self.chromosome_name()
        if cname in self.processed_chromosomes:
            logging.error('File is not grouped by chromosome')
            raise InvalidFileError
        self.data = []
        while self.line[0] == cname:
            if collect_data:
                read = self.parse_line(self.line)
                if read[0] < self.current_index:
                    logging.error('Reads in chromosome %s are not sorted by index. (At index %d)' % (cname, self.current_index))
                    raise InvalidFileError
                self.current_index = read[0]
                self.add_read(read)
            try:
                self.next()
            except StopIteration:
                self.done = True
                break
        self.processed_chromosomes.append(cname)
        self.current_index = 0
        data = self.data
        del self.data # Don't retain reference anymore to save memory
        return data
    
    def add_read(self, read):
        if self.format == 'idx':
            self.data.append(read)
        else:
            index, strand, value = read
            if value == '' or value == '.':
                value = 1
            else:
                value = int(value)
            if not self.data:
                self.data.append([index, 0, 0])
                current_read = self.data[-1]
            if self.data[-1][0] == index:
                current_read = self.data[-1]
            elif self.data[-1][0] < index:
                self.data.append([index, 0, 0])
                current_read = self.data[-1]
            else:
                logging.error('Reads in chromosome %s are not sorted by index. (At index %d)' % (self.chromosome_name(), index))
                raise InvalidFileError
            if strand == '+':
                current_read[1] += value
            elif strand == '-':
                current_read[2] += value
            else:
                logging.error('Strand "%s" at chromosome "%s" index %d is not valid.' % (strand, self.chromosome_name(), index))
                raise InvalidFileError
        
    
    def skip_chromosome(self):
        ''' Skip the current chromosome, discarding data '''
        self.load_chromosome(collect_data=False)
    
            


def make_keys(data):
    return [read[0] for read in data]
    
def make_peak_keys(peaks):
    return [peak.index for peak in peaks]

def get_window(data, start, end, keys):
    ''' Returns all reads from the data set with index between the two indexes'''
    start_index = bisect.bisect_left(keys, start)
    end_index = bisect.bisect_right(keys, end)
    return data[start_index:end_index]
    
def get_index(value, keys):
    ''' Returns the index of the value in the keys using bisect '''
    return bisect.bisect_left(keys, value)

def get_range(data):
    lo = min([item[0] for item in data])
    hi = max([item[0] for item in data])
    return lo, hi

def get_chunks(lo, hi, size, overlap=500):
    ''' Divides a range into chunks of maximum size size. Returns a list of 2-tuples
    (slice_range, process_range), each a 2-tuple (start, end). process_range has zero overlap
    and should be given to process_chromosome as-is, and slice_range is overlapped and should be used to
    slice the data (using get_window) to be given to process_chromosome. '''
    chunks = []
    for start_index in range(lo, hi, size):
        process_start = start_index
        process_end = min(start_index + size, hi) # Don't go over upper bound
        slice_start = max(process_start - overlap, lo) # Don't go under lower bound
        slice_end = min(process_end + overlap, hi) # Don't go over upper bound
        chunks.append(((slice_start, slice_end), (process_start, process_end)))
    return chunks
    
    

def allocate_array(data, width):
    ''' Allocates a new array with the dimensions required to fit all reads in the
    argument. The new array is totally empty. Returns the array and the shift (number to add to
    a read index to get the position in the array it should be at).'''
    lo, hi = get_range(data)
    rng = hi - lo
    shift = width - lo
    return numpy.zeros(rng+width*2, numpy.float), shift
    
def normal_array(width, sigma, normalize=True):
    ''' Returns an array of the normal distribution of the specified width '''
    sigma2 = float(sigma)**2
    
    def normal_func(x):
        return math.exp( -x * x / ( 2 * sigma2 ))
        
    # width is the half of the distribution
    values = map( normal_func, range(-width, width) )
    values = numpy.array( values, numpy.float )

    # normalization
    if normalize:
        values = 1.0/math.sqrt(2 * numpy.pi * sigma2) * values 

    return values

def call_peaks(array, shift, data, keys, direction, options):
    peaks = []
    def find_peaks():
        # Go through the array and call each peak
        results = (array > numpy.roll(array, 1)) & (array > numpy.roll(array, -1))
        indexes = numpy.where(results)
        for index in indexes[0]:
            pos = options.down_width or options.exclusion // 2
            neg = options.up_width or options.exclusion // 2
            if direction == 2: # Reverse strand
                pos, neg = neg, pos # Swap positive and negative widths
            peaks.append(Peak(int(index)-shift, pos, neg))
    find_peaks()
        
    def calculate_reads():
        # Calculate the number of reads in each peak
        for peak in peaks:
            reads = get_window(data, peak.start, peak.end, keys)
            peak.value = sum([read[direction] for read in reads])
            indexes = [r for read in reads for r in [read[0]] * read[direction]] # Flat list of indexes with frequency
            peak.stddev = numpy.std(indexes)
    calculate_reads()
        
    before = len(peaks)
        
    def perform_exclusion():
        # Process the exclusion zone
        peak_keys = make_peak_keys(peaks)
        peaks_by_value = peaks[:]
        peaks_by_value.sort(key=lambda peak: -peak.value)
        for peak in peaks_by_value:
            peak.safe = True
            window = get_window(peaks, peak.index-options.exclusion//2, peak.index+options.exclusion//2, peak_keys)
            for excluded in window:
                if excluded.safe:
                    continue
                i = get_index(excluded.index, peak_keys)
                del peak_keys[i]
                del peaks[i]
    perform_exclusion()
            
    after = len(peaks)
    if before != 0:
        logging.debug('%d of %d peaks (%d%%) survived exclusion' % (after, before, after*100/before))
            
    return peaks
    
def process_chromosome(cname, data, writer, process_bounds, options):
    ''' Process a chromosome. Takes the chromosome name, list of reads, a CSV writer
    to write processes results to, the bounds (2-tuple) to write results in, and options. '''
    if data:
        logging.info('Processing chromosome %s indexes %d-%d' % (cname, process_bounds[0], process_bounds[1]))
    else:
        logging.info('Skipping chromosome %s indexes %d-%d because no reads within these bounds' % (cname, process_bounds[0], process_bounds[1]))
        return
    keys = make_keys(data)
    # Create the arrays that hold the sum of the normals
    forward_array, forward_shift = allocate_array(data, WIDTH)
    reverse_array, reverse_shift = allocate_array(data, WIDTH)
    normal = normal_array(WIDTH, options.sigma)
    
    
    def populate_array():
        # Add each read's normal to the array
        for read in data:
            index, forward, reverse = read
            # Add the normals to the appropriate regions
            if forward:
                forward_array[index+forward_shift-WIDTH:index+forward_shift+WIDTH] += normal * forward
            if reverse:
                reverse_array[index+reverse_shift-WIDTH:index+reverse_shift+WIDTH] += normal * reverse
    populate_array()
        
    logging.debug('Calling forward strand')
    forward_peaks = call_peaks(forward_array, forward_shift, data, keys, 1, options)
    logging.debug('Calling reverse strand')
    reverse_peaks = call_peaks(reverse_array, reverse_shift, data, keys, 2, options)

    # Convert chromosome name in preparation for writing our
    cname = convert_data(cname, 'zeropad', 'numeric')
    
    
    def write(cname, strand, peak):
        start = max(peak.start, 1)
        end = peak.end
        value = peak.value
        stddev = peak.stddev
        if value > options.filter:
            if options.format == 'gff':
                writer.writerow(gff_row(cname=cname, source='genetrack', start=start, end=end,
                                        score=value, strand=strand, attrs={'stddev':stddev}))
            else:
                writer.writerow((cname, strand, start, end, value))
    
    for peak in forward_peaks:
        if process_bounds[0] < peak.index < process_bounds[1]:
            write(cname, '+', peak)
    for peak in reverse_peaks:
        if process_bounds[0] < peak.index < process_bounds[1]:
            write(cname, '-', peak)
    
    
    
def get_output_path(input_path, options):
    directory, fname = os.path.split(input_path)
    
    if fname.startswith('INPUT'):
        fname = fname[5:].strip('_') # Strip "INPUT_" from the file if present
    fname = ''.join(fname.split('.')[:-1]) # Strip extension (will be re-added as appropriate)

    attrs = 's%de%d' % (options.sigma, options.exclusion) # Attribute list to add to file/dir name
    if options.up_width:
        attrs += 'u%d' % options.up_width
    if options.down_width:
        attrs += 'd%d' % options.down_width
    if options.filter:
        attrs += 'F%d' % options.filter
    
    output_dir = os.path.join(directory, 'genetrack_%s' % attrs)
    if not os.path.exists(output_dir):
        os.mkdir(output_dir)
    if options.chromosome:
        fname = options.chromosome + '_' + fname

    return os.path.join(output_dir, '%s_%s.%s' % (fname, attrs, options.format))
    
*/