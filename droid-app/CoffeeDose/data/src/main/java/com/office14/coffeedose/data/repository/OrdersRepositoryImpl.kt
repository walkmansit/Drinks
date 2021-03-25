package com.office14.coffeedose.data.repository

import com.office14.coffeedose.data.network.CreateOrderBody
import com.office14.coffeedose.data.network.DeleteFcmDeviceTokenBody
import com.office14.coffeedose.data.network.PostFcmDeviceTokenBody
import com.office14.coffeedose.domain.entity.LastOrderStatus
import com.office14.coffeedose.domain.entity.Order
import com.office14.coffeedose.domain.entity.OrderDetailFull
import com.office14.coffeedose.domain.entity.OrderInfo
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.functional.map
import com.office14.coffeedose.domain.interactor.UseCaseBase
import com.office14.coffeedose.domain.repository.OrdersRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

class OrdersRepositoryImpl @Inject constructor(
    private val ordersDao: com.office14.coffeedose.data.database.OrderDao,
    private val coffeeApi: com.office14.coffeedose.data.network.CoffeeApiService
) :BaseRepository(), OrdersRepository {

    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

    //private val allOrders : Flow<List<Order>> = ordersDao.getAll().map { it.map { orderDbo ->  orderDbo.toDomainModel() } }

    //suspend fun getCurrentQueueOrderNormal(): Order? = ordersDao.getAll().firstOrNull()?.get(0)?.toDomainModel()
    /*{
        val list = ordersDao.getAll().first()
        if (list.isNullOrEmpty()) return null
        return list[0].toDomainModel()
    }*/

    /*fun getOrderById(orderId: Int) = Transformations.map(ordersDao.getById(orderId)) { itDbo ->
        itDbo.map { it.toDomainModel() }
    }*/

    /*fun getCurrentQueueOrder(): LiveData<Order> = Transformations.map(ordersDao.getAll()) { itDbo ->
        if (itDbo.size == 1) {
            return@map itDbo[0].toDomainModel()
        }
        return@map null
    }*/

    override fun getCurrentQueueOrderByUser(email:String): Flow<Order?> =
        ordersDao.getAllNotFinishedForuser(email).map { list -> list.firstOrNull()?.toDomainModel()
        }
    /*{
        return ordersDao.getAllNotFinished().combine(emailFlow) { orders, email ->
            orders.filter { it.owner == email }.firstOrNull()?.map { it.toDomainModel() }
        }
    }*/

    override fun getCurrentNotFinishedOrderByUser(email: String): Order? = ordersDao.getAllForUserNotFinishedStraight(email).getOrNull(0)?.toDomainModel()
    /*{

        var result: Order? = null

        withContext(ioDispatcher) {
            val list = ordersDao.getAllForUserNotFinishedStraight(email)

            if (list.size == 1) {
                result = list[0].toDomainModel()
            }

        }
        return result
    }*/


   /* override fun queueOrderStatus(emailFlow: Flow<String>): Flow<OrderStatus> = ordersDao.getAllNotFinished().combine(emailFlow) { orders, email ->
        OrderStatus.getStatusByString(orders.firstOrNull { it.owner == email }?.statusCode ?: "unknown")
    }.flowOn(defaultDispatcher).conflate()*/


   /* {

        val result = MediatorLiveData<OrderStatus>()
        val allOrdersInQueue = ordersDao.getAllNotFinished()

        val update = {
            val list = allOrdersInQueue.value?.filter { it.owner == email.value }
            if (list?.size == 1) {
                result.value = OrderStatus.getStatusByString(list[0].statusCode)
            } else
                result.value = OrderStatus.NONE
        }


        result.addSource(email) { update.invoke() }
        result.addSource(allOrdersInQueue) { update.invoke() }

        return result

    }*/



    override fun createOrder (orders: List<OrderDetailFull>,comment: String?, token: String?, email: String): Either<Failure,Int>  {
        val ordersBody = CreateOrderBody(comment)
        ordersBody.fillWithOrders(orders)
        val newOrder = requestApi(coffeeApi.createOrder(ordersBody,composeAuthHeader(token))){
            it.toDataBaseModel(email)
        }
        newOrder.fold({},{right -> ordersDao.insertAllOrders(right)})
        return newOrder.map { it.id }
    }

    override fun refreshLastOrderStatus(token: String) : Either<Failure, LastOrderStatus> {
        //withContext(ioDispatcher) {
            val status = requestApi(coffeeApi.getLastOrderStatusForUser(composeAuthHeader(token))){it.toDomainModel()}
            status.fold({},{ right ->
                ordersDao.updateStatusCodeAndNameById(right.id,right.statusCode,right.statusName)
            })
            //delay(5000) //TODO handle delay
            return status
        //}
    }

    override fun markAsFinishedForUser(email: String) =  ordersDao.markAsFinishedForUser(email)

    // using on log in
    override fun getLastOrderForUserAndPutIntoDB(token: String?, email: String) : Either<Failure,UseCaseBase.None> {
        val lastOrder = requestApi(coffeeApi.getLastOrderForUser(composeAuthHeader(token))){
            it.toDataBaseModel(email)
        }
        lastOrder.fold({},{right -> ordersDao.insertAllOrders(right)})
        return lastOrder.map { UseCaseBase.None() }
    }

    override fun updateFcmDeviceToken(deviceId: String, fcmToken: String, idToken: String) : Either<Failure,UseCaseBase.None> {
        val body = PostFcmDeviceTokenBody(deviceId, fcmToken)
        val result = requestApi(coffeeApi.updateFcmDeviceToken(body, composeAuthHeader(idToken))){}
        return result.map { UseCaseBase.None() }
    }

    override fun deleteFcmDeviceTokenOnLogOut(deviceId: String, idToken: String) : Either<Failure,UseCaseBase.None> {
        val result = requestApi(coffeeApi.deleteFcmDeviceToken(DeleteFcmDeviceTokenBody(deviceId),composeAuthHeader(idToken))){}
        return result.map { UseCaseBase.None() }
    }

    // using only for got data on order awaiting screen
    override fun getLastOrderInfo(token: String, owner:String): Either<Failure,OrderInfo> =
        requestApi(coffeeApi.getLastOrderForUser(composeAuthHeader(token))){it.toOrderInfoDomainModel(owner)}
     /*
        val order =

        var orderInfo: OrderInfo? = null
        try {
            withContext(ioDispatcher) {
                val result = coffeeApi.getLastOrderForUser(composeAuthHeader(token))
                if (result.hasError())
                    throw com.office14.coffeedose.data.network.HttpExceptionEx(result.getError())
                else {
                    //val existing = ordersDao.getByIdStraight(result.payload!!.id)
                    //if (existing.isNotEmpty() && existing[0].isFinished == "false")
                    orderInfo = result.payload!!.toOrderInfoDomainModel()
                }
            }
        } catch (ex: Exception) {
            Log.d("OrdersRepository.getLastOrderInfo", ex.message ?: "")
        }
        return orderInfo
    }*/

}