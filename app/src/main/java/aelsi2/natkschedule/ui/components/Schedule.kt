package aelsi2.natkschedule.ui.components

import aelsi2.natkschedule.ui.theme.ScheduleTheme
import aelsi2.natkschedule.ui.theme.Shapes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Lecture(
    title : String,
    time : String,
    info : String,
    modifier : Modifier = Modifier,
    spacing : Dp = 5.dp,
    state : String = "",
    stateTime : String = "",
    stateColor : Color = MaterialTheme.colorScheme.primary
) {
    Surface(
        shape = Shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            val columnChildModifier = Modifier.fillMaxWidth()
            Text(
                text = title,
                modifier = columnChildModifier,
                style = MaterialTheme.typography.titleSmall)
            Spacer(
                modifier = columnChildModifier.height(spacing)
            )
            Row(
                modifier = columnChildModifier,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(time, style = MaterialTheme.typography.bodySmall)
                    Text(info, style = MaterialTheme.typography.bodySmall)
                }
                if (state.isNotEmpty() && stateTime.isNotEmpty()){
                    Spacer(modifier = Modifier.width(spacing))
                }
                Column(
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        state,
                        color = stateColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (stateTime.isNotEmpty()){
                        Text(
                            stateTime,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun LecturePreview() {
    ScheduleTheme(darkTheme = false) {
        Lecture(
            title = "МДК.01.03 Разработка мобильных приложений",
            time = "16:20 – 18:00",
            info = "№366 • Климова И. С.",
            state = "Идет",
            stateTime = "До перерыва: 40:31",
            modifier = Modifier.width(340.dp)
        )
    }
}