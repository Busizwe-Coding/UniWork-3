package com.example.budgitzpoe

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle as DateTextStyle
import java.util.Locale
import com.example.budgitzpoe.ui.theme.Acid

data class BudgetJar(
    val categoryName: String,
    val budgetLimit: Int,
    val customColor: Color
)

// STATE STORE FOR SAVING BUDGET DATA
object BudgetStore {
    // Keeps the default premade categories
    val jars = mutableStateListOf(
        BudgetJar("SOCIAL", 0, Color.White),
        BudgetJar("FOOD", 0, Color.White),
        BudgetJar("TRANSPORT", 0, Color.White),
        BudgetJar("SHOPPING", 0, Color.White),
        BudgetJar("BILLS", 0, Color.White)
    )

    fun updateJar(updatedJar: BudgetJar) {
        val index = jars.indexOfFirst { it.categoryName == updatedJar.categoryName }
        if (index != -1) {
            jars[index] = updatedJar
        }
    }

    // New function to add a jar when a custom category is made
    fun addJar(categoryName: String) {
        val upperName = categoryName.trim().uppercase()
        // Prevent duplicate jars from being created
        val exists = jars.any { it.categoryName == upperName }
        if (!exists && upperName.isNotBlank()) {
            jars.add(BudgetJar(upperName, 0, Color.White))
        }
    }

    //allow deleting a jar
    fun deleteJar(categoryName: String) {
        jars.removeAll { it.categoryName.equals(categoryName, ignoreCase = true) }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BudgetsScreen(
    onMenuClick: () -> Unit
) {
    var selectedJar by remember { mutableStateOf<BudgetJar?>(null) }
    var showMonths by remember { mutableStateOf(false) }

    val currentMonth = LocalDate.now()
        .month
        .getDisplayName(DateTextStyle.FULL, Locale.ENGLISH)
        .uppercase()

    var selectedMonth by remember { mutableStateOf(currentMonth) }

    // Exactly matching homescreen date filtering
    val currentMonthTransactions = TransactionStore.transactions.filter { transaction ->
        runCatching {
            val date = LocalDate.parse(transaction.date, DateTimeFormatter.ofPattern("dd/MM/yy"))
            date.month.getDisplayName(DateTextStyle.FULL, Locale.ENGLISH).uppercase() == selectedMonth
        }.getOrDefault(false)
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Acid) { // Uses exact same primary Acid tint

        Box(modifier = Modifier.fillMaxSize()) {

            ConstraintLayout(modifier = Modifier.fillMaxSize()) {

                val (nameRow, topbar, bottombox, card) = createRefs()

                // Header Top Image (Identical to Homescreen)
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

                // Customized information alignment matching structural padding metrics
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
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                                Text(
                                    text = "MY BUDGET JARS",
                                    fontSize = 25.sp,
                                    color = Acid, // Changed to black for perfect contrast against Acid
                                    fontWeight = FontWeight.Bold
                                )
                        }
                    }

                    // Month and Menu buttons (Identical layout to Homescreen)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black)
                                .clickable { showMonths = true }
                                .padding(horizontal = 30.dp, vertical = 10.dp)
                        ) {
                            Text(selectedMonth, color = Color.White, fontSize = 18.sp)
                        }

                        Image(
                            painter = painterResource(id = R.drawable.menuicon),
                            contentDescription = null,
                            modifier = Modifier.clickable { onMenuClick() }
                        )
                    }
                }

                // Scrollable Budget Cards layout
                LazyColumn(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .constrainAs(card) {
                            top.linkTo(topbar.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(bottombox.top)
                        },
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(BudgetStore.jars) { jar ->

                        val totalSpentOnCategory = currentMonthTransactions
                            .filter { it.category.equals(jar.categoryName, ignoreCase = true) && it.type.equals("Debited", ignoreCase = true) }
                            .sumOf { it.amount }

                        val remainingBudget = jar.budgetLimit - totalSpentOnCategory
                        val isOverBudget = remainingBudget < 0

                        val cardBackgroundColor = if (isOverBudget) Color.Red else jar.customColor

                        val displayAmountString = if (isOverBudget) "-R$totalSpentOnCategory" else "R$totalSpentOnCategory"
                        val contentColor = if (cardBackgroundColor == Color.White) Color.Black else Color.White

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp)
                                .background(cardBackgroundColor, RoundedCornerShape(10.dp))
                                .clickable { selectedJar = jar },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${jar.categoryName}-JAR",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    text = displayAmountString,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }

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
                }
            }
            selectedJar?.let { jar ->
                JarEditDialog(
                    jar = jar,
                    currentMonthTransactions = currentMonthTransactions, // Passed here
                    onDismiss = { selectedJar = null }
                )
            }

