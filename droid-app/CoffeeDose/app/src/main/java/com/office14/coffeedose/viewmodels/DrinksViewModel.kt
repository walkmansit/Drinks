package com.office14.coffeedose.viewmodels

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.office14.coffeedose.extensions.mutableLiveData
import com.office14.coffeedose.plugins.PreferencesRepositoryImpl
import com.office14.coffeedose.domain.entity.Coffee
import com.office14.coffeedose.domain.entity.UserA
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.interactor.UseCaseBase
import com.office14.coffeedose.domain.repository.PreferencesRepository
import com.office14.coffeedose.domain.usecase.GetDrinks
import com.office14.coffeedose.domain.usecase.GetUser
import com.office14.coffeedose.domain.usecase.RefreshDrinks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

@ExperimentalCoroutinesApi
class DrinksViewModel @Inject constructor(
        application: Application,
        private val getDrinks : GetDrinks,
        private val refreshDrinks : RefreshDrinks,
        private val getUser: GetUser,
        private val prefRepository : PreferencesRepository
) : BaseViewModel(application) {

    //private val prefRepository = PreferencesRepositoryImpl

    private val appTheme = MutableLiveData<Int>()

    private val notDefinedId = -1

    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _user : MutableLiveData<UserA> = mutableLiveData()
    val user : LiveData<UserA> = _user

    private val _drinks : MutableLiveData<List<Coffee>> = mutableLiveData()
    val drinks : LiveData<List<Coffee>> = _drinks

    var isRefreshing = mutableLiveData(false)

    var errorMessage: MutableLiveData<String?> = mutableLiveData(null)

    private val _selectedId = mutableLiveData(notDefinedId)
    private val _navigatingOrders = MutableLiveData<Boolean>()

    val navigatingOrders: LiveData<Boolean>
        get() = _navigatingOrders

    val selectedId: LiveData<Int>
        get() = _selectedId

    fun getDrinkName(): String  = drinks.value?.firstOrNull { coffee -> coffee.id == _selectedId.value }?.name ?: "Not defined"

    @ExperimentalCoroutinesApi
    private fun loadDrinks() {
        getDrinks(UseCaseBase.None()){
            it.mapLatest { either ->
                either.fold(::handleFailure,::handleGetDrinks)
            }
            .launchIn(viewModelScope)
        }
    }

    override fun handleFailure(failure: Failure){
        super.handleFailure(failure)
        isRefreshing.value = false
    }

    private fun handleGetDrinks(drinksInput : List<Coffee>){
        _drinks.value = drinksInput
    }

    init {
        loadDrinks()
        refreshData()
        getUser()
        appTheme.value = prefRepository.getAppTheme()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun refreshData(showRefresh: Boolean = false) {
        if (showRefresh) isRefreshing.value = true
        refreshDrinks(UseCaseBase.None()){
            it.fold(::handleFailure) { if (showRefresh) isRefreshing.value = false }
        }
    }

    private fun getUser(){

        fun right(userA: UserA){
            _user.value = userA
        }

        getUser(UseCaseBase.None()){
            it.mapLatest {
                either -> either.fold({},::right)
            }.launchIn(viewModelScope)
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

    /*fun navigateOrders() {
        _navigatingOrders.value = true
    }*/

    fun getTheme() : LiveData<Int> = appTheme

    fun switchTheme() {
        if (appTheme.value == AppCompatDelegate.MODE_NIGHT_YES)
            appTheme.value = AppCompatDelegate.MODE_NIGHT_NO
        else
            appTheme.value = AppCompatDelegate.MODE_NIGHT_YES

        prefRepository.saveAppTheme(appTheme.value!!)
    }

}
