package com.office14.coffeedose.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.office14.coffeedose.di.AssistedSavedStateViewModelFactory
import com.office14.coffeedose.domain.entity.CoffeeSize
import com.office14.coffeedose.domain.entity.OrderDetail
import com.office14.coffeedose.extensions.mutableLiveData
import com.office14.coffeedose.plugins.PreferencesRepositoryImpl
import com.office14.coffeedose.domain.entity.Addin
import com.office14.coffeedose.domain.interactor.UseCaseBase
import com.office14.coffeedose.domain.usecase.*
import com.office14.coffeedose.entity.AddinView
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest

class SelectDoseAndAddinsViewModel @AssistedInject constructor(
    application: Application,
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val getSizes: GetSizes,
    private val getAddins: GetAddins,
    private val refreshSizes : RefreshSizes,
    private val refreshAddins: RefreshAddins,
    private val getSummaryPrice : GetSummaryPrice,
    private val mergeOrderDetailsIn: MergeOrderDetailsIn,

) : BaseViewModel(application) {

    private val drinkId = savedStateHandle.get<Int>("drinkId") ?: -1

    private val _sizes : MutableLiveData<List<CoffeeSize>> = mutableLiveData()
    val sizes : LiveData<List<CoffeeSize>> = _sizes

    private val _addins: MutableLiveData<List<AddinView>> = mutableLiveData()
    val addins : LiveData<List<AddinView>> = _addins

    private val viewModelJob = Job()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    //private val addinsTotal = mutableLiveData(0)

    private val _count:MutableLiveData<Int> = mutableLiveData(1)
    val count : LiveData<Int> =_count

    var isRefreshing = mutableLiveData(false)

    var errorMessage: MutableLiveData<String?> = mutableLiveData(null)

    private val _navigateDrinks = MutableLiveData<Boolean>()

    //private var selectedItemIndex = mutableLiveData(-1)
    private var selectedSizeIndex = -1

    private val selectedSizeChannel = ConflatedBroadcastChannel<CoffeeSize>()
    private val addInsChannel = ConflatedBroadcastChannel<List<Addin>>()
    private val countChannel = ConflatedBroadcastChannel<Int>()

    /*val selectedSize: LiveData<CoffeeSize?>
        get() = _selectedSize*/

    val navigateDrinks: LiveData<Boolean>
        get() = _navigateDrinks

    init {
        loadSizes(drinkId)
        loadAddins()
        getTotal()
        refreshData()
        initTotal()
    }

    private fun initTotal(){
        countChannel.offer(1)
        addInsChannel.offer(listOf())
    }


    private fun loadSizes(drinkId:Int){
        getSizes(GetSizes.Params(drinkId)){
            it.mapLatest { either ->
                either.fold(::handleFailure, ::handleSizesLoaded)
            }.launchIn(viewModelScope)
        }
    }

    private fun loadAddins(){
        getAddins(UseCaseBase.None()){
            it.mapLatest { either ->
                either.fold(::handleFailure, ::handleAddinsLoaded)
            }.launchIn(viewModelScope)
        }
    }

    private fun handleSizesLoaded(sizesInp:List<CoffeeSize>){
        _sizes.value = sizesInp
    }

    private fun handleAddinsLoaded(addinsInp:List<Addin>){
        _addins.value = addinsInp.map { AddinView.fromAddin(it) }
    }

    private val _summary : MutableLiveData<String> = mutableLiveData()
    val summary : LiveData<String> = _summary

    private fun getTotal(){
        val params : GetSummaryPrice.Params = GetSummaryPrice.Params(
            addInsChannel.asFlow(),
            selectedSizeChannel.asFlow(),
            countChannel.asFlow()
        )
        getSummaryPrice(params) {
            it.mapLatest {
                either -> either.fold(::handleFailure,::handleTotal)
            }.launchIn(viewModelScope)
        }
    }

    private fun handleTotal(total : String){
        _summary.value = total
    }

    /*fun getSummary(): LiveData<String> {



        return

        var result = MediatorLiveData<String>()

        val update = {
            val portionPrice = (addinsTotal.value
                ?: 0) + if (selectedSize.value == null) 0 else selectedSize.value!!.price
            val countP = count.value!!

            result.value = "${portionPrice * countP} Р."
        }

        result.addSource(selectedSize) { update.invoke() }
        result.addSource(addinsTotal) { update.invoke() }
        result.addSource(count) { update.invoke() }

        return result
    }*/



    /*private var _selectedSize = Transformations.map(selectedItemIndex) {
        if (sizes.value == null)
            return@map null
        else return@map sizes.value!![selectedItemIndex.value!!]
    }*/


    fun refreshData(showRefresh: Boolean = false) {

        if (showRefresh) isRefreshing.value = true
        refreshSizes(RefreshSizes.Params(drinkId)){
            it.fold(::handleFailure) { if (showRefresh) isRefreshing.value = false }
        }

        if (showRefresh) isRefreshing.value = true
        refreshAddins(UseCaseBase.None()){
            it.fold(::handleFailure) { if (showRefresh) isRefreshing.value = false }
        }
    }

    fun onSelectedSizeIndexChanged(newIndex: Int) {
        selectedSizeIndex = newIndex
        val size = sizes.value!![newIndex]
        selectedSizeChannel.offer(size)

        /*if (newIndex != selectedItemIndex.value)
            selectedItemIndex.value = newIndex*/
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun updateSelectedAddins(addin: AddinView, isChecked: Boolean) {

        addin.isSelected = isChecked
        _addins.value?.let { addInsChannel.offer(it.filter { item -> item.isSelected }.map { item -> item.toDomainModel() }) }

    }

    fun updateCount(newValue: Int) {
        if (_count.value!! != newValue) {
            _count.value = newValue
            countChannel.offer(newValue)
        }
    }

    private fun getAddinsToString(): String? {
        if (addins.value?.size == 0) return null
        else return addins.value!!.filter { it.isSelected }.map { it.price }.joinToString()
    }

    fun addIntoOrderDetails() {
        val email = PreferencesRepositoryImpl.getUserEmail()!!

        val newOrderDetail = OrderDetail(
            id = 0,
            drinkId = drinkId,
            sizeId = sizes.value!![selectedSizeIndex].id,
            addIns = addins.value?.filter { it.isSelected }?.map { it.toDomainModel() } ?: listOf(),
            count = count.value!!,
            owner = email,
            orderId = null
        )

        mergeOrderDetailsIn(MergeOrderDetailsIn.Params(newOrderDetail)){ either ->
            either.fold(::handleFailure,::navigateDrinks)
        }
    }

    private fun navigateDrinks(right:UseCaseBase.None){
        _navigateDrinks.value = true
    }

    fun doneNavigating() {
        _navigateDrinks.value = false
    }

    @AssistedInject.Factory
    interface Factory : AssistedSavedStateViewModelFactory<SelectDoseAndAddinsViewModel>
}