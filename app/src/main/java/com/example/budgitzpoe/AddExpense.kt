package com.example.budgitzpoe

import android.R.attr.description
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.budgitzpoe.ui.theme.Acid
import com.example.budgitzpoe.ui.theme.Charcoal
import com.example.budgitzpoe.ui.theme.DarkGreen
import com.example.budgitzpoe.ui.theme.DeepRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddExpenseScreen(
    onCancel: () -> Unit,
    onSave: (Transaction) -> Unit
) {

    var selectedTab by remember { mutableStateOf("Expense") }
    var selectedAccount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var selectedWallet by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("0") }

    var showCategoryPicker by remember { mutableStateOf(false) }
    var categoryOptions by remember { mutableStateOf(listOf<String>()) }

    var showWalletPicker by remember { mutableStateOf(false) }

    val context = LocalContext.current

    var selectedImageUri by remember { mutableStateOf<String?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri?.toString()
    }

    LaunchedEffect(selectedTab) {
        selectedCategory = ""
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Charcoal) {

        ConstraintLayout(modifier = Modifier.fillMaxSize()) {

            val (topbar, content) = createRefs()

            topHeaders(
                selectedTab = selectedTab,
                onTabChange = {
                    selectedTab = it
                    selectedCategory = ""
                },
                onCancel = onCancel,
                onSave = {
                    val amount = amountText.toIntOrNull() ?: 0

                    // Check if wallet balance is 0 for expense and transfer
                    if (selectedTab != "Income") {
                        val wallet = WalletStore.wallets.firstOrNull { it.name == selectedWallet }
                        if (wallet != null && wallet.balance < amount) {
                            // Show error or prevent save
                            return@topHeaders
                        }
                    }

                    val type = when (selectedTab) {
                        "Income" -> "Income"
                        "Transfer" -> "Savings"
                        else -> "Debited"
                    }

                    val newTransaction = Transaction(
                        amount = amount,
                        type = type,
                        category = selectedCategory,
                        date = "27/04/26",
                        description = description,
                        imageUri = selectedImageUri
                    )

                    // Update wallet balance
                    updateWalletBalance(selectedWallet, amount, selectedTab)

                    onSave(newTransaction)
                    selectedImageUri = null
                },
                modifier = Modifier.constrainAs(topbar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(content) {
                        top.linkTo(topbar.bottom)
                    }
            ) {

                Spacer(Modifier.height(8.dp))

                //drop downs
                if (selectedTab == "Income") {

                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        FieldBoxWithPlus("WALLET", selectedWallet) {
                            showWalletPicker = true
                        }

                        FieldBoxWithPlus("CATEGORY", selectedCategory) {
                            categoryOptions = listOf("Salary", "Savings", "Birthday", "Freelancing")
                            showCategoryPicker = true
                        }
                    }

                } else {

                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        FieldBoxWithPlus("ACCOUNT", selectedWallet) {
                            showWalletPicker = true
                        }

                        FieldBoxWithPlus("CATEGORY", selectedCategory) {
                            categoryOptions = listOf("Social", "Food", "Transport", "Shopping", "Bills")
                            showCategoryPicker = true
                        }

                        FieldBoxWithPlus("IMAGE", "ADD") {
                            imagePicker.launch("image/*")
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                DescriptionBox(
                    text = description,
                    onChange = { description = it }
                )

                Spacer(Modifier.height(8.dp))

                AmountBox(
                    amountText = amountText,
                    onBackspace = {
                        amountText = amountText.dropLast(1)
                        if (amountText.isEmpty()) amountText = "0"
                    }
                )

                Spacer(Modifier.height(12.dp))

                CalculatorPad { key ->
                    if (key == "=") return@CalculatorPad
                    if (amountText == "0") amountText = ""
                    amountText += key
                }
            }
        }

        if (showCategoryPicker) {
            CategoryPickerDialog(
                options = categoryOptions,
                onSelect = {
                    selectedCategory = it
                    showCategoryPicker = false
                },
                onAddNew = {
                    selectedCategory = it
                    showCategoryPicker = false
                },
                onDismiss = { showCategoryPicker = false }
            )
        }

        if (showWalletPicker) {
            WalletPickerDialog(
                onSelect = { walletName ->
                    selectedWallet = walletName
                    showWalletPicker = false
                },
                onDismiss = { showWalletPicker = false }
            )
        }
    }
}

// Helper function to update wallet balance
fun updateWalletBalance(walletName: String, amount: Int, tabType: String) {
    val walletIndex = WalletStore.wallets.indexOfFirst { it.name == walletName }
    if (walletIndex != -1) {
        val currentWallet = WalletStore.wallets[walletIndex]
        val newBalance = when (tabType) {
            "Income" -> currentWallet.balance + amount
            "Expense" -> currentWallet.balance - amount
            "Transfer" -> currentWallet.balance - amount
            else -> currentWallet.balance
        }

        val updatedWallet = currentWallet.copy(balance = newBalance)
        WalletStore.wallets[walletIndex] = updatedWallet
    }
}

@Composable
fun topHeaders(
    selectedTab: String,
    onTabChange: (String) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,  // Keep as simple lambda
    modifier: Modifier = Modifier
) {

    Box(modifier = modifier.fillMaxWidth()) {

        Image(
            painter = painterResource(id = R.drawable.topbaracid),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )

        //cancel and save buttons
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 48.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Box(
                    modifier = Modifier
                        .border(4.dp, Color.Black, RoundedCornerShape(6.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .clickable { onCancel() }
                ) {
                    Text("CANCEL", color = DeepRed, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .border(4.dp, Color.Black, RoundedCornerShape(6.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .clickable { onSave() }
                ) {
                    Text("SAVE", color = DarkGreen, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                }
            }

            //income, expense, transfer headers
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Income", "Expense", "Transfer").forEach { tab ->
                    Text(
                        text = tab,
                        fontSize = 22.sp,
                        color = if (selectedTab == tab) Charcoal else Charcoal.copy(alpha = 0.5f),
                        fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.clickable { onTabChange(tab) }
                    )
                }
            }
        }
    }
}

//the dropdown menus
@Composable
fun FieldBoxWithPlus(label: String, value: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(label, color = Color.White, fontSize = 15.sp)

        Box(
            modifier = Modifier
                .width(110.dp)
                .height(60.dp)
                .border(4.dp, Acid, RoundedCornerShape(12.dp))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = if (value.isBlank()) "+" else value,
                color = Color.White,
                fontSize = 18.sp
            )
        }
    }
}

//transaction description
@Composable
fun DescriptionBox(
    text: String,
    onChange: (String) -> Unit
) {

    BasicTextField(
        value = text,
        onValueChange = onChange,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(120.dp)
            .background(Color(0xFF2A2A2A), RoundedCornerShape(12.dp))
            .border(4.dp, Acid, RoundedCornerShape(12.dp))
            .padding(12.dp)
            .focusable(),
        textStyle = TextStyle(
            color = Color.White,
            fontSize = 18.sp
        ),
        cursorBrush = SolidColor(Color.White)
    )
}

//amount spent or gained
@Composable
fun AmountBox(amountText: String, onBackspace: () -> Unit) {
    Box(
        modifier = Modifier.padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(72.dp)
            .background(Color.White, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                amountText,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
            Box(
                modifier = Modifier.clickable { onBackspace() }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_backspace_24),
                    contentDescription = null
                )
            }
        }
    }
}

