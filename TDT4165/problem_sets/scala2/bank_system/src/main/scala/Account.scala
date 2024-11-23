
class Account(val code : String, val balance: Double) {

    def withdraw(amount: Double) : Either[String, Account] = {
        if (amount < 0) {
            return Left("Can not withdraw negative amount")
        }

        val new_balance = balance - amount
        if (new_balance < 0) {
            return Left("Balance less than zero")
        }
        Right(new Account(code, new_balance))
    }

    def deposit(amount: Double) : Either[String, Account] = {
        if (amount < 0) {
            return Left("Can not withdraw negative amount")
        }
        Right(new Account(code, balance + amount))
    }
}
