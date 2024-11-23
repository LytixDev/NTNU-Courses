object Task1 extends App {
    def sum(arr: Array[Int]) : Int = {
        var sum = 0
        for (el <- arr) {
            sum += el
        }
        return sum
    }

    def sumRecurse(arr: Array[Int]) : Int = {
        if (arr.length == 0) {
            return 0
        }
        return arr(0) + sumRecurse(arr.slice(1, arr.length))
    }

    def fib(n: BigInt) : BigInt = {
        if (n == 1) {
            return 1
        }
        if (n == 0) {
            return 0
        }
        return fib(n - 1) + fib(n - 2)
    }

    // a)
    val n = 50
    var arr = Array.empty[Int]

    for (i <- 0 to n) {
        arr = arr :+ i
    }

    // b)
    val result = sum(arr)
    println(result)
    
    // c)
    val resultRecurse = sumRecurse(arr)
    println(resultRecurse)

    // d)
    val resultFib = fib(12)
    println(resultFib)
}


object Task2 extends App {
    def quadraticEquation(A: Double, B: Double, C: Double): (Boolean, Double, Double) = {
        val discriminant = B*B - 4.0*A*C
        if (discriminant < 0.0) {
            return (false, 0.0, 0.0)
        }

        val discriminantRoot = Math.sqrt(discriminant)
        val x1 = (-B + discriminantRoot) / (2.0 * A)
        val x2 = (-B - discriminantRoot) / (2.0 * A)
        return (true, x1, x2)
    }

    def quadratic(A: Double, B: Double, C: Double): Double => Double = {
        return (x: Double) => A*x*x + B*x + C
    }

    val (realSol, x1, x2) = quadraticEquation(2.0, 1.0, -1.0)
    if (realSol) {
        printf("Real solutions for A=2 B=1 C=-1: %f %f\n", x1, x2)
    } else {
        printf("No real solutions\n")
    }

    val (realSol2, x12, x22) = quadraticEquation(2.0, 1.0, 2.0)
    if (realSol2) {
        printf("Real solutions for A=2 B=1 C=2: %f %f\n", x12, x22)
    } else {
        printf("No real solutions\n")
    }

    val f = quadratic(2.0, 1.0, -1.0)
    val fRes = f(3)
    printf("2*3^2 + 3 - 1 = %f\n", fRes)
}

object Task3a extends App {
    // a)
    def initThread(task: () => Unit): Thread = {
        return new Thread(new Runnable {
            def run(): Unit = task()
        })
    }
    val thread = initThread(() => println("Hello, World!"))
    // thread.run()
}
