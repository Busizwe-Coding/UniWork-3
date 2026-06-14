package com.example.budgitzpoe

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import java.io.OutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import com.example.budgitzpoe.ui.theme.Acid

@Composable
fun ExportScreen(
    onMenuClick: () -> Unit,
    onRecords: () -> Unit,
    onOverviews: () -> Unit,
    onWallets: () -> Unit
) {
    val context = LocalContext.current

    // Track selected export type: "CSV" or "XLSX"
    var selectedType by remember { mutableStateOf("CSV") }

    // Lazy initialization of document saver
    val fileSaverLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("*/*")
    ) { uri: Uri? ->
        if (uri != null) {
            writeTransactionDataToFile(context, uri, selectedType)
        } else {
            Toast.makeText(context, "Export cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Acid) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (nameRow, topbar, bottombox, content) = createRefs()

            Image(
                painter = painterResource(id = R.drawable.topbar),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth().constrainAs(topbar) {
                    top.linkTo(parent.top)
                }
            )

            topHeader(
                modifier = Modifier.constrainAs(nameRow) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                onMenuClick = onMenuClick
            )

            Column(
                modifier = Modifier
                    .width(350.dp)
                    .constrainAs(content) {
                        top.linkTo(topbar.bottom)
                        bottom.linkTo(bottombox.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        height = androidx.constraintlayout.compose.Dimension.fillToConstraints
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Clicking option updates selection state tracking variables
                ExportOption(
                    text = "Export as .csv",
                    isSelected = selectedType == "CSV",
                    onClick = { selectedType = "CSV" }
                )

                ExportOption(
                    text = "Export as .xlsx",
                    isSelected = selectedType == "XLSX",
                    onClick = { selectedType = "XLSX" }
                )

                Spacer(Modifier.height(40.dp))

                Button(
                    onClick = {
                        val currentMonthName = LocalDate.now().month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).lowercase()
                        val extension = if (selectedType == "CSV") "csv" else "xlsx"
                        val fileName = "transactions_$currentMonthName.$extension"

                        // Triggers the system file manager prompt
                        fileSaverLauncher.launch(fileName)
                    },
                    modifier = Modifier
                        .width(200.dp)
                        .height(60.dp)
                        .padding(top = 10.dp)
                        .align(alignment = Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(Color.Black)
                ) {
                    Text("EXPORT", fontSize = 20.sp, color = Color.White)
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Image(
                        painter = painterResource(R.drawable.recordsicon),
                        contentDescription = "Records",
                        modifier = Modifier.clickable { onRecords() }
                    )
                    Image(
                        painter = painterResource(R.drawable.walletsicon),
                        contentDescription = "Wallets",
                        modifier = Modifier.clickable { onWallets() }
                    )
                    Image(
                        painter = painterResource(R.drawable.overviewicon),
                        contentDescription = null,
                        modifier = Modifier.clickable { onOverviews() }
                    )
                    Image(painterResource(R.drawable.exporticon), null)
                }
            }
        }
    }
}

@Composable
fun topHeader(
    modifier: Modifier,
    onMenuClick: () -> Unit
) {
    // 1. Get current month string name
    val currentMonth = LocalDate.now()
        .month
        .getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        .uppercase()

    // 2. Filter transactions to only count the current month
    val filteredTransactions = TransactionStore.transactions.filter { transaction ->
        runCatching {
            val date = LocalDate.parse(transaction.date, DateTimeFormatter.ofPattern("dd/MM/yy"))
            date.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH).uppercase() == currentMonth
        }.getOrDefault(false)
    }

    // 3. Calculate totals based ONLY on this month's filtered data
    val totalIncome = filteredTransactions.filter { it.type.equals("Income", true) || it.type.equals("Credited", true) }.sumOf { it.amount }
    val totalExpense = filteredTransactions.filter { it.type.equals("Debited", true) }.sumOf { it.amount }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 75.dp, start = 16.dp, end = 16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.menuicon),
            contentDescription = null,
            modifier = Modifier.align(Alignment.TopEnd).clickable { onMenuClick() }
        )
        Column {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Expense", fontSize = 22.sp, color = Color.Red)
                    Text("-${totalExpense}", fontSize = 22.sp, color = Color.Red)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Income", fontSize = 22.sp, color = Color.Green)
                    Text("+${totalIncome}", fontSize = 22.sp, color = Color.Green)
                }
            }
        }
    }
}

// REFACTORED SELECTION EXPORT CARDS
@Composable
fun ExportOption(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
            .height(70.dp)
            .border(3.dp, Color.Black, RoundedCornerShape(20.dp))
            .background(
                if (isSelected) Color.Black else Color.White,
                RoundedCornerShape(20.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color.Black,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// FILE WRITING ENGINE DATA CONTROLLER
private fun writeTransactionDataToFile(context: Context, uri: Uri, type: String) {
    val currentMonth = LocalDate.now().month.getDisplayName(TextStyle.FULL, Locale.ENGLISH).uppercase()
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")

    // Filter down to the active month's accounting transactions
    val filteredTransactions = TransactionStore.transactions.filter { transaction ->
        runCatching {
            val date = LocalDate.parse(transaction.date, formatter)
            date.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH).uppercase() == currentMonth
        }.getOrDefault(false)
    }

    // Generate column split separation format based on type selection
    val separator = if (type == "CSV") "," else "\t"

    val stringBuilder = StringBuilder()
    // Append headers row
    stringBuilder.append("Date${separator}Category${separator}Description${separator}Type${separator}Amount\n")

    // Populate lines sequentially
    for (t in filteredTransactions) {
        stringBuilder.append("${t.date}${separator}${t.category}${separator}${t.description}${separator}${t.type}${separator}${t.amount}\n")
    }

    try {
        val outputStream: OutputStream? = context.contentResolver.openOutputStream(uri)
        outputStream?.use { stream ->
            stream.write(stringBuilder.toString().toByteArray())
            stream.flush()
        }
        Toast.makeText(context, "Export successful!", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to save: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

@Composable
@Preview(showBackground = true)
fun previewExportScreen() {
    ExportScreen(
        onMenuClick = { },
        onRecords = { },
        onOverviews = {},
        onWallets = { }
    )
}