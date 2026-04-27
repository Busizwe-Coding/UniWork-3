package com.example.budgitzpoe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun WalletScreen() {

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
                }
            )

            Column( //for each wallet type
                modifier = Modifier
                    .padding(top = 220.dp, start = 20.dp, end = 20.dp)
                    .constrainAs(content) { top.linkTo(parent.top) },
                verticalArrangement = Arrangement.spacedBy(25.dp)
            ) {

                WalletCard("MAIN: R500", Color.White)
                WalletCard("SAVINGS: R30000", Color(0xFF39FF14))
                WalletCard("EMERGENCY: R10000", Color.Red)

                Spacer(Modifier.height(20.dp))

                AddButton()
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
                    Image(painterResource(R.drawable.recordsicon), null)
                    Image(painterResource(R.drawable.walletsicon), null)
                    Image(painterResource(R.drawable.overviewicon), null)
                    Image(painterResource(R.drawable.exporticon), null)
                }
            }
        }
    }
}


@Composable
fun TopHeader(modifier: Modifier) {

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
fun AddButton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .border(3.dp, Color.Black, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text("+ ADD", fontSize = 26.sp, fontWeight = FontWeight.Bold)
    }
}
@Composable
@Preview (showBackground = true)
fun previewWalletscreen (){
    WalletScreen()
}