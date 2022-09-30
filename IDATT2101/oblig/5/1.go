/* 
 * Written by Nicolai H. Brand
 * Note: Go version >= 1.18 is required
 */

package main

import (
    "fmt"
    "errors"
    "os"
    "bufio"
)

/* globals */

var TOTAL_ITEMS uint = 114   /* wc -l navn.txt */
var total_collisions uint


/* types */

type (
HashTable struct {
    capacity uint
    items []HashTableItem[any]
}

HashTableItem[T any] struct {
    key string
    value T
    next *HashTableItem[any]
})


/* functions */

func hasher(key string) uint32 {
    var hash uint32
    for _, char := range key {
        hash += (hash << 5) + uint32(char)      /* generic hash function used by zsh */
    }

    return hash
}


func (ht *HashTable) New(capacity uint) {
    ht.capacity = capacity
    ht.items = make([]HashTableItem[any], capacity)
}


func (ht *HashTable) Set(key string, value any) {
    hash := hasher(key) % uint32(ht.capacity)

    item := &ht.items[hash]
    /* item == empty struct means hash index empty -> insert immediately */
    if *item == (HashTableItem[any]{}) {
        *item = HashTableItem[any]{key: key, value: value}
        return;
    }

    /* entering here means there is a collision */
    total_collisions++
    var prev *HashTableItem[any]
    /*
     * walk through each item until either the end is
     * reached or a matching key is found
     */
    for item != nil {
        if item.key == key {
            /* match found -> override */
            item.value = value
            return;
        }

        fmt.Printf("Collision between %s and %s\n", key, item.key)
        prev = item;
        item = prev.next;
    }

    /* end of chain reached without a match -> add new */
    prev.next = &HashTableItem[any]{key: key, value: value}
}

func (ht *HashTable) Get(key string) (any, error) {
    hash := hasher(key) % uint32(ht.capacity)
    found := &ht.items[hash]

    if (found == nil) {
        return nil, errors.New("1: item not found")
    }

    /*
     * on collision, return matching key
     */
    for ; found != nil; found = found.next {
        if found.key == key {
            return found.value, nil
        }
    }

    /* 
     * entering here means some number of items were stored 
     * for this hash, but not using this key.
     */
    return nil, errors.New("2: item not found")
}


func read_file(ht *HashTable) {
    file, err := os.Open("navn.txt")
    if err != nil {
        fmt.Println("Could not open file 'navn.txt'")
        os.Exit(1)
    }

    defer file.Close()

    scanner := bufio.NewScanner(file)
    for scanner.Scan() {
        /* key and value are equal for this example */
        ht.Set(scanner.Text(), scanner.Text())
    }
}


func main() {
    var ht HashTable
    ht.New(128)
    read_file(&ht)

    found, err := ht.Get("Nicolai Hollup Brand")
    if err == nil {
        fmt.Println("\nFound", found, "using ht.Get(\"Nicolai Hollup Brand\")")
    } else {
        fmt.Println(err)
    }

    fmt.Println("\n--- metadata ---")
    fmt.Println("Capacity in table:", ht.capacity)
    fmt.Println("Items in table:", TOTAL_ITEMS)
    fmt.Println("Total collisions:", total_collisions)
    fmt.Println("Total collisions per item:", float32(total_collisions) / float32(TOTAL_ITEMS))
    fmt.Println("Load factor:", float32(TOTAL_ITEMS) / float32(ht.capacity))

    /* result

    Collision between Erlend Ravn Ryan and Magnus Fikse Forbord
    .
    .
    .
    Collision between Torstein Øvstedal and Ingrid Flatland

    Found Nicolai Hollup Brand using ht.Get("Nicolai Hollup Brand")

    --- metadata ---
    Capacity in table: 128
    Items in table: 114
    Total collisions: 38
    Total collisions per item: 0.33333334
    Load factor: 0.890625
    */
}

