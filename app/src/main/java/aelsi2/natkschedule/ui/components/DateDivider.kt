package aelsi2.natkschedule.ui.components

import aelsi2.natkschedule.ui.theme.ScheduleTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DateDivider(
    dayOfWeekText: String,
    dateText : String,
    modifier: Modifier = Modifier
){
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.padding(horizontal = 10.dp)
    ) {
        Divider(thickness = 2.dp, color = MaterialTheme.colorScheme.primary)
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                dayOfWeekText,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge)
            Text(
                dateText,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Preview
@Composable
fun DateDividerPreview() {
    ScheduleTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            DateDivider(
                dayOfWeekText = "Вторник",
                dateText = "07.03.2023",
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}