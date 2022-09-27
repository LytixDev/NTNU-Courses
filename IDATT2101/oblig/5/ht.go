/* 
 * Written by Nicolai H. Brand
 * Note: Go version >= 1.18 is required
 */

package main

import (
    //"fmt"
    "errors"
)


/* types */

type (

HashTable struct {
    capacity uint
    items []HashTableItem
}

HashTableItem struct {
    key string
    value int
    next *HashTableItem
}

)


/* functions */

func hasher(key string) uint32 {
    var hash uint32
    for _, char := range key {
        hash += (hash << 5) + uint32(char)
    }

    return hash
}


func (ht *HashTable) New(capacity uint) {
    ht.capacity = capacity
    ht.items = make([]HashTableItem, capacity)
}


func (ht *HashTable) Set(key string, value int) {
    hash := hasher(key) % uint32(ht.capacity)

    item := &ht.items[hash]
    /* no item means hash index empty -> insert immediately */
    if item == nil {
        *item = HashTableItem{key: key, value: value}
        return;
    }

    var prev *HashTableItem
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

        prev = item;
        item = prev.next;
    }

    /* end of chain reached without a match -> add new */
    prev.next = &HashTableItem{key: key, value: value}
}

func (ht *HashTable) Get(key string) (int, error) {
    hash := hasher(key) % uint32(ht.capacity)
    found := &ht.items[hash]

    if (found == nil) {
        return -1, errors.New("item not found")
    }

    /*
     * on collision, return matching key
     */
    for ; found.next != nil; found = found.next {
        if found.key == key {
            break
        }
    }

    /* 
     * entering here means 'found' should be the corresponding 
     * item for the given key assuming correct implementation.
     */
    return found.value, nil
}


/*
func main() {
    ht := New(32)
    Set(&ht, "Nicolai", 40)
    Set(&ht, "Nicolai", 10)
    Set(&ht, "Vinfur", 10)

    value, err := Get(&ht, "Nicolai")
    if err == nil {
        fmt.Println(value)
    } else {
        fmt.Println("Value not found")
    }
}
*/
