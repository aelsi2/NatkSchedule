package aelsi2.natkschedule.ui.screens.attribute_list.groups

import aelsi2.natkschedule.data.network.NetworkMonitor
import aelsi2.natkschedule.domain.use_cases.LoadAllAttributesUseCase
import aelsi2.natkschedule.model.Group
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
class GroupListScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    networkMonitor: NetworkMonitor,
    loadAttributes: LoadAllAttributesUseCase
) : OnlineAttributeListScreenViewModel(
    ScheduleType.Group,
    savedStateHandle,
    networkMonitor,
    loadAttributes
) {
    private val _programs: MutableStateFlow<SortedSet<String>> = MutableStateFlow(sortedSetOf())
    val programs: StateFlow<SortedSet<String>> get() = _programs

    // Может, надо брать допустимые курсы из результата с сервера,
    // хотя введение 5-го курса в колледжи, вроде как, не предвидится
    val years: List<Int> = listOf(1, 2, 3, 4)

    val selectedProgram: StateFlow<String?> =
        savedStateHandle.getStateFlow(FILTER_PROGRAM_KEY, null)
    val selectedYear: StateFlow<Int?> =
        savedStateHandle.getStateFlow(FILTER_YEAR_KEY, null)

    override val attributes: StateFlow<List<ScheduleAttribute>> =
        rawAttributes.applyFilters().applySearch().stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

    init {
        viewModelScope.launch {
            rawAttributes.collect { attributes ->
                val treeSet = TreeSet<String>()
                for (attribute in attributes) {
                    treeSet.add((attribute as Group).programName)
                }
                val selectedProgramValue = selectedProgram.value
                if (selectedProgramValue != null && selectedProgramValue !in treeSet) {
                    selectProgram(null)
                }
                _programs.emit(treeSet)
            }
        }
    }

    fun selectProgram(program: String?) {
        savedStateHandle[FILTER_PROGRAM_KEY] = program
    }

    fun selectYear(year: Int?) {
        savedStateHandle[FILTER_YEAR_KEY] = year
    }

    fun resetSelectedProgram() {
        selectProgram(null)
    }

    fun resetSelectedYear() {
        selectYear(null)
    }

    override fun resetSearchAndFilters() {
        super.resetSearchAndFilters()
        selectProgram(null)
        selectYear(null)
    }

    private fun Flow<List<ScheduleAttribute>>.applyFilters(): Flow<List<ScheduleAttribute>> =
        combine(this, selectedProgram, selectedYear) { attributes, program, year ->
            if (year == null && program == null) {
                attributes
            } else {
                attributes.filter {
                    (program == null || (it as Group).programName == program) &&
                            (year == null || (it as Group).year == year)
                }
            }
        }

    companion object {
        private const val FILTER_PROGRAM_KEY = "program"
        private const val FILTER_YEAR_KEY = "year"
    }
}