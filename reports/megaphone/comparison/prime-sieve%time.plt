set title "prime-sieve - 24*amd64, 1.7.0_04-ea-b06"
set xlabel "Threads"
set ylabel "Time[s]"
set xtics 1
set log y
#set key out

#set term png
#set output "prime-sieve.png"
#set term postscript eps enhanced
#set output "prime-sieve.eps"

plot '../molecule/prime-sieve/molecule-io_wcfj_SST=1-CCT=1.dat' using 1:2:($2*$3)/100 with errorlines title "Molecule(1)",\
'../molecule/prime-sieve/molecule-io_wcfj_SST=10-CCT=10.dat' using 1:2:($2*$3)/100 with errorlines title "Molecule(10)",\
'../molecule/prime-sieve/molecule-io_wcfj_SST=50-CCT=50.dat' using 1:2:($2*$3)/100 with errorlines title "Molecule(50)",\
'../go/sieve-go-1.dat' using 1:2 with linespoint title "Go (1)",\
'../go/sieve-go-50.dat' using 1:2 with linespoint title "Go (50)",\
'../go/sieve-go-100.dat' using 1:2 with linespoint title "Go (100)"
pause -1
