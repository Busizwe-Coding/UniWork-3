package com.example.budgitzpoe

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.budgitzpoe.ui.theme.Acid
import com.example.budgitzpoe.ui.theme.MainWalletColor
import com.example.budgitzpoe.ui.theme.SavingsWalletColor

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun IncomeScreen(
    onRecords: () -> Unit,
    onWallets: () -> Unit,
    onOverviews: () -> Unit,
    onExport: () -> Unit,
    onMenuClick: () -> Unit,
    onAccounts: () -> Unit,
    onExpenses: () -> Unit
) {
    val currentMonth = LocalDate.now().month.getDisplayName(TextStyle.FULL, Locale.ENGLISH).uppercase()

    // 1. Calculate top header metrics (Current Month Only)
    val filteredTransactions = TransactionStore.transactions.filter { transaction ->
        runCatching {
            val date = LocalDate.parse(transaction.date, DateTimeFormatter.ofPattern("dd/MM/yy"))
            date.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH).uppercase() == currentMonth
        }.getOrDefault(false)
    }

    val totalIncome = filteredTransactions.filter { it.type.equals("Income", true) || it.type.equals("Credited", true) }.sumOf { it.amount }
    val totalExpense = filteredTransactions.filter { it.type.equals("Debited", true) || it.type.equals("Expense", true) }.sumOf { it.amount }
    val total = totalIncome - totalExpense

    // 2. Map exactly to explicit grouped allocations matching specified requirement
    val monthlyIncomes = filteredTransactions.filter { it.type.equals("Income", true) || it.type.equals("Credited", true) }

    // Main wallet captures base income
    val incomeWalletTotal = monthlyIncomes.filter {
        it.category.equals("Main", true) || (!it.category.equals("Savings", true) && !it.category.equals("Emergency", true))
    }.sumOf { it.amount }

    // Savings wallet aggregates both Savings and Emergency transactions
    val savingsWalletTotal = monthlyIncomes.filter {
        it.category.equals("Savings", true) || it.category.equals("Emergency", true)
    }.sumOf { it.amount }

    val incomeBreakdownList = listOf(
        Pair("INCOME", incomeWalletTotal),
        Pair("SAVINGS", savingsWalletTotal)
    )
    val incomeColors = listOf(MainWalletColor, SavingsWalletColor)

    Surface(modifier = Modifier.fillMaxSize(), color = Acid) {
        Box(modifier = Modifier.fillMaxSize()) {
            ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                val (nameRow, topbar, bottombox, content) = createRefs()

                Image(
                    painter = painterResource(id = R.drawable.topbar),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth().constrainAs(topbar) { top.linkTo(parent.top) }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 55.dp, start = 16.dp, end = 16.dp)
                        .constrainAs(nameRow) { top.linkTo(parent.top) }
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Image(
                            painter = painterResource(id = R.drawable.menuicon),
                            contentDescription = null,
                            modifier = Modifier.clickable { onMenuClick() }
                        )
                    }

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Expense", fontSize = 22.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                                Text("-${CurrencyStore.selectedCurrency}$totalExpense", fontSize = 22.sp, color = Color.Red)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Income", fontSize = 22.sp, color = Color.Green, fontWeight = FontWeight.Bold)
                                Text("+${CurrencyStore.selectedCurrency}$totalIncome", fontSize = 22.sp, color = Color.Green)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Total", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                Text("${CurrencyStore.selectedCurrency}$total", fontSize = 22.sp, color = Color.White)
                            }
                        }
                    }
                }

                // Main Content Block
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(content) {
                            top.linkTo(topbar.bottom)
                            bottom.linkTo(bottombox.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            height = androidx.constraintlayout.compose.Dimension.fillToConstraints
                        }
                        .background(Color.White)
                ) {

                    // Donut Chart Wheel Area
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(170.dp)
                                .weight(1.1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val strokeWidthPx = 35.dp.toPx()
                                val arcSize = size.minDimension - strokeWidthPx
                                val topLeftOffset = Offset(strokeWidthPx / 2, strokeWidthPx / 2)

                                if (totalIncome == 0) {
                                    // Empty state structural placeholder ring
                                    drawArc(
                                        color = Color(0xFFE0E0E0),
                                        startAngle = 0f,
                                        sweepAngle = 360f,
                                        useCenter = false,
                                        style = Stroke(width = strokeWidthPx),
                                        size = Size(arcSize, arcSize),
                                        topLeft = topLeftOffset
                                    )
                                } else {
                                    var currentStartAngle = -90f
                                    incomeBreakdownList.forEachIndexed { index, pair ->
                                        val amount = pair.second
                                        if (amount > 0) {
                                            val sweepAngle = (amount.toFloat() / totalIncome) * 360f
                                            drawArc(
                                                color = incomeColors[index],
                                                startAngle = currentStartAngle,
                                                sweepAngle = sweepAngle,
                                                useCenter = false,
                                                style = Stroke(width = strokeWidthPx),
                                                size = Size(arcSize, arcSize),
                                                topLeft = topLeftOffset
                                            )
                                            currentStartAngle += sweepAngle
                                        }
                                    }
                                }
                            }
                        }

                        // Right side Total Header Label block
                        Column(
                            modifier = Modifier
                                .weight(0.9f)
                                .padding(start = 12.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "TOTAL INCOME",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Green
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${CurrencyStore.selectedCurrency} $totalIncome",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Green
                            )
                        }
                    }

                    //Two-Row Breakdown List
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        if (totalIncome == 0) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(top = 20.dp), contentAlignment = Alignment.Center) {
                                    Text("No income recorded this month.", color = Color.Gray, fontSize = 14.sp)
                                }
                            }
                        } else {
                            items(incomeBreakdownList.size) { index ->
                                val label = incomeBreakdownList[index].first
                                val amount = incomeBreakdownList[index].second

                                val percentage = if (totalIncome > 0) (amount.toFloat() / totalIncome * 100) else 0f
                                val displayPercentage = if (percentage % 1 == 0f) "${percentage.toInt()}%" else String.format(Locale.ENGLISH, "%.2f%%", percentage)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(incomeColors[index])
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(
                                            text = label,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                    }
                                    Text(
                                        text = displayPercentage,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }

                    // Action Selection Tab Bar Row
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black)
                                .clickable { onAccounts() }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("ACCOUNTS", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black)
                                .clickable { onExpenses() }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("EXPENSES", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("INCOME", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Global Persistent Bottom Navigation Dock Bar
                Box(
                    modifier = Modifier.fillMaxWidth().constrainAs(bottombox) { bottom.linkTo(parent.bottom) }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bottombar),
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Image(painter = painterResource(id = R.drawable.recordsicon), contentDescription = null, modifier = Modifier.clickable { onRecords() })
                        Image(painter = painterResource(id = R.drawable.walletsicon), contentDescription = null, modifier = Modifier.clickable { onWallets() })
                        Image(painter = painterResource(R.drawable.overviewicon), contentDescription = null, modifier = Modifier.clickable { onOverviews() })
                        Image(painter = painterResource(id = R.drawable.exporticon), contentDescription = null, modifier = Modifier.clickable { onExport() })
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PreviewIncomeScreen() {
    IncomeScreen({}, {}, {}, {}, {}, {}, {})
}