package com.example.budgitzpoe

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.intl.Locale
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

object TransactionStore {

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yy")
        return dateFormat.format(Date())
    }

    var transactions = mutableStateListOf(
        Transaction(1000, "Debited", "Social", "27/04/26", "Went to a party"),
        Transaction(4000, "Income", "Salary", "26/04/26", "Monthly salary")
    )
        private set

    fun addTransaction(transaction: Transaction): Transaction {
        val transactionWithDate = transaction.copy(
            date = getCurrentDate()
        )
        transactions.add(transactionWithDate)
        return transactionWithDate
    }
}