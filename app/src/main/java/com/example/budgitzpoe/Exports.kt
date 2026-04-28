package com.example.budgitzpoe

import androidx.compose.animation.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.budgitzpoe.ui.theme.Acid

@Composable
fun ExportScreen(
    onMenuClick: () -> Unit,
    onRecords: () -> Unit,
    onOverviews: () -> Unit,
    onWallets: () -> Unit
) {

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

            //income and expenses again
            topHeader(
                modifier = Modifier.constrainAs(nameRow) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                onMenuClick = onMenuClick  // Pass the function
            )

            //export button
            Column(
                modifier = Modifier
                    .width(350.dp)
                    .constrainAs(content) {
                        top.linkTo(topbar.bottom)
                        bottom.linkTo(bottombox.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                ExportOption("Export as .csv")
                ExportOption("Export as .xlsx")

                Spacer(Modifier.height(40.dp))

                Button(
                    onClick = { },
                    modifier = Modifier.width(200.dp).height(60.dp)
                        .padding(top = 10.dp)
                        .align(alignment = Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(Color.Black)
                ) {
                    Text("EXPORT", fontSize = 20.sp)
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
                        modifier = Modifier.clickable { onRecords() }  // Goes to homescreen
                    )
                    Image(
                        painter = painterResource(R.drawable.walletsicon),
                        contentDescription = "Wallets",
                        modifier = Modifier.clickable { onWallets() }  // Goes to walletscreen
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
    onMenuClick: () -> Unit  // Added this parameter
) {

    //for menu icon, income and expense
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 75.dp, start = 16.dp, end = 16.dp)

    ) {

        Image(
            painter = painterResource(id = R.drawable.menuicon),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clickable { onMenuClick() }  // Now this works
        )

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

    }
}

//export file types
@Composable
fun ExportOption(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp)
            .height(80.dp)
            .background(Color.Black, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White, fontSize = 22.sp)
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