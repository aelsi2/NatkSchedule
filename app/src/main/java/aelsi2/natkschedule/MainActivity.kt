package aelsi2.natkschedule

import aelsi2.natkschedule.ui.ScheduleApp
import aelsi2.natkschedule.ui.theme.ScheduleTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ScheduleTheme {
                ScheduleApp()
            }
        }
    }
}