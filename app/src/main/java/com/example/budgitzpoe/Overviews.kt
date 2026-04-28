package com.example.budgitzpoe

import android.os.Build
import android.view.Surface
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import kotlinx.coroutines.NonDisposableHandle.parent
import com.example.budgitzpoe.ui.theme.Acid
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OverviewsScreen(
    onRecords: () -> Unit,
    onWallets: () -> Unit,
    onOverviews: () -> Unit,
    onExport: () -> Unit,
    onMenuClick: () -> Unit
) {

    var showMonths by remember { mutableStateOf(false) }

    val currentMonth = LocalDate.now()
        .month
        .getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        .uppercase()

    var selectedMonth by remember { mutableStateOf(currentMonth) }

    // categories in month
    var selectedTab by remember { mutableStateOf(1) }

    val categories = listOf(
        "SOCIAL" to "R0",
        "FOOD" to "R0",
        "TRANSPORT" to "R0",
        "SHOPPING" to "R0",
        "BILLS" to "R0"
    )

    Surface(modifier = Modifier.fillMaxSize(), color = Acid) {

        Box(modifier = Modifier.fillMaxSize()) {

            ConstraintLayout(modifier = Modifier.fillMaxSize()) {

                val (nameRow, topbar, bottombox, content, monthButton) = createRefs()

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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 75.dp, start = 16.dp, end = 16.dp)
                        .constrainAs(nameRow) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.menuicon),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clickable { onMenuClick() }
                    )
                }

                //month button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black)
                        .clickable { showMonths = true }
                        .padding(horizontal = 30.dp, vertical = 10.dp)
                        .constrainAs(monthButton) {
                            top.linkTo(topbar.top)
                            bottom.linkTo(topbar.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                ) {
                    Text(selectedMonth, color = Color.White, fontSize = 18.sp)
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
                            painter = painterResource(id = R.drawable.recordsicon),
                            contentDescription = null,
                            modifier = Modifier.clickable { onRecords() }
                        )

                        Image(
                            painter = painterResource(id = R.drawable.walletsicon),
                            contentDescription = null,
                            modifier = Modifier.clickable { onWallets() }
                        )

                        Image(
                            painter = painterResource(R.drawable.overviewicon),
                            contentDescription = null,
                            modifier = Modifier.clickable { onOverviews() }
                        )

                        Image(
                            painter = painterResource(id = R.drawable.exporticon),
                            contentDescription = null,
                            modifier = Modifier.clickable { onExport() }
                        )
                    }
                }
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

//month picker in header
@Composable
fun MonthPickerDialog(
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {

    val months = listOf(
        "JANUARY","FEBRUARY","MARCH","APRIL","MAY","JUNE",
        "JULY","AUGUST","SEPTEMBER","OCTOBER","NOVEMBER","DECEMBER"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {

        Column (
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(20.dp)
        ) {

            months.forEach { month ->
                Text(
                    text = month,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable() { onSelect(month) }
                )
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Preview(showBackground = true)
fun previewOverviewScreen() {
    OverviewsScreen(
        onWallets = {},
        onRecords = {},
        onOverviews = {},
        onExport = {},
        onMenuClick = {}
    )
}
