set title "thread-ring - 24*amd64, 1.7.0_04-ea-b06"
set xlabel "Threads"
set ylabel "Throughput[msg/s]"
set xtics 1
#set key out

#set term png
#set output "thread-ring.png"
#set term postscript eps enhanced
#set output "thread-ring.eps"

plot '../molecule/thread-ring/molecule-word_wcfj.dat' using 1:4:($4*$3)/100 with errorlines title "Molecule-word",\
'../go/threadring%throughput.dat' using 1:3 with linespoint title "Go"
pause -1
