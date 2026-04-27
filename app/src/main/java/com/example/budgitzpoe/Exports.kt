package com.example.budgitzpoe

import androidx.compose.animation.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
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
fun ExportScreen() {

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
                }
            )

            //export button
            Column(
                modifier = Modifier
                    .padding(top = 240.dp, start = 30.dp, end = 30.dp)
                    .constrainAs(content) { top.linkTo(parent.top) },
                verticalArrangement = Arrangement.spacedBy(30.dp)
            ) {

                ExportOption("Export as .csv")
                ExportOption("Export as .xlsx")

                Spacer(Modifier.height(40.dp))

                Button(
                    onClick = { },
                    modifier = Modifier.width(200.dp).height(60.dp)
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
fun topHeader(modifier: Modifier) {

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

//export file types
@Composable
fun ExportOption(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp)
            .height(80.dp)
            .background(Color.Black, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White, fontSize = 22.sp)
    }
}

@Composable
@Preview (showBackground = true)
fun previewExportscreen (){
    ExportScreen()
}