package com.example.budgitzpoe

import androidx.compose.animation.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.budgitzpoe.ui.theme.Acid
import com.example.budgitzpoe.ui.theme.Charcoal
import com.example.budgitzpoe.ui.theme.DarkGreen
import com.example.budgitzpoe.ui.theme.DeepRed

@Composable
fun AddExpenseScreen() {

    Surface(modifier = Modifier.fillMaxSize(), color = Charcoal) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {

            val (topbar, content) = createRefs()

            topHeaders(
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

                // Account, Category side by side
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FieldBoxWithPlus("ACCOUNT", Modifier.weight(1f).padding(end = 6.dp))
                    FieldBoxWithPlus("CATEGORY", Modifier.weight(1f).padding(start = 6.dp))
                }

                Spacer(Modifier.height(8.dp))

                // DESCRIPTION full width
                DescriptionBox()

                Spacer(Modifier.height(8.dp))

                // Amount display with backspace
                AmountBox()

                Spacer(Modifier.height(12.dp))

                CalculatorPad()
            }
        }
    }
}

@Composable
fun topHeaders(modifier: Modifier = Modifier) {

    var selectedTab by remember { mutableStateOf("Expense") }

    Box(modifier = modifier.fillMaxWidth()) {

        Image(
            painter = painterResource(id = R.drawable.topbaracid),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp)
        ) {

            // CANCEL / SAVE row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .border(4.dp, Color.Black, RoundedCornerShape(6.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        "CANCEL",
                        color = DeepRed,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .border(4.dp, Color.Black, RoundedCornerShape(6.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        "SAVE",
                        color = DarkGreen,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Income / Expense / Transfer tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Income", "Expense", "Transfer").forEach { tab ->
                    Text(
                        text = tab,
                        fontSize = 22.sp,
                        color = if (selectedTab == tab) Charcoal else Charcoal.copy(alpha = 0.5f),
                        fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.clickable() { selectedTab = tab }
                    )
                }
            }
        }
    }
}

@Composable
fun FieldBoxWithPlus(label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            label,
            color = Color.White,
            fontSize = 15.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Box(
            modifier = Modifier
                .width(170.dp)
                .height(60.dp)
                .border(4.dp, Acid, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_add_24),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun DescriptionBox() {
    Column(Modifier.padding(horizontal = 16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .border(4.dp, Acid, RoundedCornerShape(12.dp))
                .background(Color(0xFF2A2A2A), RoundedCornerShape(12.dp))
                .padding(13.dp)
        ) {
            Text("DESCRIPTION", color = Color.Gray, fontSize = 25.sp)
        }
    }
}

@Composable
fun AmountBox() {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(72.dp)
            .background(Color.White, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "100",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_backspace_24),
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
fun CalculatorPad() {

    val buttons = listOf(
        listOf("+", "7", "8", "9"),
        listOf("-", "4", "5", "6"),
        listOf("X", "1", "2", "3"),
        listOf("/", "0", ".", "=")
    )

    val operatorKeys = setOf("+", "-", "X", "/", "=")

    Column(
        modifier = Modifier.padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        buttons.forEach { row ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { label ->
                    val isOperator = label in operatorKeys
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .then(
                                if (isOperator)
                                    Modifier.background(Acid, RoundedCornerShape(12.dp))
                                else
                                    Modifier
                                        .background(Charcoal, RoundedCornerShape(12.dp))
                                        .border(2.dp, Acid, RoundedCornerShape(12.dp))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            label,
                            fontSize = 40.sp,
                            color = if (isOperator) Charcoal else Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun previewExpenseScreen() {
    AddExpenseScreen()
}
