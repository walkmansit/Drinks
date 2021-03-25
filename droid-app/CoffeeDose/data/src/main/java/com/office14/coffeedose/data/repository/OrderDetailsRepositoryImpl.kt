package com.office14.coffeedose.data.repository

import com.office14.coffeedose.data.database.AddinDbo
import com.office14.coffeedose.data.database.OrderDetailAndDrinkAndSize
import com.office14.coffeedose.data.database.OrderDetailDao
import com.office14.coffeedose.data.database.OrderDetailDbo
import com.office14.coffeedose.domain.entity.OrderDetail
import com.office14.coffeedose.domain.entity.OrderDetailFull
import com.office14.coffeedose.domain.repository.OrderDetailsRepository
import com.office14.coffeedose.domain.entity.Addin
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class OrderDetailsRepositoryImpl(private val orderDetailsDao: OrderDetailDao) : BaseRepository(), OrderDetailsRepository {

   /* val unAttachedOrderDetails =
        Transformations.map(orderDetailsDao.getUnAttachedDetails()) { itDbo ->
            itDbo.map { it.toDomainModel() }
        }*/

    override fun unattachedOrderDetailsForUser(email: String): Flow<Either<Failure, List<OrderDetailFull>>> =
        orderDetailsDao.getUnAttachedDetailsForUser(email).map { list -> Either.Right( list.map { it.toDomainModel() }) }
    /*=
        orderDetailsDao.getUnAttachedDetailsForUser(email).map {
                list ->Either.Right( list.map { it.toDomainModel()
                }) }*/

            /*List<OrderDetailFull> {
        val result: MutableList<OrderDetailFull> = mutableListOf()

        withContext(Dispatchers.IO) {
            val list = orderDetailsDao.getUnAttachedDetailsForUserStraight(email)
            list.forEach {
                result.add(it.toDomainModel())
            }
        }
        return result
    }*/

    //override fun unattachedOrderDetailsWithoutUser(): List<OrderDetailFull> = orderDetailsDao.getUnAttachedDetailsWithoutUserStraight().map { it.toDomainModel() }
    /*{
        val result: MutableList<OrderDetailFull> = mutableListOf()

        withContext(ioDispatcher) {
            val list = orderDetailsDao.getUnAttachedDetailsWithoutUserStraight()
            list.forEach {
                result.add(it.toDomainModel())
            }
        }
        return result
    }*/


   /* fun getOrderDetailsByOrderId(orderId: Int) =
        Transformations.map(orderDetailsDao.getDetailsByOrderId(orderId)) { itDbo ->
            itDbo.map { it.toDomainModel() }
        }*/

    //override fun unAttachedOrderDetails(emailFlow: Flow<String>): Flow<List<OrderDetailFull>> =  orderDetailsDao.getUnAttachedDetails().map { list -> list.map { it.toDomainModel() } }

    /*{
        val result = MediatorLiveData<List<OrderDetailFull>>()
        val uod = orderDetailsDao.getUnAttachedDetails()

        val update = {
            if (email.value == EMPTY_STRING) {
                result.value = uod?.value?.asSequence()?.filter { it.orderDetail.owner == null }
                    ?.map { it.toDomainModel() }?.toList() ?: listOf()
            } else
                result.value =
                    uod?.value?.asSequence()?.filter { it.orderDetail.owner == email.value }
                        ?.map { it.toDomainModel() }?.toList() ?: listOf()
        }

        result.addSource(email) { update.invoke() }
        result.addSource(uod) { update.invoke() }


        return result
    }*/

    override fun unAttachedOrderDetailsStraight(email: String): List<OrderDetailFull> = orderDetailsDao.getUnAttachedDetailsForUserStraight(email).map { it.toDomainModel() }
    /*{

        val result: MutableList<OrderDetailFull> = mutableListOf()

        withContext(ioDispatcher) {
            val list =
                if (email != EMPTY_STRING) orderDetailsDao.getUnAttachedDetailsForUserStraight(email) else orderDetailsDao.getUnAttachedDetailsWithoutUserStraight()
            list.forEach { result.add(it.toDomainModel()) }
        }

        return result

    }*/


    override fun unAttachedOrderDetails(): Flow<List<OrderDetailFull>> = orderDetailsDao.getUnAttachedDetails().map { list -> list.map { it.toDomainModel() } }

    /*{
        val result = MediatorLiveData<Int>()
        val uod = orderDetailsDao.getUnAttachedDetails()

        val update = {
            if (email.value == EMPTY_STRING) {
                result.value = uod?.value?.filter { it.orderDetail.owner == null }
                    ?.sumBy { it.orderDetail.count } ?: 0
                //result.value = orderDetailsDao.getUnAttachedDetailsCountWithoutUserStraight()
            } else
                result.value = uod?.value?.filter { it.orderDetail.owner == email.value }
                    ?.sumBy { it.orderDetail.count } ?: 0
            //result.value = orderDetailsDao.getUnAttachedDetailsCountForUserStraight(email.value!!)
        }

        result.addSource(email) { update.invoke() }
        result.addSource(uod) { update.invoke() }


        return result
    }*/

    override fun delete(oderDetails: OrderDetail)= orderDetailsDao.delete(OrderDetailDbo(oderDetails)
    )

    override fun deleteUnAttached() = orderDetailsDao.deleteUnAttached()

    /*suspend fun insertAll(orderDetails: List<OrderDetail>) {
        try {
            withContext(ioDispatcher) {
                orderDetailsDao.insertOrderDetailsAndAddIns(*orderDetails.map {
                    OrderDetailsContainer(
                        it
                    )
                }.toTypedArray())
            }
        } catch (ex: Exception) {
            Log.d("OrderDetailsRepository.insertAll", ex.message ?: "")
        }
    }

    suspend fun insertNew(orderDetail: OrderDetail) {
        try {
            withContext(ioDispatcher) {
                val container = OrderDetailsContainer(orderDetail)
                val email = PreferencesRepositoryImpl.getUserEmail()
                container.orderDetails.owner = if (email == EMPTY_STRING) null else email
                orderDetailsDao.insertOrderDetailsAndAddIns(container)
            }
        } catch (ex: Exception) {
            Log.d("OrderDetailsRepository.insertAll", ex.message ?: "")
        }
    }*/




    override fun updateUnattachedOrderDetailsWithEmail(newEmail: String,oldEmail: String) = orderDetailsDao.updateUnattachedOrderDetailsWithEmail(newEmail,oldEmail)

    override fun updateAttachedOrderDetailsWithOrderId(email: String, orderId: Int) =  orderDetailsDao.updateAttachedOrderDetailsWithOrderId(email, orderId)

    override fun deleteOrderDetailsByEmail(email: String) = orderDetailsDao.deleteByEmail(email)

    override fun insertOrderDetailsAndAddIns(pair:  Pair<OrderDetail,List<Addin>>) =
        orderDetailsDao.insertOrderDetailsAndAddIns(OrderDetailDbo(pair.first) to pair.second.map { AddinDbo(it) })

    override fun updateCountWithOrderDetailsId(id: Int, count: Int) = orderDetailsDao.updateCountWithOrderDetailsId(id,count)
}