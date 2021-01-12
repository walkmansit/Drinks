package com.office14.coffeedose.data.repository

import com.office14.coffeedose.data.database.CoffeeDao
import com.office14.coffeedose.data.network.CoffeeApiService
import com.office14.coffeedose.domain.entity.Coffee
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.exception.NetworkHandler
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.functional.map
import com.office14.coffeedose.domain.interactor.UseCaseBase
import com.office14.coffeedose.domain.repository.CoffeeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CoffeeRepositoryImpl @Inject constructor(
    private val coffeeDao: CoffeeDao,
    private val coffeeApi: CoffeeApiService,
    private val networkHandler: NetworkHandler
) : BaseRepository(), CoffeeRepository {

    override val drinks: Flow<Either<Failure, List<Coffee>>> = coffeeDao.getAllDrinks().map { list -> Either.Right( list.map { it.toDomainModel() } )}
           /* Transformations.map(coffeeDao.getAllDrinks()) { itDbo ->
        itDbo.map { it.toDomainModel() }
    }*/

    override suspend fun refreshDrinks() : Either<Failure, UseCaseBase.None> {
        return when (networkHandler.isNetworkAvailable()){
            true -> {
                return requestApi(coffeeApi.getDrinks()) {
                    it.map {
                            coffeeJso -> coffeeJso.toDataBaseModel() }
                    }.map {
                        coffeeDao.refreshDrinks(it)
                        return@map UseCaseBase.None()
                    }
            }
            false -> Either.Left(Failure.NetworkConnection())
        }

        /*try {*/
      /*  withContext(ioDispatcher) {
            val drinksResponse = coffeeApi.getDrinksAsync().await()
            if (drinksResponse.hasError())
                throw com.office14.coffeedose.data.network.HttpExceptionEx(drinksResponse.getError())
            else
                coffeeDao.refreshDrinks(drinksResponse.payload!!.map { it.toDataBaseModel() })
        }*/
        /*}
        catch (ex:Exception){
            throw ex
        }*/
    }
}