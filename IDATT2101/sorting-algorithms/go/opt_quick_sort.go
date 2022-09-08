package main

import (
    "fmt"
    "time"
    "math/rand"
    "sync"
    "runtime"
)


func insert_sort(data[] int) {
    for i:= 1; i < len(data); {
        h := data[i]
        j := i - 1
        for j >= 0 && h < data[j] {
            data[j + 1] = data[j]
            j -= 1
        }
        data[j + 1] = h
        i += 1
    }
}

func partition(data[] int) ([] int, [] int) {
    data[len(data) / 2], data[0] = data[0], data[len(data) / 2]
    pivot := data[0]
    mid := 0

    for i := 1; i < len(data); {
        if data[i] < pivot {
            mid++;
            data[i], data[mid] = data[mid], data[i]
        }
        i++;
    }

    data[0], data[mid] = data[mid], data[0]

    if mid < len(data) / 2 {
        return data[:mid], data[mid + 1:]
    } else {
        return data[mid + 1:], data[:mid]
    }
}

func qu_sort(data[] int, wg *sync.WaitGroup) {
    var data_s[] int

    /* perform insertion sort when list becomes less than 30 */
    for len(data) >= 30 {
        data_s, data = partition(data)
        if len(data_s) > 1000 && runtime.NumGoroutine() < runtime.NumCPU() * 8 {
            wg.Add(1)
            /* define and call func */
            go func(d[] int) {
                qu_sort(d, wg)
                wg.Done()
            }(data_s)
        } else {
            qu_sort(data_s, wg)
        }
    }
    insert_sort(data)
}

func random_init(data[] int) {
    for i := 0; i < len(data); i++ {
        data[i] = rand.Intn(1_000_000_000)
    }
}

func QSort(data[] int) {
    var wg sync.WaitGroup
    qu_sort(data, &wg)
    wg.Wait()
}

func main() {
    const LEN = 50_000_000
    var data[LEN] int
    random_init(data[:])
    fmt.Printf("Sorting %d million numbers with Quicksort in Go ...\n", LEN / 1_000_000)
    start := time.Now()

    QSort(data[:])

    fmt.Printf("Time: %.2fs\n", time.Since(start).Seconds())
}
