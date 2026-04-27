package com.example.budgitzpoe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import com.example.budgitzpoe.ui.theme.DeepRed

data class Wallet(
    val name: String,
    val balance: Int,
    val minSpend: Int,
    val maxSpend: Int,
    val color: Color
)

@Composable
fun WalletPopup(
    wallet: Wallet,
    onDismiss: () -> Unit
) {

    var isEditing by remember { mutableStateOf(false) }

    // Use the current wallet from WalletStore to always show latest data
    val currentWallet = WalletStore.wallets.firstOrNull { it.name == wallet.name } ?: wallet
    var minSpend by remember { mutableStateOf(currentWallet.minSpend.toString()) }
    var maxSpend by remember { mutableStateOf(currentWallet.maxSpend.toString()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable { onDismiss() }
    ) {

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(20.dp)
                .background(currentWallet.color, RoundedCornerShape(12.dp))
                .border(3.dp, Color.Black, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {

            Text(currentWallet.name.uppercase(), fontSize = 30.sp, color = Color.Black)

            Spacer(Modifier.height(12.dp))

            Text("BALANCE: ${currentWallet.balance}", fontSize = 30.sp, color = Color.Black)

            Spacer(Modifier.height(8.dp))

            if (isEditing) {

                Text("Min Spending:", fontSize = 16.sp, color = Color.Black)
                BasicTextField(
                    value = minSpend,
                    onValueChange = { input ->
                        if (input.isEmpty() || input.all { char -> char.isDigit() }) {
                            minSpend = input
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.Black, fontSize = 18.sp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))

                Text("Max Spending:", fontSize = 16.sp, color = Color.Black)
                BasicTextField(
                    value = maxSpend,
                    onValueChange = { input ->
                        if (input.isEmpty() || input.all { char -> char.isDigit() }) {
                            maxSpend = input
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.Black, fontSize = 18.sp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(Modifier.height(12.dp))

                Row {
                    Text(
                        text = "SAVE",
                        modifier = Modifier
                            .clickable {
                                val updatedWallet = currentWallet.copy(
                                    minSpend = minSpend.toIntOrNull() ?: currentWallet.minSpend,
                                    maxSpend = maxSpend.toIntOrNull() ?: currentWallet.maxSpend
                                )

                                WalletStore.updateWallet(updatedWallet)
                                isEditing = false
                            }
                            .padding(8.dp),
                        color = Color.Black,
                        fontSize = 18.sp
                    )

                    Spacer(Modifier.width(20.dp))

                    Text(
                        text = "DELETE",
                        modifier = Modifier
                            .clickable {
                                WalletStore.wallets.removeAll { it.name == currentWallet.name }
                                onDismiss()
                            }
                            .padding(8.dp),
                        color = DeepRed,
                        fontSize = 18.sp
                    )
                }

            } else {
                Text("Minimum Spending: ${currentWallet.minSpend}", fontSize = 18.sp, color = Color.Black)
                Spacer(Modifier.height(6.dp))
                Text("Maximum Spending: ${currentWallet.maxSpend}", fontSize = 18.sp, color = Color.Black)
                Spacer(Modifier.height(12.dp))

                Text(
                    text = "EDIT",
                    modifier = Modifier
                        .clickable {
                            // Reset the edit fields to current values when entering edit mode
                            minSpend = currentWallet.minSpend.toString()
                            maxSpend = currentWallet.maxSpend.toString()
                            isEditing = true
                        }
                        .padding(8.dp),
                    color = Color.Black,
                    fontSize = 18.sp
                )
            }
        }
    }
}
object WalletStore {

    val wallets = mutableStateListOf(
        Wallet("MAIN ACCOUNT", 4000, 0, 4000, Color.White),
        Wallet("SAVINGS ACCOUNT", 3000, 0, 20000, Color(0xFF39FF14)),
        Wallet("EMERGENCY ACCOUNT", 1000, 0, 5000, Color.Red)
    )

    fun addWallet(wallet: Wallet) {
        wallets.add(wallet)
    }

    fun updateWallet(updated: Wallet) {
        val index = wallets.indexOfFirst { it.name == updated.name }
        if (index != -1) {
            wallets[index] = updated
        }
    }

    fun hasSufficientBalance(walletName: String, amount: Int): Boolean {
        val wallet = wallets.firstOrNull { it.name == walletName }
        return wallet != null && wallet.balance >= amount
    }
}