//working calculator
@Composable
fun CalculatorPad(onInput: (String) -> Unit) {

    val buttons = listOf(
        listOf("+", "7", "8", "9"),
        listOf("-", "4", "5", "6"),
        listOf("X", "1", "2", "3"),
        listOf("/", "0", ".", "=")
    )

    val operators = setOf("+", "-", "X", "/", "=")

    Column(Modifier.padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        buttons.forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { label ->
                    val isOperator = label in operators
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(
                                color = if (isOperator) Acid else Charcoal,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(2.dp, Acid, RoundedCornerShape(12.dp))
                            .clickable { onInput(label) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, fontSize = 40.sp, color = if (isOperator) Color.Black else Color.White,
                            fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

//pick from current wallets
@Composable
fun WalletPickerDialog(
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Wallet") },
        text = {
            Column {
                WalletStore.wallets.forEach { wallet ->
                    Text(
                        text = "${wallet.name} (R${wallet.balance})",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(wallet.name) }
                            .padding(12.dp),
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.height(4.dp))
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
//pick from categories
@Composable
fun CategoryPickerDialog(
    options: List<String>,
    onSelect: (String) -> Unit,
    onAddNew: (String) -> Unit,
    onDismiss: () -> Unit
) {

    //add category
    var newCategory by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Category") },
        text = {

            Column {

                options.forEach {
                    Text(
                        text = it,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(it) }
                            .padding(10.dp)
                    )
                }

                Spacer(Modifier.height(10.dp))

                //user added category
                BasicTextField(
                    value = newCategory,
                    onValueChange = { newCategory = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray)
                        .padding(8.dp)
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Add New",
                    modifier = Modifier
                        .clickable {
                            if (newCategory.isNotBlank()) {
                                onAddNew(newCategory)
                            }
                        }
                        .padding(8.dp)
                )
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}

//auto add date
fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
    return dateFormat.format(Date())
}

@Preview(showBackground = true)
@Composable
fun previewExpenseScreen() {
    AddExpenseScreen(onCancel = {}, onSave = {})
}
