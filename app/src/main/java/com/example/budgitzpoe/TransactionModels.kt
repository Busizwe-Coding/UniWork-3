package com.example.budgitzpoe

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.budgitzpoe.ui.theme.DarkGreen

data class Transaction(
    val amount: Int,
    val type: String,
    val category: String,
    val date: String,
    val description: String,
    val imageUri: String? = null
)

//the literal card popup
@Composable
fun TransactionDetailsCard(
    transaction: Transaction,
    onDismiss: () -> Unit,
    onDelete: (Transaction) -> Unit
) {

    //colour of cards
    val isIncome = transaction.type.equals("Income", true)

    val backgroundColor = when {
        isIncome && transaction.category.equals("Salary", true) -> Color.White
        isIncome && transaction.category.equals("Savings", true) -> Color.White
        isIncome -> Color.Green
        transaction.type.equals("Credited", true) -> Color.Green
        transaction.type.equals("Savings", true) -> Color.White
        else -> Color.Red
    }

    val textColor = when (backgroundColor) {
        Color.White -> Color.Black
        Color.Green -> DarkGreen
        else -> Color.White
    }

    //look of cards
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable { onDismiss() }
    ) {

        Column (
            modifier = Modifier
                .align(Alignment.Center)
                .padding(20.dp)
                .background(backgroundColor, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {

            Text("Category: ${transaction.category.uppercase()}", color = textColor)
            Spacer(Modifier.height(8.dp))

            Text("Date: ${transaction.date}", color = textColor)
            Spacer(Modifier.height(8.dp))

            Text("Amount: ${transaction.amount}", color = textColor)
            Spacer(Modifier.height(8.dp))

            Text("Description:", color = textColor)
            Text(transaction.description, color = textColor)

            Spacer(Modifier.height(12.dp))

            Text("IMAGE:", color = textColor)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .border(3.dp, textColor, RoundedCornerShape(8.dp))
                    .clickable() { /* open image */ },
                contentAlignment = Alignment.Center
            ) {

                if (transaction.imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(transaction.imageUri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("NO IMAGE", color = textColor)
                }

            }
            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Red, RoundedCornerShape(8.dp))
                    .clickable {
                        onDelete(transaction)
                        onDismiss()
                    }
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("DELETE TRANSACTION", color = Color.White)
            }
        }
    }
}