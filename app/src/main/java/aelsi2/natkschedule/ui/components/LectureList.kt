package aelsi2.natkschedule.ui.components

import aelsi2.natkschedule.domain.model.GroupedLectureWithState
import aelsi2.natkschedule.domain.model.LectureState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.DecimalFormat
import java.time.DayOfWeek
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.*
import kotlin.math.round

@Composable
fun LectureList(
    lectures: List<GroupedLectureWithState>,
    enablePullToRefresh: Boolean,
    refreshing: Boolean,
    modifier: Modifier = Modifier,
    onRefresh: (() -> Unit)? = null,
    onReachedTop: (() -> Unit)? = null,
    onReachedBottom: (() -> Unit)? = null,
){
    // TODO Pull to refresh, колбеки
    LazyColumn(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.spacedBy(7.dp),){
        var previous: GroupedLectureWithState? = null
        for (lecture in lectures) {
            if (previous == null || lecture.date > previous.date) {
                item {
                    DateDivider(
                        dayOfWeekText = DayOfWeek.from(lecture.date)
                            .getDisplayName(TextStyle.FULL, Locale.getDefault()),
                        dateText = lecture.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                }
            }
            previous = lecture
            item {
                val state = lecture.state.collectAsState().value
                LectureCard(
                    titleText = lecture.disciplineName,
                    onClick = { /*TODO*/ },
                    stateTimerText = when (state) {
                        is LectureState.Ongoing -> "До конца: " + state.willEndIn.toHumanReadableString()
                        else -> null
                    },
                    colors = when (state) {
                        is LectureState.Ongoing -> LectureCardColors.Highlighted
                        is LectureState.OngoingPreBreak -> LectureCardColors.Highlighted
                        is LectureState.UpNext -> LectureCardColors.Active
                        else -> LectureCardColors.Inactive
                    },
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
            }
        }
    }
}

private fun Duration.toHumanReadableString(): String {
    var timePart = round(toMillis() / 1000f).toLong()
    val seconds = timePart % 60
    timePart = (timePart - seconds) / 60
    val minutes = timePart % 60
    timePart = (timePart - minutes) / 60
    val hours = timePart % 24
    timePart = (timePart - hours) / 24
    val days = timePart
    return days.toDurationPartWithSeparator(true) +
            hours.toDurationPartWithSeparator(days == 0L) +
            minutes.toDurationPartWithSeparator(days + hours == 0L, true) +
            seconds.toDurationPartWithSeparator(days + hours + minutes == 0L, true)
}

private fun Long.toDurationPartWithSeparator(isFirst: Boolean, displayZero: Boolean = false): String {
    if (isFirst) {
        if (this == 0L && !displayZero) {
            return ""
        }
        return toString()
    }
    return ':' + DecimalFormat("00").format(this)
}