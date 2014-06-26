# chrtrans.py
#
# Chromosome number translation script
# Converts between formats like chr4 <-> chr04 <-> chrIV
#
# By Pindi Albert, 2011
#
# Input: any file containing chromosome numbers in the form chrXX
# Note: each chromosome number must be bounded by whitespace (space, tab, newline)
#
# Output: file with numbers translated from one format to another
# All other file contents are left untouched
#
# Run with no arguments or -h for usage, command line options, and supported formats

from optparse import OptionParser, IndentedHelpFormatter
import re, sys, os, logging

logging.basicConfig(format='%(levelname)s:%(message)s', level=logging.INFO)

ROMAN = ['0', 'I', 'II', 'III', 'IV', 'V', 'VI', 'VII', 'VIII', 'IX', 'X',
         'XI', 'XII', 'XIII', 'XIV', 'XV', 'XVI', 'XVII', 'XVIII', 'XIX', 'XX',
         'XXI', 'XXII', 'XXIII', 'XXIV', 'XXV', 'XXVI', 'XXVII', 'XXVIII', 'XXIX', 'XXX']


def noop(data):
    return data

def zeropad_to_numeric(data):
    return re.sub(r'chr0(\d)', r'chr\1', data)
    
def numeric_to_zeropad(data):
    return re.sub(r'chr(\d([^\d]|$))', r'chr0\1', data)
    
def roman_to_numeric(data):
    def convert(match):
        ''' Converts a single roman numeral to a number '''
        numeral = match.group(1)
        numeral = numeral.upper()
        if numeral not in ROMAN:
            logging.error('Unable to convert detected Roman numeral "%s"' % numeral)
            return match.group(0) 
        return 'chr'+str(ROMAN.index(numeral))+(match.group(2) or '')
    r = re.compile('chr([IVX]+)([^IVX]|$)', flags=re.IGNORECASE)
    data = r.sub(convert, data)
    return data

def numeric_to_roman(data):
    def convert(match):
        ''' Converts a number to a roman numeral '''
        number = int(match.group(1))
        if number >= len(ROMAN):
            logging.error('Number "%d" is out of range to convert to a Roman numeral' % number)
            return match.group(0)
        return 'chr'+ROMAN[number]+(match.group(2) or '')
    r = re.compile('chr(\d+)([^\d]|$)')
    data = r.sub(convert,  data)
    return data

FORMATS = ['zeropad', 'numeric', 'roman']
IN_CONVERT = {'zeropad':zeropad_to_numeric, 'roman':roman_to_numeric, 'numeric':noop}
OUT_CONVERT = {'zeropad':numeric_to_zeropad, 'roman':numeric_to_roman, 'numeric':noop}

def conversion_functions(in_fmt, out_fmt):
    ''' Returns the proper list of functions to apply to perform a conversion '''
    return [IN_CONVERT[in_fmt], OUT_CONVERT[out_fmt]]
    
def autodetect_format(data):
    if re.search('chr0\d', data):
        fmt = 'zeropad'
    elif re.search('chr[IVXivx]', data):
        fmt = 'roman'
    else:
        fmt = 'numeric'
    logging.info('Autodetected format %s' % fmt)
    return fmt   
   
def convert_data(data, in_fmt, out_fmt):
    if in_fmt == 'autodetect':
        in_fmt = autodetect_format(data)
    for fn in conversion_functions(in_fmt, out_fmt):
        data = fn(data)
    return data
    
def process_file(path, in_fmt, out_fmt):
    logging.info('Reading "%s"' % path)
    f = open(path, 'rt')
    data = f.read()
    f.close()
    
    if in_fmt == 'autodetect':
        in_fmt = autodetect_format(data)
    dir, fname = os.path.split(path)
    target_dir = os.path.join(dir, '%s_to_%s' % (in_fmt, out_fmt))
    if not os.path.exists(target_dir):
        os.mkdir(target_dir)
    out_path = os.path.join(target_dir, fname)


    data = convert_data(data, in_fmt, out_fmt)
    
    
    f = open(out_path, 'wt')
    f.write(data)
    f.close()
    logging.info('Wrote "%s"' % out_path)
    
def process_pipe(in_fmt, out_fmt):
    data = sys.stdin.read()
    data = convert_data(data, in_fmt, out_fmt)
    sys.stdout.write(data)
    
    
usage = '''
input_paths may be:
- a file or list of files to run on
- a directory or list of directories to run on all files in them
- "." to run in the current directory
- "-" to run from standard input and pipe output to standard output

formats are:
- numeric: "chr" followed by a number.                  ex chr4
- zeropad: "chr" followed by number padded to 2 places. ex chr04
- roman: "chr" followed by a Roman numeral.             ex chrIV
'''.lstrip()


 
# We must override the help formatter to force it to obey our newlines in our custom description
class CustomHelpFormatter(IndentedHelpFormatter):
    def format_description(self, description):
        return description
 

def run():   
    parser = OptionParser(usage='%prog [options] input_paths', description=usage, formatter=CustomHelpFormatter())
    parser.add_option('-i', action='store', type='string', dest='in_format', default='autodetect',
                      help='Format input data is in. Default autodetect.')
    parser.add_option('-o', action='store', type='string', dest='out_format', default='numeric',
                      help='Format to output data in. Default numeric.')
    parser.add_option('-q', action='store_true', dest='quiet', help='Quiet mode: suppresses all non-error messages')
    (options, args) = parser.parse_args()
        
    if options.quiet:
        logging.getLogger().setLevel(logging.ERROR) # Silence all non-error messages
        
    if options.in_format not in FORMATS+['autodetect']  or options.out_format not in FORMATS:
        parser.error('%s is not a valid format. Use -h option for a list of valid formats.' % options.method)
        
    if not args:
        parser.print_help()
        sys.exit(1)
        
    if args == ['-']:
        logging.disable(logging.CRITICAL) # Disable -all- log messages in pipe mode
        process_pipe(options.in_format, options.out_format)
        sys.exit(0)
        
    for path in args:
        if not os.path.exists(path):
            parser.error('Path %s does not exist.' % path)
        if os.path.isdir(path):
            for fname in os.listdir(path):
                fpath = os.path.join(path, fname)
                if os.path.isfile(fpath):
                    process_file(fpath, options.in_format, options.out_format)
        else:
            process_file(path, options.in_format, options.out_format)
    

if __name__ == '__main__':
    run()
    