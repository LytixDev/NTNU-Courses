import scala.collection.mutable


object TransactionStatus extends Enumeration {
  val SUCCESS, PENDING, FAILED = Value
}

class TransactionPool {
    private val transactions: mutable.Queue[Transaction] = mutable.Queue.empty

    // Remove and the transaction from the pool
    def remove(t: Transaction): Boolean = this.synchronized {
      val idx = transactions.indexOf(t)
      if (idx == -1) {
        return false
      }
      transactions.dequeueAll(_ == t)
      return true
    }

    // Return whether the queue is empty
    def isEmpty: Boolean = this.synchronized {
      return transactions.isEmpty
    }

    // Return the size of the pool
    def size: Integer = this.synchronized {
      return transactions.size
    }

    // Add new element to the back of the queue
    def add(t: Transaction): Boolean = this.synchronized {
      transactions.enqueue(t)
      return true
    }

    // Return an iterator to allow you to iterate over the queue
    def iterator : Iterator[Transaction] = this.synchronized {
      return transactions.iterator
    }

}

class Transaction(val from: String,
                  val to: String,
                  val amount: Double,
                  val retries: Int = 3) {

  private var status: TransactionStatus.Value = TransactionStatus.PENDING
  private var attempts = 0

  def getStatus() = status

  def setStatus(s: TransactionStatus.Value): Unit = status = s

  def noMoreRetries(): Boolean = attempts >= retries

  def incrementAttempts(): Unit = {
    attempts += 1
  }
}
