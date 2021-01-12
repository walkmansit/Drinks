package com.office14.coffeedose.data.repository

import com.office14.coffeedose.domain.entity.CoffeeSize
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.exception.NetworkHandler
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.interactor.UseCaseBase
import com.office14.coffeedose.domain.repository.SizesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SizesRepositoryImpl @Inject constructor(
    private val sizesDao: com.office14.coffeedose.data.database.SizeDao,
    private val coffeeApi: com.office14.coffeedose.data.network.CoffeeApiService,
    private val networkHandler : NetworkHandler
) : BaseRepository(), SizesRepository {

    override fun getSizes(drinkId: Int) : Flow<Either<Failure,List<CoffeeSize>>> = sizesDao.getSizesByDrinkId(drinkId).map { list -> Either.Right( list.map { it.toDomainModel() }) }

    override suspend fun refreshSizes(drinkId: Int) : Either<Failure, UseCaseBase.None> {

        return when (networkHandler.isNetworkAvailable()){
            true -> {
                val sizes = requestApi(coffeeApi.getSizesByDrinkId(drinkId)) { it.map { size -> size.toDatabaseModel(drinkId) } }

                var result : Either<Failure, UseCaseBase.None> = Either.Right(UseCaseBase.None())
                result = Either.Left(Failure.ServerError())

                sizes.fold({},{ right -> sizesDao.refreshSizes(right)})

                return result

            }
            false -> Either.Left(Failure.NetworkConnection())
        }
    }
}