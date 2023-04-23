package aelsi2.natkschedule.ui.screens.attribute_list.classrooms

import aelsi2.natkschedule.R
import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.ui.SetUiStateLambda
import aelsi2.natkschedule.ui.components.DropdownFilterChip
import aelsi2.natkschedule.ui.components.FilterChipRow
import aelsi2.natkschedule.ui.screens.attribute_list.AttributeListScreen
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.koin.androidx.compose.koinViewModel

@Composable
fun ClassroomListScreen(
    onAttributeClick: (ScheduleIdentifier) -> Unit,
    setUiState: SetUiStateLambda,
    modifier: Modifier = Modifier,
    onError: suspend () -> Unit = {},
    viewModel: ClassroomListScreenViewModel = koinViewModel()
) {
    AttributeListScreen(
        title = stringResource(R.string.title_classroom_list),
        searchPlaceholderText = stringResource(R.string.search_classrooms_placeholder),
        filters = {
            FilterChipRow {
                val addresses by viewModel.addresses.collectAsState()
                val selectedAddress by viewModel.selectedAddress.collectAsState()
                var addressesExpanded by remember { mutableStateOf(false) }
                DropdownFilterChip(
                    text = selectedAddress ?: stringResource(R.string.filter_classroom_list_address),
                    hasSelectedValue = selectedAddress != null,
                    isExpanded = addressesExpanded,
                    onChangeExpandedRequest = { expanded ->
                        if (!expanded || addresses.isNotEmpty()) {
                            addressesExpanded = expanded
                        }
                    },
                    onClearClick = viewModel::resetSelectedAddress
                ) {
                    for (address in addresses) {
                        DropdownMenuItem(
                            text = {
                                Text(address)
                            },
                            onClick = {
                                viewModel.selectAddress(address)
                                addressesExpanded = false
                            }
                        )
                    }
                }
            }
        },
        onAttributeClick = onAttributeClick,
        onError = onError,
        setUiState = setUiState,
        modifier = modifier,
        viewModel = viewModel
    )
}