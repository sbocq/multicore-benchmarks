set title "prime-sieve - 24*amd64, 1.7.0_04-ea-b06"
set xlabel "Threads"
set ylabel "Time[s]"
set xtics 1
set log y
#set key out

set term png
set output "prime-sieve.png"
#set term postscript eps enhanced
#set output "prime-sieve.eps"

plot '../go/go-results-1.0.3/prime-sieve-1.dat' using 1:2 with linespoint title "Go 1.0.3 (buffer=1)",\
'../go/go-results-1.0.3/prime-sieve-50.dat' using 1:2 with linespoint title "Go 1.0.3 (buffer=50)",\
'../go/go-results-1.1/prime-sieve-1.dat' using 1:2 with linespoint title "Go 1.1 (buffer=1)",\
'../go/go-results-1.1/prime-sieve-50.dat' using 1:2 with linespoint title "Go 1.1 (buffer=50)",\
'../molecule/prime-sieve/molecule-io_wcfj_SST=1-CCT=1.dat' using 1:2:($2*$3)/100 with errorlines title "Molecule (buffer=fusions=1)",\
'../molecule/prime-sieve/molecule-io_wcfj_SST=50-CCT=50.dat' using 1:2:($2*$3)/100 with errorlines title "Molecule (buffer=fusions=50)"

pause -1
