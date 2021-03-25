package com.office14.coffeedose.domain.usecase

import com.office14.coffeedose.domain.defaultuser.DefaultUser
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.functional.map
import com.office14.coffeedose.domain.interactor.UseCase
import com.office14.coffeedose.domain.interactor.UseCaseBase
import com.office14.coffeedose.domain.repository.OrderDetailsRepository
import com.office14.coffeedose.domain.repository.OrdersRepository
import com.office14.coffeedose.domain.repository.PreferencesRepository
import javax.inject.Inject

class RefreshOrderDetailsOnUserLogIn @Inject constructor (private val ordersRepository: OrdersRepository,
                                                          private val orderDetailsRepository: OrderDetailsRepository,
                                                          private val prefsRepository: PreferencesRepository
) : UseCase<UseCaseBase.None, RefreshOrderDetailsOnUserLogIn.Params>() {

    data class Params(val oldEmail: String,val newEmail: String)

    override suspend fun run(params: Params): Either<Failure, None> {
        if (params.newEmail != params.oldEmail && params.newEmail != DefaultUser.DEFAULT_EMAIL){
            val result = ordersRepository.getLastOrderForUserAndPutIntoDB(prefsRepository.getIdToken(),params.newEmail)
            result.fold({},{
                if (params.oldEmail == DefaultUser.DEFAULT_EMAIL){
                    orderDetailsRepository.updateUnattachedOrderDetailsWithEmail(params.newEmail,params.oldEmail)
                }
            })
            return result.map { None() }
        }
        return Either.Right(None())
    }

    fun refreshOrderDetailsByUser() {

        //TODO
        /*cancelJob()

        val oldEmail = email
        email = PreferencesRepositoryImpl.getUserEmail()!!

        viewModelScope.launch {

            if (email != EMPTY_STRING) {

                user = usersRepository.getUserByEmail(email!!)

                ordersRepository.getLastOrderForUserAndPutIntoDB(
                    PreferencesRepositoryImpl.getIdToken(),
                    email!!
                )

                if (oldEmail == EMPTY_STRING) {
                    val unattachedDetailsForUser =
                        orderDetailsRepository.unattachedOrderDetailsForUser(email!!)

                    val unattachedDetailsFree =
                        orderDetailsRepository.unattachedOrderDetailsWithoutUser()

                    if (unattachedDetailsFree.isNotEmpty()) {
                        if (unattachedDetailsForUser.isNotEmpty())
                            orderDetailsRepository.deleteOrderDetailsByEmail(email!!)

                        orderDetailsRepository.updateUnattachedOrderDetailsWithEmail(email!!)
                    }
                }
            } else {
                user = null
            }
        }

        if (email != EMPTY_STRING)
            restartLongPolling()*/
    }

}