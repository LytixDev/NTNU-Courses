import collection.mutable.Map
import scala.util.Random

class Bank(val allowedAttempts: Integer = 3) {

    private val accountsRegistry : Map[String,Account] = Map()

    val transactionsPool: TransactionPool = new TransactionPool()
    val completedTransactions: TransactionPool = new TransactionPool()


    def processing : Boolean = !transactionsPool.isEmpty

    def transfer(from: String, to: String, amount: Double): Unit = {
        (getAccount(from), getAccount(to)) match {
            case (Some(_), Some(_)) =>
                transactionsPool.add(new Transaction(from, to, amount))
        }
    }

    def processTransactions: Unit = {
        val workers : List[Thread] = transactionsPool.iterator.toList
                                               .filter(_.getStatus() == TransactionStatus.PENDING)
                                               .map(processSingleTransaction)

        workers.map( element => element.start() )
        workers.map( element => element.join() )

        val succeded : List[Transaction] = transactionsPool.iterator.toList
                                                           .filter(_.getStatus() == TransactionStatus.SUCCESS)

        val failed : List[Transaction] = transactionsPool.iterator.toList
                                                         .filter(_.getStatus() == TransactionStatus.FAILED)

        transactionsPool.iterator.toList.map(t => transactionsPool.remove(t))

        succeded.map(t => transactionsPool.remove(t))
        succeded.map(t => completedTransactions.add(t))

        failed.map(t => {
            if (t.noMoreRetries()) {
                completedTransactions.add(t)
            } else {
                transactionsPool.remove(t)
                t.setStatus(TransactionStatus.PENDING)
                transactionsPool.add(t)
            }
        })

        if(!transactionsPool.isEmpty) {
            processTransactions
        }
    }

    private def processSingleTransaction(t : Transaction) : Thread =  {
        new Thread(new Runnable {
            def run(): Unit = {
                t.incrementAttempts()

                accountsRegistry.synchronized {
                    (getAccount(t.from), getAccount(t.to)) match {
                        case (Some(from), Some(to)) =>
                            val updatedFrom = from.withdraw(t.amount)
                            val updatedTo = to.deposit(t.amount)

                            (updatedFrom, updatedTo) match {
                                case (Right(a), Right(b)) =>
                                    accountsRegistry += (a.code -> a)
                                    accountsRegistry += (b.code -> b)
                                    t.setStatus(TransactionStatus.SUCCESS)
                                case _ =>
                                    t.setStatus(TransactionStatus.FAILED)
                            }
                        case _ =>
                            t.setStatus(TransactionStatus.FAILED)
                    }
                }

            }
        })
    }

    def createAccount(initialBalance: Double) : String = {
        val code = Random.alphanumeric.take(15).mkString
        val newAccount = new Account(code, initialBalance)
        accountsRegistry += (code -> newAccount)
        code
    }

    def getAccount(code : String) : Option[Account] = {
        accountsRegistry.get(code)
    }
}
