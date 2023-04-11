package aelsi2.natkschedule

import aelsi2.natkschedule.ui.ScheduleApp
import aelsi2.natkschedule.ui.theme.ScheduleTheme
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ScheduleTheme {
                ScheduleApp()
            }
        }
    }
}