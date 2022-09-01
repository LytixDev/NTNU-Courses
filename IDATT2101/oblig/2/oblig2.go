package main

import (
    "fmt"
    "math"
    "time"
)

var ITER = 1000


func my_pow1(x float64, n int) float64 {
    if n == 0 {
        return 1
    }

    return x * my_pow1(x, n - 1)
}

func my_pow2(x float64, n int) float64 {
    if n == 0 {
        return 1
    }

    if n % 2 == 0 {
        return my_pow2(x * x, n / 2)
    }

    return x * my_pow2(x * x, (n - 1) / 2);
}

func lib_pow(x float64, n int) float64 {
    return math.Pow(x, float64(n))
}

func test_case(x float64, n int) {
    res1 := my_pow1(x, n)
    res2 := my_pow2(x, n)
    res3 := lib_pow(x, n)

    fmt.Printf("%f^%d = %f = %f = %f\n", x, n, res1, res2, res3)
}


func performance(x float64, n int, pow_func func(float64, int) float64) {
    start := time.Now()

    for i := 0; i < ITER; i++ {
        pow_func(x, n)
    }

    elapsed := time.Since(start)
    fmt.Printf("%f^%d took %s for %d iterations\n", x, n, elapsed, ITER);

}

func main() {
    x := 1.001
    exponent := 10


    for i := 2; i < 8; i++ {
        exponent = int(lib_pow(10.0, i))
        fmt.Print("2.1-1\t\t")
        performance(x, exponent, my_pow1)

        fmt.Print("2.2-3\t\t")
        performance(x, exponent, my_pow2)

        fmt.Print("go builtin pow\t")
        performance(x, exponent, lib_pow)

        fmt.Println()
    }

}
