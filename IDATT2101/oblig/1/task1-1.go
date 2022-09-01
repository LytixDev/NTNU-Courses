package main

import "fmt"

func main() {
    prices := []int{-1, 3, -9, 2, 2, -1, 2, -1}
    var buy, sell, stonk, best int

    for i := 0; i <= len(prices); i++ {
        stonk = 0
        for j := i; j < len(prices); j++ {
            stonk += prices[j]
            if stonk > best {
                best = stonk
                buy = i
                sell = j
            }
        }
    }

    fmt.Printf("kjøpsdag: %d, salgsdag: %d, profit: %d\n", buy, sell + 1, best)
}
