package aelsi2.natkschedule.ui.screens.attribute_list.classrooms

import aelsi2.natkschedule.data.network.NetworkMonitor
import aelsi2.natkschedule.domain.use_cases.LoadAllAttributesUseCase
import aelsi2.natkschedule.model.Classroom
import aelsi2.natkschedule.model.ScheduleAttribute
import aelsi2.natkschedule.model.ScheduleType
import aelsi2.natkschedule.ui.screens.attribute_list.OnlineAttributeListScreenViewModel
import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.SortedSet
import java.util.TreeSet

@Stable
class ClassroomListScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    networkMonitor: NetworkMonitor,
    loadAttributes: LoadAllAttributesUseCase
) : OnlineAttributeListScreenViewModel(
    ScheduleType.Classroom,
    savedStateHandle,
    networkMonitor,
    loadAttributes
) {
    private val _addresses: MutableStateFlow<SortedSet<String>> = MutableStateFlow(sortedSetOf())
    val addresses: StateFlow<SortedSet<String>> get() = _addresses

    val selectedAddress: StateFlow<String?> =
        savedStateHandle.getStateFlow(FILTER_ADDRESS_KEY, null)

    override val hasFiltersSet: StateFlow<Boolean> =
        combine(searchString, selectedAddress) { search, address ->
            search.isNotEmpty() || address != null
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    override val attributes: StateFlow<List<ScheduleAttribute>> =
        rawAttributes.applyFilters().applySearch().stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), listOf()
        )

    init {
        viewModelScope.launch {
            rawAttributes.collect { attributes ->
                val treeSet = TreeSet<String>()
                for (attribute in attributes) {
                    val address = (attribute as Classroom).address
                    if (address != null) {
                        treeSet.add(address)
                    }
                }
                val selectedAddressValue = selectedAddress.value
                if (selectedAddressValue != null && selectedAddressValue !in treeSet) {
                    selectAddress(null)
                }
                _addresses.emit(treeSet)
            }
        }
    }

    fun selectAddress(address: String?) {
        savedStateHandle[FILTER_ADDRESS_KEY] = address
    }

    fun resetSelectedAddress() {
        selectAddress(null)
    }

    override fun resetFilters() {
        super.resetFilters()
        selectAddress(null)
    }

    private fun Flow<List<ScheduleAttribute>>.applyFilters(): Flow<List<ScheduleAttribute>> =
        combine(selectedAddress) { attributes, address ->
            if (address == null) {
                attributes
            } else {
                attributes.filter {
                    (it as Classroom).address == address
                }
            }
        }

    companion object {
        private const val FILTER_ADDRESS_KEY = "address"
    }
}