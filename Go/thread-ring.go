// 8g test/threadring.go; 8l threadring.8

package main

import (
    "fmt"
    "os"
    "strconv"
)

type Token int

func worker(id int, in <-chan Token, out chan<- Token, res chan<- int) {
    for {
        t := <- in
        if t==0 {
            res <- id
        } else {
            out <- t-1
        }
    }
}

const NThreads = 503

func main() {
    n := 2000000
    if len(os.Args)>1 { n,_ = strconv.Atoi(os.Args[1]) }

    var channels [NThreads] chan Token
    for i:=0; i<NThreads; i++ { channels[i] = make(chan Token) }
    res := make(chan int)

    for i:=0; i<NThreads; i++ {
        go worker(i+1, channels[i], channels[(i+1)%NThreads], res)
    }

    channels[0] <- Token(n)
    r := <- res
    fmt.Printf("%d\n",r)
    os.Exit(0)
}

