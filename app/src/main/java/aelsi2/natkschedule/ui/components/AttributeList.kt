package aelsi2.natkschedule.ui.components

import aelsi2.natkschedule.R
import aelsi2.natkschedule.model.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@Composable
fun AttributeList(
    attributes: List<ScheduleAttribute>,
    modifier: Modifier = Modifier,
    filters: @Composable () -> Unit = {},
    onAttributeClick: (ScheduleIdentifier) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
    ) {
        item {
            filters()
        }
        items(attributes) { attribute ->
            AttributeListItem(
                mainText = when (attribute) {
                    is Teacher -> attribute.fullName
                    is Classroom -> attribute.fullName
                    is Group -> attribute.name
                    else -> ""
                },
                supportingText = when (attribute) {
                    is Teacher -> null
                    is Classroom -> attribute.address
                    is Group -> stringResource(R.string.group_info, attribute.year, attribute.programName)
                    else -> null
                },
                leadingIconResource = when (attribute) {
                    is Teacher -> R.drawable.person_outlined
                    is Classroom -> R.drawable.door_outlined
                    is Group -> R.drawable.people_outlined
                    else -> R.drawable.question_mark
                },
                onClick = {
                    onAttributeClick(attribute.scheduleIdentifier)
                }
            )
        }
    }
}