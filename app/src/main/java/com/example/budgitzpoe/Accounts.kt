package com.example.budgitzpoe

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.example.budgitzpoe.ui.theme.Acid
import com.example.budgitzpoe.ui.theme.MainWalletColor
import com.example.budgitzpoe.ui.theme.SavingsWalletColor
import com.example.budgitzpoe.ui.theme.EmergencyWalletColor

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AccountsScreen(
    onRecords: () -> Unit,
    onWallets: () -> Unit,
    onOverviews: () -> Unit,
    onExport: () -> Unit,
    onMenuClick: () -> Unit,
    onExpenses: () -> Unit,
    onIncome: () -> Unit
) {
    val currentMonth = LocalDate.now().month.getDisplayName(TextStyle.FULL, Locale.ENGLISH).uppercase()

    //Calculate dynamic amounts for the Top Header (Matching Home Screen Exactly)
    val filteredTransactions = TransactionStore.transactions.filter { transaction ->
        runCatching {
            val date = LocalDate.parse(transaction.date, DateTimeFormatter.ofPattern("dd/MM/yy"))
            date.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH).uppercase() == currentMonth
        }.getOrDefault(false)
    }

    val totalIncome = filteredTransactions.filter { it.type.equals("Income", true) || it.type.equals("Credited", true) }.sumOf { it.amount }
    val totalExpense = filteredTransactions.filter { it.type.equals("Debited", true) || it.type.equals("Expense", true) }.sumOf { it.amount }
    val total = totalIncome - totalExpense

    // Scans transactions across wallets
    val allTransactions = TransactionStore.transactions

    //Filter transactions strictly for the current active month first
    val currentMonthTransactions = allTransactions.filter { transaction ->
        runCatching {
            val date = LocalDate.parse(transaction.date, DateTimeFormatter.ofPattern("dd/MM/yy"))
            date.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH).uppercase() == currentMonth
        }.getOrDefault(false)
    }

    // wallets being affected
    val mainBalance = currentMonthTransactions
        .filter { it.account.equals("Main Account", true) }
        .sumOf { if (it.type.equals("Debited", true) || it.type.equals("Expense", true)) -it.amount else it.amount }

    val savingsBalance = currentMonthTransactions
        .filter { it.account.equals("Savings Account", true) }
        .sumOf { if (it.type.equals("Debited", true) || it.type.equals("Expense", true)) -it.amount else it.amount }

    val emergencyBalance = currentMonthTransactions
        .filter { it.account.equals("Emergency Account", true) }
        .sumOf { if (it.type.equals("Debited", true) || it.type.equals("Expense", true)) -it.amount else it.amount }

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
                ) {
                    Text(
                        text = "ACCOUNTS STATUS",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp)
                    )

                    // GRAPH CANVAS WRAPPER CARD
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 20.dp)
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .border(3.dp, Color.Black, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        val maxGraphScale = 9000f // Ceiling scale matching the top step label
                        val balancesList = listOf(mainBalance.toFloat(), savingsBalance.toFloat(), emergencyBalance.toFloat())
                        val labelsList = listOf("MAIN", "SAVINGS", "EMERGENCY")
                        val colorsList = listOf(MainWalletColor, SavingsWalletColor, EmergencyWalletColor)

                        Row(modifier = Modifier.fillMaxSize()) {

                            // 1. LEFT SIDE Y-AXIS SCALE LABELS (Updated to dynamically match 10 steps)
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(end = 8.dp, bottom = 24.dp), // Matched bottom clearance padding with text label row height
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.End
                            ) {
                                listOf("10000", "9000", "8000", "7000", "6000", "5000", "4000", "3000", "2000", "1000", "0").forEach { step ->
                                    Text(text = step, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                }
                            }

                            // 2. CORE GRAPH BOX WITH INTEGRATED LABELS COLUMN
                            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {

                                val totalBars = 3
                                val maxGraphScale = 10000f // Updated scale ceiling limit config to match your 10k label

                                Column(modifier = Modifier.fillMaxSize()) {

                                    // The drawing canvas now cleanly occupies all space except the text label row row
                                    Canvas(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth()
                                    ) {
                                        val canvasWidth = size.width
                                        val canvasHeight = size.height

                                        // FIXED: Set segmentsCount to 10 to draw exactly 10 matching blocks for your 11 scale points
                                        val segmentsCount = 10
                                        val verticalStep = canvasHeight / segmentsCount
                                        for (i in 0..segmentsCount) {
                                            val currentY = canvasHeight - (i * verticalStep)
                                            drawLine(
                                                color = Color(0xFFF0F0F0),
                                                start = Offset(0f, currentY),
                                                end = Offset(canvasWidth, currentY),
                                                strokeWidth = 2f
                                            )
                                        }

                                        val barWidth = canvasWidth / (totalBars * 2)
                                        val spacing = canvasWidth / (totalBars * 2)

                                        for (idx in 0 until totalBars) {
                                            val currentBalanceValue = balancesList[idx].coerceIn(0f, maxGraphScale)
                                            val calculatedBarHeight = (currentBalanceValue / maxGraphScale) * canvasHeight

                                            val startPointX = spacing + idx * (barWidth + spacing)
                                            val startPointY = canvasHeight - calculatedBarHeight

                                            // Draw Solid Colored Bar
                                            drawRect(
                                                color = colorsList[idx],
                                                topLeft = Offset(startPointX, startPointY),
                                                size = Size(barWidth, calculatedBarHeight)
                                            )

                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 4.dp)
                                    ) {
                                        labelsList.forEach { label ->
                                            Box(
                                                modifier = Modifier.weight(1f),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = label,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = Color.Black
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // TAB BAR SWITCH BUTTONS
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("ACCOUNTS", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
                                .background(Color.Black)
                                .clickable { onIncome() }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("INCOME", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // BOTTOM SYSTEM NAVIGATION BAR
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
fun PreviewAccountsScreen() {
    AccountsScreen({}, {}, {}, {}, {}, {}, {})
}