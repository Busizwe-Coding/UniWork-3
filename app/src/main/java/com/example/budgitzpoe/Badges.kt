package com.example.budgitzpoe

import androidx.compose.ui.tooling.preview.Preview
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import com.example.budgitzpoe.ui.theme.Acid

// 1. DATA DEFINITIONS
data class Badge(
    val id: String,
    val name: String,
    val description: String
)

object BadgeStore {
    val badgeList = listOf(
        Badge("daily", "DAILY USER", "Create one new expense everyday for 7 consecutive days."),
        Badge("irresponsible", "IRRESPONSIBLE", "Spent over budget on any of your active tracking jars."),
        Badge("money_wise", "MONEY WISE", "Successfully deposited an entry directly into your savings balance."),
        Badge("bulk", "BULK BUYER", "Logged 10 or more separate accounting transactions inside a single calendar day.")
    )

    // ENGINE CODE TO AUTOMATICALLY TRACK AND EVALUATE UNLOCK CRITERIA
    @RequiresApi(Build.VERSION_CODES.O)
    fun isBadgeUnlocked(badgeId: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")
        val allTransactions = TransactionStore.transactions

        return when (badgeId) {
            "daily" -> {
                val expenseDates = allTransactions
                    .filter { it.type.equals("Debited", ignoreCase = true) || it.type.equals("Expense", ignoreCase = true) }
                    .mapNotNull { runCatching { LocalDate.parse(it.date, formatter) }.getOrNull() }
                    .toSet()

                if (expenseDates.size < 7) return false

                // Scan list chronologically to evaluate continuous streak rows
                val sortedDates = expenseDates.sorted()
                var consecutiveDays = 1
                var maxConsecutive = 1

                for (i in 0 until sortedDates.size - 1) {
                    if (sortedDates[i].plusDays(1) == sortedDates[i + 1]) {
                        consecutiveDays++
                        if (consecutiveDays > maxConsecutive) maxConsecutive = consecutiveDays
                    } else if (sortedDates[i] != sortedDates[i + 1]) {
                        consecutiveDays = 1
                    }
                }
                maxConsecutive >= 7
            }

            "irresponsible" -> {
                // Evaluates if spending has overrun limits across any active monthly budget jar
                BudgetStore.jars.any { jar ->
                    val totalSpent = allTransactions
                        .filter { it.category.equals(jar.categoryName, ignoreCase = true) && (it.type.equals("Debited", ignoreCase = true) || it.type.equals("Expense", ignoreCase = true)) }
                        .sumOf { it.amount }
                    jar.budgetLimit > 0 && totalSpent > jar.budgetLimit
                }
            }

            "money_wise" -> {
                // Unlocks if the user adds an Income type into a Savings balance account structure
                allTransactions.any {
                    (it.type.equals("Income", ignoreCase = true) || it.type.equals("Credited", ignoreCase = true)) &&
                            it.category.equals("Savings", ignoreCase = true)
                }
            }

            "bulk" -> {
                // Groups entries together across specific date stamps to look for volume blocks
                val transactionsPerDay = allTransactions.groupBy { it.date }
                transactionsPerDay.values.any { it.size >= 10 }
            }

            else -> false
        }
    }
}

//MAIN BADGES SCREEN
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BadgesScreen(
    onMenuClick: () -> Unit
) {
    var selectedBadge by remember { mutableStateOf<Badge?>(null) }
    val currentMonth = LocalDate.now().month.getDisplayName(TextStyle.FULL, Locale.ENGLISH).uppercase()

    Surface(modifier = Modifier.fillMaxSize(), color = Acid) {
        Box(modifier = Modifier.fillMaxSize()) {
            ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                val (nameRow, topbar, bottombox, gridArea) = createRefs()

                Image(
                    painter = painterResource(id = R.drawable.topbar),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth().constrainAs(topbar) { top.linkTo(parent.top) }
                )

                // Layout Title Construction
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 55.dp, start = 16.dp, end = 16.dp)
                        .constrainAs(nameRow) { top.linkTo(parent.top) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 45.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("MY BADGES", fontSize = 35.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black)
                                .padding(horizontal = 30.dp, vertical = 10.dp)
                        ) {
                            Text(currentMonth, color = Color.White, fontSize = 18.sp)
                        }

                        Image(
                            painter = painterResource(id = R.drawable.menuicon),
                            contentDescription = null,
                            modifier = Modifier.clickable { onMenuClick() }
                        )
                    }
                }

                // Grid (scrollable) with badges
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 80.dp, bottom = 16.dp)
                        .constrainAs(gridArea) {
                            top.linkTo(nameRow.bottom) // Anchor to nameRow instead of topbar to prevent overlapping
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(bottombox.top)
                            height = androidx.constraintlayout.compose.Dimension.fillToConstraints
                        },
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(BadgeStore.badgeList) { badge ->
                        val isUnlocked = try {
                            BadgeStore.isBadgeUnlocked(badge.id)
                        } catch (e: Exception) {
                            false // Safe processing fallback
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .border(3.dp, Color.Black, RoundedCornerShape(16.dp))
                                .background(if (isUnlocked) Acid else Color.White, RoundedCornerShape(16.dp))
                                .clickable { selectedBadge = badge },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isUnlocked) {
                                Image(
                                    painter = painterResource(id = R.drawable.badgeicon),
                                    contentDescription = badge.name,
                                    modifier = Modifier.size(90.dp),
                                    contentScale = ContentScale.Fit
                                )
                            } else {
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    
                                    Box(
                                        modifier = Modifier.size(100.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.badgeicon),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Fit
                                        )
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Locked State",
                                            tint = Color.Black.copy(alpha = 0.6f),
                                            modifier = Modifier.size(60.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = badge.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier.fillMaxWidth().constrainAs(bottombox) { bottom.linkTo(parent.bottom) }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bottombar),
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Popup with badge info
            selectedBadge?.let { badge ->
                AlertDialog(
                    onDismissRequest = { selectedBadge = null },
                    title = { Text(badge.name, fontWeight = FontWeight.Bold) },
                    text = { Text(badge.description, fontSize = 16.sp) },
                    confirmButton = {
                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            onClick = { selectedBadge = null }
                        ) {
                            Text("OK", color = Color.White)
                        }
                    }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PreviewBadgesScreen() {
    BadgesScreen(
        onMenuClick = {}
    )
}