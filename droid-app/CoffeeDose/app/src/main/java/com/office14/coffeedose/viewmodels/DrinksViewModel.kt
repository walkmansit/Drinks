package com.office14.coffeedose.viewmodels

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.office14.coffeedose.extensions.mutableLiveData
import com.office14.coffeedose.network.HttpExceptionEx
import com.office14.coffeedose.repository.CoffeeRepository
import com.office14.coffeedose.repository.PreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class DrinksViewModel @Inject constructor(
    application: Application,
    private val coffeeRepository: CoffeeRepository
) : AndroidViewModel(application) {

    private val prefRepository = PreferencesRepository

    private val appTheme = MutableLiveData<Int>()

    private val notDefinedId = -1

    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val drinks = coffeeRepository.drinks

    var isRefreshing = mutableLiveData(false)

    var errorMessage: MutableLiveData<String?> = mutableLiveData(null)

    private val _selectedId = mutableLiveData(notDefinedId)
    private val _navigatingOrders = MutableLiveData<Boolean>()

    val navigatingOrders: LiveData<Boolean>
        get() = _navigatingOrders

    val selectedId: LiveData<Int>
        get() = _selectedId

    fun getDrinkName(): String {
        val coffee = drinks.value?.firstOrNull { coffee -> coffee.id == _selectedId.value }
        return coffee?.name ?: "Not defined"
    }


    init {
        refreshData()
        appTheme.value = prefRepository.getAppTheme()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun refreshData(showRefresh: Boolean = false) {
        viewModelScope.launch {
            try {

                if (showRefresh) isRefreshing.value = true

                coffeeRepository.refreshDrinks()

                errorMessage.value = null
            } catch (responseEx: HttpExceptionEx) {
                errorMessage.value = responseEx.error.title
            } catch (ex: Exception) {
                errorMessage.value = ex.message
            } finally {
                if (showRefresh) isRefreshing.value = false
            }
        }
    }

    fun doneNavigatingDose() {
        _selectedId.value = notDefinedId
    }

    fun doneNavigatingOrders() {
        _selectedId.value = notDefinedId
    }

    fun selectDrink(id: Int) {
        _selectedId.value = id
    }

    fun navigateOrders() {
        _navigatingOrders.value = true
    }

    fun getTheme() : LiveData<Int> = appTheme

    fun switchTheme() {
        if (appTheme.value == AppCompatDelegate.MODE_NIGHT_YES)
            appTheme.value = AppCompatDelegate.MODE_NIGHT_NO
        else
            appTheme.value = AppCompatDelegate.MODE_NIGHT_YES

        prefRepository.saveAppTheme(appTheme.value!!)
    }
}
