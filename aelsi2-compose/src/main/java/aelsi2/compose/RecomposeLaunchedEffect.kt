package aelsi2.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope

@Composable
fun RecomposeLaunchedEffect(vararg keys: Any?, block: suspend CoroutineScope.() -> Unit) {
    var isUpdate by remember { mutableStateOf(false) }
    LaunchedEffect(keys) {
        if (isUpdate) {
            this.block()
        }
        else {
            isUpdate = true
        }
    }
}

@Composable
fun RecomposeLaunchedEffect(key: Any?, block: suspend CoroutineScope.() -> Unit) {
    var isUpdate by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = key) {
        if (isUpdate) {
            this.block()
        }
        else {
            isUpdate = true
        }
    }
}