package com.example.budgitzpoe

import android.R.attr.top
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import com.example.budgitzpoe.ui.theme.Acid
import com.example.budgitzpoe.ui.theme.DarkGreen
import com.example.budgitzpoe.ui.theme.DeepRed


@Composable
fun homescreen(
    onAddTransaction: () -> Unit,
    onWallets: () -> Unit,
    onExport: () -> Unit,
    onMenuClick: () -> Unit
) {

    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }

    Surface(modifier = Modifier.fillMaxSize(), color = Acid) {

        Box(modifier = Modifier.fillMaxSize()) {

            ConstraintLayout(modifier = Modifier.fillMaxSize()) {

                val (nameRow, topbar, bottombox, card, addBtn) = createRefs()

                Image(
                    painter = painterResource(id = R.drawable.topbar),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(topbar) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 55.dp, start = 16.dp, end = 16.dp)
                        .constrainAs(nameRow) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                ) {

                    Column {

                        Text("Welcome Back!", fontSize = 25.sp, color = Acid)

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 15.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Expense", fontSize = 22.sp, color = Color.Red)
                                Text("-1850", fontSize = 22.sp, color = Color.Red)
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Income", fontSize = 22.sp, color = Color.Green)
                                Text("+4300", fontSize = 22.sp, color = Color.Green)
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Total", fontSize = 22.sp, color = Color.White)
                                Text("2450", fontSize = 22.sp, color = Color.White)
                            }
                        }
                    }

                    Image(
                        painter = painterResource(id = R.drawable.menuicon),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clickable { onMenuClick() }
                    )
                }

                TransactionsList(
                    modifier = Modifier.constrainAs(card) {
                        top.linkTo(topbar.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(bottombox.top)
                    },
                    onTransactionClick = { selectedTransaction = it }
                )

                Image(
                    painter = painterResource(id = R.drawable.baseline_add_circle_24),
                    contentDescription = null,
                    modifier = Modifier
                        .size(70.dp)
                        .constrainAs(addBtn) {
                            end.linkTo(parent.end, margin = 20.dp)
                            bottom.linkTo(bottombox.top, margin = 16.dp)
                        }
                        .clickable { onAddTransaction() }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(bottombox) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.bottombar),
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        Image(painter = painterResource(id = R.drawable.recordsicon), contentDescription = null)

                        Image(
                            painter = painterResource(id = R.drawable.walletsicon),
                            contentDescription = null,
                            modifier = Modifier.clickable { onWallets() }
                        )

                        Image(painter = painterResource(id = R.drawable.overviewicon), contentDescription = null)

                        Image(
                            painter = painterResource(id = R.drawable.exporticon),
                            contentDescription = null,
                            modifier = Modifier.clickable { onExport() }
                        )
                    }
                }
            }

            selectedTransaction?.let {
                TransactionDetailsCard(
                    transaction = it,
                    onDismiss = { selectedTransaction = null }
                )
            }
        }
    }
}

//each transaction item
@Composable
fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit
) {

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

    Column(
        modifier = Modifier
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .clickable { onClick() }
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = "${transaction.type} R${transaction.amount}",
                color = textColor,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = transaction.category,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TransactionsList(
    modifier: Modifier = Modifier,
    onTransactionClick: (Transaction) -> Unit
) {

    val transactions = listOf(
        Transaction(1000, "Debited", "Social", "27/04/26", "Went to a party"),
        Transaction(3000, "Income", "Salary", "26/04/26", "Monthly salary"),
        Transaction(300, "Debited", "Food", "25/04/26", "Groceries"),
        Transaction(150, "Debited", "Transport", "24/04/26", "Uber ride"),
        Transaction(500, "Credited", "Freelance", "23/04/26", "Design work"),
        Transaction(800, "Credited", "Birthday", "22/04/26", "Gift money"),
        Transaction(400, "Savings", "Savings", "21/04/26", "Saved money")
    )

    LazyColumn(modifier = modifier) {
        items(transactions) { item ->
            TransactionItem(
                transaction = item,
                onClick = { onTransactionClick(item) }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun previewHomescreen() {
    homescreen(
        onAddTransaction = {},
        onWallets = {},
        onExport = {},
        onMenuClick = {}
    )
}