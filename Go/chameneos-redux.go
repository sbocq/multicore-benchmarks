/*
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.

    * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.

    * Neither the name of "The Computer Language Benchmarks Game" nor the
    name of "The Computer Language Shootout Benchmarks" nor the names of
    its contributors may be used to endorse or promote products derived
    from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
*/

/* The Computer Language Benchmarks Game
 * http://shootout.alioth.debian.org/
 *
 * contributed by The Go Authors.
 */

package main

import (
	"flag"
	"fmt"
	"strconv"
)

const (
	blue = iota
	red
	yellow
	ncol
)

var complement = [...]int{
	red | red<<2: red,
	red | yellow<<2: blue,
	red | blue<<2: yellow,
	yellow | red<<2: blue,
	yellow | yellow<<2: yellow,
	yellow | blue<<2: red,
	blue | red<<2: yellow,
	blue | yellow<<2: red,
	blue | blue<<2: blue,
}

var colname = [...]string{
	blue: "blue",
	red: "red",
	yellow: "yellow",
}

var n = 300000      // nb meetings

type ChameneoId struct {
  name   int
  colour int
}

type ChameneoMessage interface {
}

type MeetRequest struct {
  id    ChameneoId
  mate  chan<- ChameneoMessage
}

type Copy struct {
  id   ChameneoId
}

type Stop struct {
}

type MallRequest struct {
  id    ChameneoId
  reply chan ChameneoMessage
}

func main() {
    var nbChameneos = 300  // default
	flag.Parse()
	if flag.NArg() > 0 {
		nbChameneos, _ = strconv.Atoi(flag.Arg(0))
	}

	fmt.Print("\n")
    fmt.Printf("nbMeetings = %d\n", n)
    fmt.Printf("nbChameneos = %d\n", nbChameneos)
        
    var colors = []int{blue, red, yellow}
    var chameneos []int = make([]int, nbChameneos)
    for i := 0; i < nbChameneos; i++ {
       chameneos[i] = colors[i % ncol]
    }
    pallmall(chameneos)
}

func pallmall(cols []int) {

	meetingplace := make(chan MallRequest, 4)
	ended := make(chan int)
	msg := ""
	go mall(n, meetingplace)
	for i, col := range cols {
		go chameneo(ChameneoId{i, col}, meetingplace, ended)
		msg += " " + colname[col]
	}
	fmt.Println(msg)
	tot := 0
	// wait for all results
	for _ = range cols {
		result := <-ended
		tot += result
		fmt.Printf("%v\n", result)
	}
}

func mall(nbMeetings int, meetingplace chan MallRequest) {
  for n := 0; n < nbMeetings; n++ {
    req1 := <-meetingplace
    req2 := <- meetingplace
	req1.reply <- MeetRequest{id: req2.id, mate:req2.reply}
  }
  for {
    req := <- meetingplace
	req.reply <- Stop{}
  }
}

func chameneo(id ChameneoId, meetingplace chan MallRequest, ended chan int) {
	met := 0
    req := MallRequest{id:id, reply: make(chan ChameneoMessage)}
	for {
		meetingplace <- req
		rsp := <- req.reply;
		switch rsp.(type) {
		case MeetRequest:
		    req := rsp.(MeetRequest)
		    id.colour = complement[id.colour|req.id.colour<<2]
		    req.mate <- Copy{id:id}
		case Copy:
		    req := rsp.(Copy)
		    id.colour = req.id.colour
		case Stop:
		    ended <- met
		    return
		}
		met++
	}
}

