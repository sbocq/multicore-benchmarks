set title "chameneos-redux - 24*amd64, 1.7.0_04-ea-b06"
set xlabel "Threads"
set ylabel "Throughput[meetings/s]"
set xtics 1
#set key out

#set term png
#set output "chameneos-redux.png"
#set term postscript eps enhanced
#set output "chameneos-redux.eps"

plot '../molecule/chameneos-redux/molecule-core_wcfj_SST=0.dat' using 1:4:($4*$3)/100 with errorlines title "Molecule-core",\
'../molecule/chameneos-redux/molecule-io_wcfj_SST=0.dat' using 1:4:($4*$3)/100 with errorlines title "Molecule-word",\
'../go/go-results-1.1/chameneos%throughput.dat' using 1:3 with linespoint title "Go 1.1",\
'../go/go-results-1.0.3/chameneos%throughput.dat' using 1:3 with linespoint title "Go 1.0.3"
pause -1
