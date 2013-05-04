// $G $F.go && $L $F.$A  # don't run it - goes forever

// Copyright 2009 The Go Authors. All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
// 8g test/sieve.go; 8l sieve.8

package main
import( "os" )

// Send the sequence 2, 3, 4, ... to channel 'ch'.
func Generate(ch chan<- int) {
	for i := 2; i <= 150000; i++ {
		ch <- i  // Send 'i' to channel 'ch'.
	}
}

// Copy the values from channel 'in' to channel 'out',
// removing those divisible by 'prime'.
func Filter(in <-chan int, out chan<- int, prime int) {
	for {
		i := <-in;  // Receive value of new variable 'i' from 'in'.
		if i % prime != 0 {
			out <- i  // Send 'i' to channel 'out'.
		}
	}
}

// The prime sieve: Daisy-chain Filter processes together.
func Sieve() {
	ch := make(chan int, 50);  // Create a new channel.
	go Generate(ch);  // Start Generate() as a subprocess.
	for {
		prime := <-ch;
		print(prime, "\n");
                if prime == 149993 {os.Exit(1)}
		ch1 := make(chan int,50);
		go Filter(ch, ch1, prime);
		ch = ch1
	}
}

func main() {
	Sieve()
}
