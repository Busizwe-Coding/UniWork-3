package com.example.budgitzpoe

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object TransactionStore {

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDate(): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")
        return LocalDate.now().format(formatter)
    }

    var transactions = mutableStateListOf(
        Transaction(1000, "Debited", "Social", "27/04/26", "Went to a party"),
        Transaction(3000, "Income", "Salary", "26/04/26", "Monthly salary")
    )
        private set

    @RequiresApi(Build.VERSION_CODES.O)
    fun addTransaction(transaction: Transaction) {

        val transactionWithDate = transaction.copy(
            date = getCurrentDate()
        )

        transactions.add(transactionWithDate)
    }
}
