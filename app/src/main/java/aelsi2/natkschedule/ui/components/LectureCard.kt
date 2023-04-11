package aelsi2.natkschedule.ui.components

import aelsi2.natkschedule.ui.theme.ScheduleTheme
import aelsi2.natkschedule.ui.theme.Shapes
import androidx.compose.animation.animateColor
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.remember


interface LectureCardColors {
    val backgroundColor: Color
        @Composable get
    val titleTextColor: Color
        @Composable get
    val supportingTextColor: Color
        @Composable get
    val stateTextColor: Color
        @Composable get

    object Inactive : LectureCardColors {
        override val backgroundColor: Color
            @Composable
            get() = MaterialTheme.colorScheme.surface
        override val titleTextColor: Color
            @Composable
            get() = MaterialTheme.colorScheme.onSurface
        override val supportingTextColor: Color
            @Composable
            get() = MaterialTheme.colorScheme.onSurfaceVariant
        override val stateTextColor: Color
            @Composable
            get() = MaterialTheme.colorScheme.onSurface
    }

    object Active : LectureCardColors {
        override val backgroundColor: Color
            @Composable
            get() = MaterialTheme.colorScheme.secondaryContainer
        override val titleTextColor: Color
            @Composable
            get() = MaterialTheme.colorScheme.onSecondaryContainer
        override val supportingTextColor: Color
            @Composable
            get() = MaterialTheme.colorScheme.onSecondaryContainer
        override val stateTextColor: Color
            @Composable
            get() = MaterialTheme.colorScheme.onSecondaryContainer
    }

    class Highlighted(private val infiniteTransition: InfiniteTransition) : LectureCardColors {
        override val backgroundColor: Color
            @Composable
            get() {
                return infiniteTransition.animateColor(
                    initialValue = MaterialTheme.colorScheme.secondaryContainer,
                    targetValue = MaterialTheme.colorScheme.surface,
                    animationSpec = infiniteRepeatable(
                        animation = tween(750, easing = EaseIn),
                        repeatMode = RepeatMode.Reverse
                    )
                ).value
            }
        override val titleTextColor: Color
            @Composable
            get() = MaterialTheme.colorScheme.onSecondaryContainer
        override val supportingTextColor: Color
            @Composable
            get() = MaterialTheme.colorScheme.onSecondaryContainer
        override val stateTextColor: Color
            @Composable
            get() = MaterialTheme.colorScheme.primary

        companion object {
            @Composable
            fun remember(
                infiniteTransition: InfiniteTransition = rememberInfiniteTransition()
            ): LectureCardColors = remember(infiniteTransition) {
                Highlighted(infiniteTransition)
            }
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
    colors: LectureCardColors = LectureCardColors.Inactive
) {
    Surface(
        shape = Shapes.medium,
        color = colors.backgroundColor,
        modifier = modifier
            .clip(Shapes.medium)
            .defaultMinSize(minHeight = 48.dp)
            .clickable(onClick = onClick)
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
                style = MaterialTheme.typography.titleSmall,
                color = colors.titleTextColor
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
                    Text(
                        infoText,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.supportingTextColor
                    )
                }
                Spacer(modifier = Modifier.width(spacing))
                Column(
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.End
                ) {
                    if (stateText != null) {
                        Text(
                            stateText,
                            color = colors.stateTextColor,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    if (stateTimerText != null) {
                        Text(
                            stateTimerText,
                            textAlign = TextAlign.Right,
                            color = colors.supportingTextColor,
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
            Column(
                verticalArrangement = Arrangement.spacedBy(7.dp),
                modifier = Modifier.padding(vertical = 10.dp)
            ) {
                val highlighted = LectureCardColors.Highlighted.remember()
                LectureCard(
                    titleText = "МДК.01.03 Разработка мобильных приложений",
                    infoText = "16:20 – 18:00\n№366 • Климова И. С.",
                    stateText = "Идет",
                    stateTimerText = "До перерыва: 40:31",
                    colors = LectureCardColors.Inactive,
                    modifier = Modifier
                        .defaultMinSize(minHeight = 75.dp)
                        .padding(horizontal = 10.dp),
                    onClick = {

                    },
                )
                LectureCard(
                    titleText = "МДК.01.03 Разработка мобильных приложений",
                    infoText = "16:20 – 18:00\n№366 • Климова И. С.",
                    stateText = "Идет",
                    stateTimerText = "До перерыва: 40:31",
                    colors = LectureCardColors.Active,
                    modifier = Modifier
                        .defaultMinSize(minHeight = 75.dp)
                        .padding(horizontal = 10.dp),
                    onClick = {

                    },
                )
                LectureCard(
                    titleText = "МДК.01.03 Разработка мобильных приложений",
                    infoText = "16:20 – 18:00\n№366 • Климова И. С.",
                    stateText = "Идет",
                    stateTimerText = "До перерыва: 40:31",
                    colors = highlighted,
                    modifier = Modifier
                        .defaultMinSize(minHeight = 75.dp)
                        .padding(horizontal = 10.dp),
                    onClick = {

                    },
                )
            }
        }
    }
}