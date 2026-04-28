package com.example.budgitzpoe

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import com.example.budgitzpoe.ui.theme.Acid

@Composable
fun menuDrawer(
    isOpen: Boolean,
    onClose: () -> Unit,
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

                MenuItem("Budgets")
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

@Composable
fun MenuItem(text: String, onClick: () -> Unit = {}) {
    Text(
        text = text,
        color = Color.White,
        fontSize = 20.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp)
    )
}

@Composable
@Preview(showBackground = true)
fun previewMenuClosed() {
    menuDrawer(
        isOpen = false,
        onClose = {},
        onLogout = {}
    )
}