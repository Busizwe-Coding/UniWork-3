package com.example.budgitzpoe

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.budgitzpoe.ui.theme.BudgitzPOETheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BudgitzPOETheme {

                var screen by remember { mutableStateOf("home") }
                var menuOpen by remember { mutableStateOf(false) }

                Box(modifier = Modifier.fillMaxSize()) {

                    when (screen) {

                        "home" -> homescreen(
                            onAddTransaction = { screen = "add" },
                            onWallets = { screen = "wallet" },
                            onOverviews = { screen = "overview" },
                            onExport = { screen = "export" },
                            onMenuClick = { menuOpen = true }
                        )

                        "add" -> AddExpenseScreen(
                            onCancel = { screen = "home" },
                            onSave = {
                                TransactionStore.transactions.add(it)
                                screen = "home"
                            }
                        )
                        "wallet" -> WalletScreen(
                            onRecords = { screen = "home" },
                            onWallets = { screen = "wallet" },
                            onOverviews = { screen = "overview" },
                            onExport = { screen = "export" },
                            onMenuClick = { menuOpen = true }
                        )

                        "overview" -> OverviewsScreen(
                            onRecords = { screen = "home" },
                            onWallets = { screen = "wallet" },
                            onOverviews = { screen = "overview" },
                            onExport = { screen = "export" },
                            onMenuClick = { menuOpen = true }
                        )

                        "export" -> ExportScreen(
                            onMenuClick = { menuOpen = true },
                            onOverviews = { screen = "overview" },
                            onRecords = { screen = "home" },
                            onWallets = { screen = "wallet" }
                        )
                    }

                    menuDrawer(
                        isOpen = menuOpen,
                        onClose = { menuOpen = false }
                    )
                }
            }
        }
    }
}
