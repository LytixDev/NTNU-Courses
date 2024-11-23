object ConcurrencyTroublesSoln1 {
    private var value1: Int = 1000
    private var value2: Int = 0
    private var sum: Int = 0

    def moveOneUnit(): Unit = synchronized {
        value1 -= 1;
        value2 += 1;
        if(value1 == 0) {
            value1 = 1000;
            value2 = 0;
        }
    }

    def updateSum(): Unit = {
        sum = value1 + value2;
    }

    def execute(): Unit = {
        while (true) {
            moveOneUnit();
            updateSum();
            Thread.sleep(100)
        }
    }

    def main(args: Array[String]): Unit = {
        for (i <- 1 to 2) {
            val thread = new Thread {
                override def run(): Unit = execute()
            }
            thread.start()
        }
        while (true) {
            updateSum()
            println(sum + " [" + value1 + " " + value2 + "]")
        }
    }
}