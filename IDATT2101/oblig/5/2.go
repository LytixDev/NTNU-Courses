/*
 * Written by Nicolai H. Brand
 * Note: Go version >= 1.18 is required
 */

package main

import (
	"fmt"
	"errors"
	"math/rand"
        "time"
)

/* globals */

var TOTAL_ITEMS uint = 10_000_000
var HT_CAPACITY uint = 13_003_231       // a sufficiently large prime number
var INT32MAX int = 1 << 32

var total_collisions uint


/* types */

type HashTable struct {
    capacity uint
    items []int
}


/* functions */

func hash_1(k int) uint32 {
    return uint32(k % int(HT_CAPACITY))
}

func hash_2(k int) uint32 {
    return uint32(k % (int(HT_CAPACITY) - 1) + 1)
}

func (ht *HashTable) New(capacity uint) {
    ht.capacity = capacity
    ht.items = make([]int, capacity)
}

func (ht *HashTable) Set(key int) (error) {
    pos := hash_1(key) % uint32(ht.capacity)
    if ht.items[pos] == 0 {
        ht.items[pos] = key
        return nil
    }

    h2 := hash_2(key) % uint32(ht.capacity)
    for {
        total_collisions++
        pos = (pos + h2) % uint32(ht.capacity)

        if pos >= uint32(ht.capacity) {
            return errors.New("hashtable full!")
        }

        if ht.items[pos] == 0 {
            ht.items[pos] = key
            return nil
        }
    }
}

func (ht *HashTable) Get(key int) (int, error) {
    h1 := hash_1(key) % uint32(ht.capacity)

    if ht.items[h1] == key {
        return ht.items[h1], nil
    } else {
        var c uint32
        for c = 1; uint(c) < ht.capacity; {
            h2 := (hash_2(key) * c + h1) % uint32(ht.capacity)
            if ht.items[h2] == key {
                return ht.items[h2], nil
            }

            c++
        }
    }

    return 0, errors.New("key not found");
}

func custom_test() {
    var ht HashTable
    ht.New(HT_CAPACITY)

    var elapsed time.Duration
    for i := 0; i < int(TOTAL_ITEMS); i++ {
        start := time.Now()
        err := ht.Set(rand.Intn(int(INT32MAX)))
        elapsed += time.Since(start)

        if err != nil {
            fmt.Println("Could not insert", i)
        }
    }

    fmt.Println("--- custome hashtable for integers ---")
    fmt.Printf("Inserting %d ints took %s\n", TOTAL_ITEMS, elapsed)
    fmt.Println("Collisions:", total_collisions, "Load factor:", float32(TOTAL_ITEMS) / float32(HT_CAPACITY))
}

func default_test() {
    ht := make(map[int]int)

    var elapsed time.Duration
    for i := 0; i < int(TOTAL_ITEMS); i++ {
        start := time.Now()
        ht[rand.Intn(int(INT32MAX))] = i
        elapsed += time.Since(start)
    }

    fmt.Println("--- builtin hashmap for integers ---")
    fmt.Printf("Inserting %d ints took %s\n", TOTAL_ITEMS, elapsed)
}

func correctness_check() {
    /* super rudementary correctess check */
    var ht HashTable
    ht.New(HT_CAPACITY)

    key := 42
    ht.Set(key)

    key_got, err := ht.Get(key)
    if err != nil {
        panic("Hashtable implementation not correct!")
    } else {
        if key_got != key {
            panic("Hashtable implementation not correct!")
        } else {
            //fmt.Println("Hashtable implementation may be correct!")
        }
    }
}

func main() {
    correctness_check()
    custom_test()
    fmt.Println()
    default_test()

    /* result
    --- custome hashtable for integers ---
    inserting 10000000 ints took 1.778951996s
    collisions: 9067140 load factor: 0.76903963

    --- builtin hashmap for integers ---
    inserting 10000000 ints took 2.13720174s
    */
}

