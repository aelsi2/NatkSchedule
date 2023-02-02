package aelsi2.natkschedule

import aelsi2.natkschedule.data.database.databaseModule
import aelsi2.natkschedule.ui.ScheduleApp
import aelsi2.natkschedule.ui.viewModelsModule
import aelsi2.natkschedule.ui.theme.ScheduleTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        startKoin {
            modules(databaseModule)
            modules(viewModelsModule)
            androidContext(this@MainActivity)
        }
        setContent {
            ScheduleTheme {
                ScheduleApp()
            }
        }
    }
}