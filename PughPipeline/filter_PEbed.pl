#! /usr/bin/perl

die "Input_Bed\n" unless $#ARGV == 0;
my($input) = @ARGV;
open(IN, "<$input") or die "Can't open $input for reading!\n";
open(OUT1, ">Read1.bed") or die "Can't open Read1.bed for writing!\n";
open(OUT2, ">Read2.bed") or die "Can't open Read2.bed for writing!\n";

while($line = <IN>) {
	chomp($line);
	@array = split(/\t/, $line);
	#chr1	0	35	NS500168:2:H0LDCAGXX:2:13110:3570:3628/2	36	+
	#chr1	0	38	NS500168:2:H0LDCAGXX:3:22410:15179:5396/1	3	-
	if($array[3] =~ /\/2/) {
		print OUT2 $line,"\n";
	} else {
		print OUT1 $line,"\n";
	}
}
close IN;
close OUT1;
close OUT2;
