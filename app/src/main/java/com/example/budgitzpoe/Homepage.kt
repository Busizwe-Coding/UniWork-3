package com.example.budgitzpoe

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import com.example.budgitzpoe.ui.theme.Acid
import com.example.budgitzpoe.ui.theme.DarkGreen
import com.example.budgitzpoe.ui.theme.DeepRed

@Composable
fun homescreen (){
    Surface(modifier= Modifier.fillMaxSize(), color = Acid){
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val(nameRow,topbar,bottombox,card) = createRefs()

            Image(painter = painterResource(id = R.drawable.topbar),null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth().constrainAs(topbar){
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

                    Text(//welcome will be date
                        text = "Welcome Back!",
                        fontSize = 25.sp,
                        color = Acid
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        //this row is for income/expense/total all in one place
                        modifier = Modifier.fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Expense", fontSize = 18.sp, color = Color.Red)
                            Text("-1850", fontSize = 18.sp, color = Color.Red)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Income", fontSize = 18.sp, color = Color.Green)
                            Text("+4300", fontSize = 18.sp, color = Color.Green)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total", fontSize = 18.sp, color = Color.White)
                            Text("2450", fontSize = 18.sp, color = Color.White)
                        }
                    }
                }

                Image(
                    painter = painterResource(id = R.drawable.menuicon),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(y = 4.dp) //becuz the menu cannot sit in a box i manually moved it
                )
            }

            TransactionsList(
                modifier = Modifier.constrainAs(card) {
                    top.linkTo(topbar.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(bottombox.top)
                }
            )

            Box( //bottombar
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

                Row( //all the bottom icons wallets,overview,records,export
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.recordsicon),
                        contentDescription = null
                    )

                    Image(
                        painter = painterResource(id = R.drawable.walletsicon),
                        contentDescription = null
                    )

                    Image(
                        painter = painterResource(id = R.drawable.overviewicon),
                        contentDescription = null
                    )

                    Image(
                        painter = painterResource(id = R.drawable.exporticon),
                        contentDescription = null
                    )
                }

            }

            }
        }
    }

@Composable //the decoration template for transactions
fun TransactionItem(amount: Int, type: String, category: String) {

    val backgroundColor = when (type.lowercase()) {
        "credited" -> Color.Green
        "income" -> Color.White
        "savings" -> Color.White
        else -> Color.Red
    }

    val textColor = when (type.lowercase()) {
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
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 17.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = "$type R$amount",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )

            Text(
                text = category,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable //individual transactions
fun TransactionsList(modifier: Modifier = Modifier) {

    val transactions = listOf(
        Triple(1000, "Debited", "Social"),
        Triple(3000, "Income", "Salary"),
        Triple(300, "Debited", "Food"),
        Triple(150, "Debited", "Transport"),
        Triple(500, "Credited", "Freelance"),
        Triple(800, "Credited", "Birthday"),
        Triple(400, "Savings", "Savings")
    )

    LazyColumn (
        modifier = modifier
    ) {
        items(transactions) { item ->
            TransactionItem(
                amount = item.first,
                type = item.second,
                category = item.third
            )
        }
    }
}

@Composable
@Preview (showBackground = true)
fun previewHomescreen (){
    homescreen()
}