            if (showMonths) {
                MonthPickerDialog(
                    onSelect = {
                        selectedMonth = it
                        showMonths = false
                    },
                    onDismiss = { showMonths = false }
                )
            }
        }
    }
}

@Composable
fun JarEditDialog(
    jar: BudgetJar,
    currentMonthTransactions: List<Transaction>,
    onDismiss: () -> Unit
) {
    var limitInput by rememberSaveable { mutableStateOf(if (jar.budgetLimit == 0) "" else jar.budgetLimit.toString()) }

    val totalSpentOnCategory = currentMonthTransactions
        .filter { it.category.equals(jar.categoryName, ignoreCase = true) && it.type.equals("Debited", ignoreCase = true) }
        .sumOf { it.amount }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configure ${jar.categoryName}-JAR", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    text = "Total Spent This Month: R$totalSpentOnCategory",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (totalSpentOnCategory > jar.budgetLimit && jar.budgetLimit > 0) Color.Red else Color.Black
                )

                Spacer(Modifier.height(12.dp))

                Text("Set Budget Limit (R):", fontSize = 16.sp)
                Spacer(Modifier.height(6.dp))

                BasicTextField(
                    value = limitInput,
                    onValueChange = { input ->
                        if (input.isEmpty() || input.all { it.isDigit() }) {
                            limitInput = input
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        .padding(8.dp),
                    textStyle = TextStyle(fontSize = 18.sp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                onClick = {
                    val parsedLimit = limitInput.toIntOrNull() ?: 0
                    // Saves the limit while keeping the default white coloring template
                    BudgetStore.updateJar(jar.copy(budgetLimit = parsedLimit, customColor = Color.White))
                    onDismiss()
                }
            ) {
                Text("SAVE JAR", color = Color.White)
            }
        },
        dismissButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Feature: Delete Button inside the popup configuration card
                TextButton(
                    onClick = {
                        BudgetStore.deleteJar(jar.categoryName)
                        onDismiss() // Close the dialog window state
                    }
                ) {
                    Text("DELETE JAR", color = Color.Red, fontWeight = FontWeight.Bold)
                }

                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = Color.Black)
                }
            }
        }
    )
}

@Composable
fun menuDrawer(
    isOpen: Boolean,
    currentScreen: String,
    onClose: () -> Unit,
    onNavigateToBudgets: () -> Unit,
    onNavigateToHome: () -> Unit,
    onLogout: () -> Unit
) {
    val offsetX by animateDpAsState(
        targetValue = if (isOpen) 0.dp else (-300).dp,
        label = "drawerAnimation"
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (isOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable() { onClose() }
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxHeight()
                .width(280.dp)
                .offset(x = offsetX),
            color = Color.Black
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {

                Spacer(modifier = Modifier.height(40.dp))

                // 3. Dynamic Button Assignment based on currentScreen
                if (currentScreen == "budgets") {
                    MenuItem("Home") {
                        onNavigateToHome()
                    }
                } else {
                    MenuItem("Budgets") {
                        onNavigateToBudgets()
                    }
                }
                Divider(color = Color.DarkGray, thickness = 3.dp)

                MenuItem("Currency")
                Divider(color = Color.DarkGray, thickness = 3.dp)

                MenuItem("Logout") {
                    onLogout()
                }

                Spacer(modifier = Modifier.weight(1f))

                Divider(color = Color.DarkGray, thickness = 3.dp)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.helpicon),
                        contentDescription = "Help",
                        modifier = Modifier
                            .size(50.dp)
                            .padding(end = 12.dp)
                    )
                    Text(
                        text = "Help",
                        color = Color.White,
                        fontSize = 22.sp
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PreviewBudgetsScreen() {
    BudgetsScreen(
        onMenuClick = {}
    )
}