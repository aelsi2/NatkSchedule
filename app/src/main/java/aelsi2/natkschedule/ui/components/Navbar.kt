package aelsi2.natkschedule.ui.components

import aelsi2.natkschedule.ui.TopLevelDestinations
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import com.aelsi2.natkschedule.R

@Composable
fun Navbar() {

}

enum class Tabs(val route : String, val title : Int, val iconNormal : Int, val iconSelected : Int) {
    HOME(TopLevelDestinations.HOME_ROUTE, R.string.home_tab_name, R.drawable.home_outlined, R.drawable.home_filled)
}