package com.office14.coffeedose.domain.usecase

import android.util.Log
import com.office14.coffeedose.domain.entity.CoffeeSize
import com.office14.coffeedose.domain.entity.OrderDetail
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.interactor.UseCase
import com.office14.coffeedose.domain.interactor.UseCaseBase
import com.office14.coffeedose.domain.interactor.UseCaseFlow
import com.office14.coffeedose.domain.repository.OrderDetailsRepository
import com.office14.coffeedose.domain.repository.SizesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MergeOrderDetailsIn @Inject constructor (private val orderDetailsRepository : OrderDetailsRepository) : UseCase<UseCaseBase.None, MergeOrderDetailsIn.Params>() {

    data class Params(val orderDetail: OrderDetail)

    override suspend fun run(params: Params): Either<Failure, UseCaseBase.None> {
        return try {
            val existingOrderDetails = orderDetailsRepository.unAttachedOrderDetailsStraight(params.orderDetail.owner).map { it.orderDetailInner }
            val existingDetail = existingOrderDetails.firstOrNull { it.checkEquals(params.orderDetail) }

            if (existingDetail == null) {
                orderDetailsRepository.insertOrderDetailsAndAddIns(params.orderDetail to params.orderDetail.addIns)
            } else
                orderDetailsRepository.updateCountWithOrderDetailsId(existingDetail.id, existingDetail.count + params.orderDetail.count)

            Either.Right(None())
        } catch (ex:Throwable ){
            Either.Left(Failure.ServerError())
        }


    }
}

/*
override suspend fun mergeIn(orderDetail: OrderDetail) {
    try {
        withContext(ioDispatcher) {

            val email = PreferencesRepositoryImpl.getUserEmail()
            val existingOrderDetails: List<OrderDetail>
            existingOrderDetails = if (email == EMPTY_STRING)
                orderDetailsDao.getUnAttachedDetailsWithoutUserStraight()
                    .map { it.toDomainModel().orderDetailInner }
            else
                orderDetailsDao.getUnAttachedDetailsForUserStraight(email)
                    .map { it.toDomainModel().orderDetailInner }

            val existingDetail =
                existingOrderDetails.firstOrNull { it.checkEquals(orderDetail) }
            if (existingDetail == null) {
                val container =
                    com.office14.coffeedose.data.database.OrderDetailsContainer(orderDetail)
                if (email != EMPTY_STRING) {
                    container.orderDetails.owner = email
                }

                orderDetailsDao.insertOrderDetailsAndAddIns(container)
            } else
                orderDetailsDao.updateCountWithOrderDetailsId(
                    existingDetail.id,
                    existingDetail.count + orderDetail.count
                )

        }
    } catch (ex: Exception) {
        Log.d("OrderDetailsRepository.mergeIn", ex.message ?: "")
    }
}*/
