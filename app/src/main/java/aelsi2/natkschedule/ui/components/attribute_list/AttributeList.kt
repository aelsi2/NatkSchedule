package aelsi2.natkschedule.ui.components.attribute_list

import aelsi2.natkschedule.R
import aelsi2.natkschedule.model.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign

@Composable
fun AttributeList(
    attributes: List<ScheduleAttribute>,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    filters: @Composable () -> Unit = {},
    onAttributeClick: (ScheduleIdentifier) -> Unit = {},
) {
    Box(modifier = modifier) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.matchParentSize()
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
                        is Group -> stringResource(
                            R.string.group_info,
                            attribute.year,
                            attribute.programName
                        )

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
        if (attributes.isEmpty()) {
            Box(modifier = Modifier.matchParentSize()) {
                Text(
                    text = stringResource(R.string.message_attribute_list_empty),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}