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
import com.example.budgitzpoe.ui.theme.DarkGreen

data class Transaction(
    val amount: Int,
    val type: String,
    val category: String,
    val date: String,
    val description: String,
    val imageRes: Int? = null
)

//the literal card popup
@Composable
fun TransactionDetailsCard(
    transaction: Transaction,
    onDismiss: () -> Unit
) {

    //colour of cards
    val backgroundColor = when (transaction.type.lowercase()) {
        "credited" -> Color.Green
        "income" -> Color.White
        "savings" -> Color.White
        else -> Color.Red
    }

    val textColor = when (transaction.type.lowercase()) {
        "credited" -> DarkGreen
        "income" -> Color.Black
        "savings" -> Color.Black
        else -> Color.White
    }

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

                if (transaction.imageRes != null) {
                    Image(
                        painter = painterResource(id = transaction.imageRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("NO IMAGE", color = textColor)
                }
            }
        }
    }
}