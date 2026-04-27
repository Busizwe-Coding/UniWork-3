package com.example.budgitzpoe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.budgitzpoe.ui.theme.Acid

@Composable
fun WalletScreen(
    onRecords: () -> Unit,
    onWallets: () -> Unit,
    onExport: () -> Unit,
    onMenuClick: () -> Unit
) {

    var selectedWallet by remember { mutableStateOf<Wallet?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Acid
    ) {
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

            TopHeader(
                modifier = Modifier.constrainAs(nameRow) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                onMenuClick = onMenuClick
            )

            Column( //for each wallet type
                modifier = Modifier
                    .padding(top = 220.dp, start = 20.dp, end = 20.dp)
                    .constrainAs(content) { top.linkTo(parent.top) },
                verticalArrangement = Arrangement.spacedBy(25.dp)
            ) {

                WalletStore.wallets.forEach { wallet ->
                    Box(
                        modifier = Modifier.clickable {
                            selectedWallet = wallet
                        }
                    ) {
                        WalletCard(
                            text = "${wallet.name}: R${wallet.balance}",
                            color = wallet.color
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                AddButton {
                    WalletStore.addWallet(it)
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

                //buttons at the bottom
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Image(
                        painterResource(R.drawable.recordsicon),
                        null,
                        modifier = Modifier.clickable { onRecords() }
                    )
                    Image(painterResource(R.drawable.walletsicon), null)
                    Image(painterResource(R.drawable.overviewicon), null)
                    Image(
                        painterResource(R.drawable.exporticon),
                        null,
                        modifier = Modifier.clickable { onExport() }
                    )
                }
            }
        }
        selectedWallet?.let {
            WalletPopup(
                wallet = it,
                onDismiss = { selectedWallet = null }
            )
        }
    }
}


@Composable
fun TopHeader(
    modifier: Modifier,
    onMenuClick: () -> Unit
) {

    //for income and expense
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 75.dp, start = 16.dp, end = 16.dp)

    ) {

        Column {

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Expense", fontSize = 22.sp, color = Color.Red)
                    Text("-1850", fontSize = 22.sp, color = Color.Red)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Income", fontSize = 22.sp, color = Color.Green)
                    Text("+4300", fontSize = 22.sp, color = Color.Green)
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
}

//each wallet type decoration
@Composable
fun WalletCard(text: String, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .border(3.dp, Color.Black, RoundedCornerShape(20.dp))
            .background(color, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}

//add new wallet
@Composable
fun AddButton(onAdd: (Wallet) -> Unit) {

    var showDialog by remember { mutableStateOf(false) }
    var name by rememberSaveable { mutableStateOf("") }
    var color by remember { mutableStateOf(Color.White) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Text(
                    "CREATE",
                    modifier = Modifier.clickable {
                        onAdd(
                            Wallet(
                                name = name,
                                balance = 0,
                                minSpend = 0,
                                maxSpend = 0,
                                color = color
                            )
                        )
                        showDialog = false
                    }
                )
            },
            text = {
                Column {
                    Text("Name:")  // Added label
                    Spacer(Modifier.height(4.dp))
                    BasicTextField(value = name, onValueChange = { name = it })
                    Spacer(Modifier.height(8.dp))
                    Text("Pick Color (simple)")
                    Row {
                        listOf(Color.White, Color.Red, Color.Green).forEach {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(it)
                                    .clickable { color = it }
                            )
                            Spacer(Modifier.width(8.dp))
                        }
                    }
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .border(3.dp, Color.Black, RoundedCornerShape(20.dp))
            .clickable { showDialog = true },
        contentAlignment = Alignment.Center
    ) {
        Text("+ ADD", fontSize = 26.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun previewWalletscreen() {
    homescreen(
        onAddTransaction = {},
        onWallets = {},
        onExport = {},
        onMenuClick = {}
    )
}