package aelsi2.natkschedule.ui.screens.attribute_list

import aelsi2.natkschedule.data.network.NetworkMonitor
import aelsi2.natkschedule.data.preferences.FavoritesReader
import aelsi2.natkschedule.domain.model.ScreenState
import aelsi2.natkschedule.domain.use_cases.LoadAllAttributesUseCase
import aelsi2.natkschedule.domain.use_cases.LoadAttributesUseCase
import aelsi2.natkschedule.model.Classroom
import aelsi2.natkschedule.model.Group
import aelsi2.natkschedule.model.ScheduleAttribute
import aelsi2.natkschedule.model.ScheduleType
import androidx.annotation.CallSuper
import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.TreeSet

@Stable
class TeacherListScreenViewModel(
    savedStateHandle: SavedStateHandle,
    networkMonitor: NetworkMonitor,
    loadAttributes: LoadAllAttributesUseCase
) : OnlineAttributeListScreenViewModel(
    ScheduleType.Teacher,
    savedStateHandle,
    networkMonitor,
    loadAttributes
) {
    override val attributes: StateFlow<List<ScheduleAttribute>> =
        rawAttributes.applySearch().stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), listOf()
        )
}

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
    private val _addresses: MutableStateFlow<Iterable<String>> = MutableStateFlow(emptyList())
    val addresses: StateFlow<Iterable<String>> get() = _addresses

    val selectedAddress: StateFlow<String?> =
        savedStateHandle.getStateFlow(FILTER_ADDRESS_KEY, null)

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

    override fun resetSearchAndFilters() {
        super.resetSearchAndFilters()
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
    private val _programs: MutableStateFlow<Iterable<String>> = MutableStateFlow(emptyList())
    val programs: StateFlow<Iterable<String>> get() = _programs

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

class FavoritesListScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    networkMonitor: NetworkMonitor,
    loadAttributes: LoadAttributesUseCase,
    favoritesReader: FavoritesReader,
) : AttributeListScreenViewModel(savedStateHandle) {
    private val rawAttributes = MutableStateFlow<List<ScheduleAttribute>>(emptyList())

    val selectedScheduleType: StateFlow<ScheduleType?> = savedStateHandle.getStateFlow(
        FILTER_SCHEDULE_TYPE_KEY, null
    )

    override val attributes: StateFlow<List<ScheduleAttribute>> =
        rawAttributes.applyFilters().applySearch().stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

    override val state: StateFlow<ScreenState> =
        combineTransform(
            networkMonitor.isOnline,
            favoritesReader.favoriteScheduleIds,
            refreshTrigger
        ) { isOnline, favorites, _ ->
            var hadErrors = false
            emit(ScreenState.Loading)
            loadAttributes(
                favorites,
                loadOffline = true,
                loadOnline = isOnline,
                storeOffline = isOnline,
                onOfflineError = { hadErrors = true },
                onOfflineSuccess = { rawAttributes.emit(it) },
                onOnlineError = { hadErrors = true },
                onOfflineStoreError = { hadErrors = true },
                onOfflineStoreSuccess = { rawAttributes.emit(it) }
            )
            emit(when {
                hadErrors -> ScreenState.Error
                !isOnline -> ScreenState.NoInternet
                else -> ScreenState.Loaded
            })
        }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), ScreenState.Loading
        )

    fun selectScheduleType(scheduleType: ScheduleType?) {
        savedStateHandle[FILTER_SCHEDULE_TYPE_KEY] = scheduleType
    }

    private fun Flow<List<ScheduleAttribute>>.applyFilters(): Flow<List<ScheduleAttribute>> =
        combine(selectedScheduleType) { attributes, type ->
            if (type == null) {
                attributes
            } else {
                attributes.filter {
                    it.scheduleIdentifier.type == type
                }
            }
        }

    init {
        refresh()
    }

    companion object {
        private const val FILTER_SCHEDULE_TYPE_KEY = "scheduleType"
    }
}

@Stable
abstract class OnlineAttributeListScreenViewModel(
    scheduleType: ScheduleType,
    savedStateHandle: SavedStateHandle,
    networkMonitor: NetworkMonitor,
    loadAttributes: LoadAllAttributesUseCase
) : AttributeListScreenViewModel(savedStateHandle) {
    protected val rawAttributes = MutableStateFlow<List<ScheduleAttribute>>(listOf())

    override val state: StateFlow<ScreenState> = combineTransform(
        networkMonitor.isOnline,
        refreshTrigger
    ) { isOnline, _ ->
        if (!isOnline) {
            emit(ScreenState.NoInternet)
            return@combineTransform
        }
        emit(ScreenState.Loading)
        loadAttributes(
            scheduleType,
            onSuccess = {
                rawAttributes.emit(it)
                emit(ScreenState.Loaded)
            },
            onFailure = {
                emit(ScreenState.Error)
            }
        )
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), ScreenState.Loading
    )

    init {
        refresh()
    }
}

@Stable
abstract class AttributeListScreenViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    protected val refreshTrigger: MutableSharedFlow<Unit> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val searchString: StateFlow<String> = savedStateHandle.getStateFlow(
        FILTER_SEARCH_STRING_KEY, ""
    )
    abstract val attributes: StateFlow<List<ScheduleAttribute>>
    abstract val state: StateFlow<ScreenState>
    fun refresh() {
        viewModelScope.launch {
            refreshTrigger.emit(Unit)
        }
    }

    fun setSearchString(string: String) {
        savedStateHandle[FILTER_SEARCH_STRING_KEY] = string
    }

    fun resetSearchString() {
        setSearchString("")
    }

    @CallSuper
    open fun resetSearchAndFilters() {
        resetSearchString()
    }

    protected fun Flow<List<ScheduleAttribute>>.applySearch(): Flow<List<ScheduleAttribute>> =
        combine(searchString) { attributes, search ->
            if (search.isBlank()) {
                attributes
            } else {
                attributes.filter {
                    it.matchesString(search)
                }
            }
        }

    companion object {
        private const val FILTER_SEARCH_STRING_KEY = "searchString"
    }
}