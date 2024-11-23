import java.util.concurrent.atomic.AtomicReference

object ConcurrencyTroublesSoln2 {
    // Use AtomicReference to hold the values of value1 and value2 together.
    private val values = new AtomicReference((1000, 0))
    private val sum = new AtomicReference(0)

    def moveOneUnit(): Unit = {
        values.getAndUpdate { case (v1, v2) =>
            // Decrement value1 and increment value2
            val newV1 = v1 - 1
            val newV2 = v2 + 1
            // Reset if value1 reaches 0
            if (newV1 == 0) (1000, 0) else (newV1, newV2)
        }
    }

    def updateSum(): Unit = {
        // Get the current values and update the sum atomically.
        val (v1, v2) = values.get()
        sum.set(v1 + v2)
    }

    def execute(): Unit = {
        while (true) {
            moveOneUnit()
            updateSum()
            Thread.sleep(100)
        }
    }

    def main(args: Array[String]): Unit = {
        for (_ <- 1 to 2) {
            val thread = new Thread {
                override def run(): Unit = execute()
            }
            thread.start()
        }
        while (true) {
            updateSum()
            // Get current sum and values atomically for printing
            val (v1, v2) = values.get()
            println(s"${sum.get()} [$v1 $v2]")
            Thread.sleep(100)
        }
    }
}