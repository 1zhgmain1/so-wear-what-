package com.example.weathercloth.v2.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weathercloth.v2.data.local.CityEntity
import com.example.weathercloth.v2.data.local.ReminderEntity
import com.example.weathercloth.v2.data.local.UserPreferenceEntity
import com.example.weathercloth.v2.data.local.WardrobeItemEntity
import com.example.weathercloth.v2.data.repository.WeatherRepository
import com.example.weathercloth.v2.domain.OutfitAdvice
import com.example.weathercloth.v2.domain.WeatherSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class WeatherUiState(
    val loading: Boolean = true,
    val weather: WeatherSnapshot? = null,
    val advice: OutfitAdvice? = null,
    val error: String? = null
)

data class AppUiState(
    val preference: UserPreferenceEntity = UserPreferenceEntity(),
    val cities: List<CityEntity> = emptyList(),
    val wardrobe: List<WardrobeItemEntity> = emptyList(),
    val reminders: List<ReminderEntity> = emptyList()
)

class WeatherClothViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val _weatherState = MutableStateFlow(WeatherUiState())
    val weatherState: StateFlow<WeatherUiState> = _weatherState

    val appState: StateFlow<AppUiState> = combine(
        repository.observePreference(),
        repository.observeCities(),
        repository.observeWardrobe(),
        repository.observeReminders()
    ) { pref, cities, wardrobe, reminders ->
        AppUiState(pref ?: UserPreferenceEntity(), cities, wardrobe, reminders)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppUiState())

    init {
        viewModelScope.launch {
            repository.ensureDefaults()
            val cached = repository.getCachedAdvice()
            if (cached != null) {
                _weatherState.value = WeatherUiState(loading = false, weather = cached.first, advice = cached.second)
            }
        }
    }

    /** Called after location permission. Tries GPS first, falls back to default city if denied. */
    fun initializeWithLocation(useLocation: Boolean) {
        viewModelScope.launch {
            if (useLocation) {
                repository.addCurrentCity().onFailure {
                    _weatherState.value = _weatherState.value.copy(error = "Location failed, using default city")
                }
            }
            refresh(forceRefresh = false)
        }
    }

    fun refresh(city: CityEntity? = null, forceRefresh: Boolean = true, showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) _weatherState.value = _weatherState.value.copy(loading = true, error = null)
            repository.loadAdvice(city, forceRefresh).fold(
                onSuccess = { (weather, advice) ->
                    _weatherState.value = WeatherUiState(loading = false, weather = weather, advice = advice)
                },
                onFailure = {
                    _weatherState.value = WeatherUiState(loading = false, error = it.message ?: "Load failed")
                }
            )
        }
    }

    fun savePreference(preference: UserPreferenceEntity) = viewModelScope.launch {
        repository.savePreference(preference)
        refresh(showLoading = false)
    }

    fun saveReminder(reminder: ReminderEntity) = viewModelScope.launch {
        repository.saveReminder(reminder)
    }

    fun deleteReminder(reminder: ReminderEntity) = viewModelScope.launch {
        repository.deleteReminder(reminder)
    }

    fun addCity(name: String) = viewModelScope.launch {
        repository.addCityByName(name).onFailure { _weatherState.value = _weatherState.value.copy(error = it.message) }
    }

    fun addCurrentCity() = viewModelScope.launch {
        repository.addCurrentCity().onFailure { _weatherState.value = _weatherState.value.copy(error = it.message) }
    }

    fun selectCity(city: CityEntity) = viewModelScope.launch {
        repository.selectCity(city)
        refresh(city, showLoading = false)
    }

    fun deleteCity(city: CityEntity) = viewModelScope.launch {
        repository.deleteCity(city)
        refresh(showLoading = false)
    }

    fun saveWardrobe(item: WardrobeItemEntity) = viewModelScope.launch {
        repository.addWardrobeItem(item)
        refresh(showLoading = false)
    }

    fun renameCity(city: CityEntity, newName: String) = viewModelScope.launch {
        repository.renameCity(city, newName)
    }

    fun deleteWardrobe(item: WardrobeItemEntity) = viewModelScope.launch {
        repository.deleteWardrobeItem(item)
        refresh(showLoading = false)
    }
}

class WeatherClothViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = WeatherClothViewModel(repository) as T
}
