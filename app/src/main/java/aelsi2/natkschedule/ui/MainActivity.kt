package aelsi2.natkschedule.ui

import aelsi2.natkschedule.ui.theme.ScheduleTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScheduleTheme {
                ScheduleApp()
            }
        }
    }
}