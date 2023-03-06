package aelsi2.natkschedule.ui.components

import aelsi2.natkschedule.ui.theme.ScheduleTheme
import aelsi2.natkschedule.ui.theme.Shapes
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


interface LectureCardStyle {
    val backgroundColor: Color
        @Composable get
    val stateInfoColor: Color
        @Composable get

    object Inactive : LectureCardStyle {
        override val backgroundColor: Color
            @Composable
            get() = Color.Transparent
        override val stateInfoColor: Color
            @Composable
            get() = MaterialTheme.colorScheme.onBackground
    }

    object Active : LectureCardStyle {
        override val backgroundColor: Color
            @Composable
            get() = MaterialTheme.colorScheme.secondaryContainer
        override val stateInfoColor: Color
            @Composable
            get() = MaterialTheme.colorScheme.onBackground
    }

    object Highlighted : LectureCardStyle {
        override val backgroundColor: Color
            @Composable
            get() {
                val infiniteTransition = rememberInfiniteTransition()
                val alpha = infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(750, easing = EaseIn),
                        repeatMode = RepeatMode.Reverse
                    )
                ).value
                return MaterialTheme.colorScheme.secondaryContainer.applyAlpha(alpha)
            }
        override val stateInfoColor: Color
            @Composable
            get() = MaterialTheme.colorScheme.primary

        private fun Color.applyAlpha(alpha: Float): Color {
            val resultAlpha = when (this.alpha * alpha) {
                in Float.MIN_VALUE..0f -> 0f
                in 1f..Float.MAX_VALUE -> 1f
                else -> this.alpha * alpha
            }
            return Color(red, green, blue, resultAlpha)
        }
    }
}

@Composable
fun LectureCard(
    titleText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    infoText: String? = null,
    stateText: String? = null,
    stateTimerText: String? = null,
    spacing: Dp = 5.dp,
    style: LectureCardStyle = LectureCardStyle.Inactive
) {
    Surface(
        shape = Shapes.medium,
        color = style.backgroundColor,
        modifier = modifier.clip(Shapes.medium).clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            val columnChildModifier = Modifier.fillMaxWidth()
            Text(
                text = titleText,
                modifier = columnChildModifier,
                style = MaterialTheme.typography.titleSmall
            )
            if (infoText != null || stateText != null || stateTimerText != null) {
                Spacer(
                    modifier = columnChildModifier.height(spacing)
                )
            }
            Row(
                modifier = columnChildModifier,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                if (infoText != null) {
                    Text(infoText, style = MaterialTheme.typography.bodySmall)
                }
                if (infoText != null && (stateText != null || stateTimerText != null)) {
                    Spacer(modifier = Modifier.width(spacing))
                }
                Column(
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.End
                ) {
                    if (stateText != null) {
                        Text(
                            stateText,
                            color = style.stateInfoColor,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    if (stateTimerText != null) {
                        Text(
                            stateTimerText,
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
fun LectureCardPreview() {
    ScheduleTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            LectureCard(
                titleText = "МДК.01.03 Разработка мобильных приложений",
                infoText = "16:20 – 18:00\n№366 • Климова И. С.",
                stateText = "Идет",
                stateTimerText = "До перерыва: 40:31",
                style = LectureCardStyle.Highlighted,
                modifier = Modifier.defaultMinSize(minHeight = 75.dp).padding(10.dp),
                onClick = {

                },
            )
        }
    }
}