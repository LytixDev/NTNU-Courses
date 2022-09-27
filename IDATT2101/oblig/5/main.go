package main

import (
    "fmt"
)

func main() {
    var ht HashTable
    ht.New(32)
    ht.Set("Nicolai", 40)
    ht.Set("Nicolai", 10)
    ht.Set("Lol", 12)

    value, err := ht.Get("Nicolai")
    if err == nil {
        fmt.Println(value)
    } else {
        fmt.Println("Value not found")
    }
}
