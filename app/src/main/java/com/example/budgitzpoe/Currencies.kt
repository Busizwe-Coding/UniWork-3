package com.example.budgitzpoe

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.budgitzpoe.ui.theme.Acid

// 1. GLOBAL CURRENCY STATE TRACKER
object CurrencyStore {
    // Default symbol isRands "R"
    var selectedCurrency by mutableStateOf("R")

    // List of currencies
    val availableCurrencies = listOf(
        CurrencyItem("R", "South African Rand (R)"),
        CurrencyItem("$", "US Dollar ($)"),
        CurrencyItem("€", "Euro (€)"),
        CurrencyItem("£", "British Pound (£)"),
        CurrencyItem("¥", "Japanese Yen (¥)"),
        CurrencyItem("₩", "Korean Won (₩)")
    )
}

data class CurrencyItem(val symbol: String, val displayName: String)

// 2. CURRENCIES CONFIGURATION SCREEN
@Composable
fun CurrenciesScreen(
    onMenuClick: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), color = Acid) {
        Box(modifier = Modifier.fillMaxSize()) {
            ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                val (nameRow, topbar, bottombox, listArea) = createRefs()

                Image(
                    painter = painterResource(id = R.drawable.topbar),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(topbar) { top.linkTo(parent.top) }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 55.dp, start = 16.dp, end = 16.dp)
                        .constrainAs(nameRow) { top.linkTo(parent.top) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 45.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("CURRENCY", fontSize = 35.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.menuicon),
                            contentDescription = null,
                            modifier = Modifier.clickable { onMenuClick() }
                        )
                    }
                }

                // Scrollable List of Currencies
                LazyColumn(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp, bottom = 16.dp)
                        .constrainAs(listArea) {
                            top.linkTo(nameRow.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(bottombox.top)
                            height = androidx.constraintlayout.compose.Dimension.fillToConstraints
                        },
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(CurrencyStore.availableCurrencies) { currency ->
                        val isSelected = CurrencyStore.selectedCurrency == currency.symbol

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(65.dp)
                                .border(3.dp, Color.Black, RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) Color.Black else Color.White,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { CurrencyStore.selectedCurrency = currency.symbol },
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = currency.displayName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color.Black
                                )

                                // Large preview of the symbol on the right
                                Text(
                                    text = currency.symbol,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isSelected) Acid else Color.Gray
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(bottombox) { bottom.linkTo(parent.bottom) }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bottombar),
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCurrenciesScreen() {
    CurrenciesScreen(onMenuClick = {})
}
