package com.main.unicdaevento

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.database.AppDb
import com.database.SeedData
import com.database.instanceAppDbInMemory
import com.main.unicdaevento.MainViewModel
import com.route.AppNavHost
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    lateinit var db: AppDb
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        splashScreen.setKeepOnScreenCondition {
            mainViewModel.isLoading.value
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        db = instanceAppDbInMemory(applicationContext)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                SeedData.seed(db)
            }
            mainViewModel.stopLoading()
        }

        setContent {
            val navController = rememberNavController()

            MyAppTheme {
                Scaffold { outerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(outerPadding)
                            .consumeWindowInsets(outerPadding)
                    ) {
                        AppNavHost(navController, db)
                    }
                }
            }
        }
    }
}

