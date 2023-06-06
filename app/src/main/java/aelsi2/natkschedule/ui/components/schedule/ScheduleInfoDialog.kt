package aelsi2.natkschedule.ui.components.schedule

import aelsi2.natkschedule.R
import aelsi2.natkschedule.model.Classroom
import aelsi2.natkschedule.model.Group
import aelsi2.natkschedule.model.ScheduleAttribute
import aelsi2.natkschedule.model.Teacher
import aelsi2.natkschedule.ui.components.InfoDialog
import aelsi2.natkschedule.ui.components.InfoDialogRow
import aelsi2.natkschedule.ui.components.InfoDialogState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun rememberScheduleInfoDialogState(
    initialData: ScheduleAttribute? = null,
    scrollState: ScrollState = rememberScrollState()
): ScheduleInfoDialogState = remember(initialData, scrollState) {
    ScheduleInfoDialogState(initialData, scrollState)
}

class ScheduleInfoDialogState(
    initialData: ScheduleAttribute?,
    scrollState: ScrollState,
) : InfoDialogState<ScheduleAttribute>(initialData, scrollState)

//TODO Переходы на страницы с фильтрами
@Composable
fun ScheduleInfoDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    state: ScheduleInfoDialogState = rememberScheduleInfoDialogState(),
) {
    InfoDialog(
        titleText = { it.longDisplayName },
        onDismissRequest = onDismissRequest,
        state = state,
        modifier = modifier
    ) { data ->
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = when (data) {
                    is Classroom -> stringResource(R.string.schedule_details_classroom)
                    is Teacher -> stringResource(R.string.schedule_details_teacher)
                    is Group -> stringResource(R.string.schedule_details_group)
                    else -> "???"
                },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Column {
                when (data) {
                    is Classroom -> {
                        if (data.address != null) {
                            InfoDialogRow(
                                mainText = data.address,
                                leadingIconResource = R.drawable.location_outlined
                            )
                        }
                    }
                    is Teacher -> {
                        //TODO Отображение подробной информации о преподе (мыло/телефон, курируемая группа)
                    }
                    is Group -> {
                        InfoDialogRow(
                            mainText = stringResource(
                                R.string.group_schedule_info_year,
                                data.year
                            ),
                            leadingIconResource = R.drawable.calendar_outlined
                        )
                        InfoDialogRow(
                            mainText = data.programName,
                            leadingIconResource = R.drawable.books_outlined
                        )
                    }
                }
            }
        }
    }
}