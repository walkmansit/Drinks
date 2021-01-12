package com.office14.coffeedose.data.repository

import com.office14.coffeedose.domain.entity.Addin
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.exception.NetworkHandler
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.interactor.UseCaseBase
import com.office14.coffeedose.domain.repository.AddinsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AddinsRepositoryImpl @Inject constructor(
    private val addinsDatabaseDao: com.office14.coffeedose.data.database.AddinDao,
    private val coffeeApi: com.office14.coffeedose.data.network.CoffeeApiService,
    private  val networkHandler: NetworkHandler
) : BaseRepository(), AddinsRepository {

    override val addins: Flow<Either<Failure, List<Addin>>> = addinsDatabaseDao.getAllAddins().map { list -> Either.Right( list.map { it.toDomainModel() } )}

    override suspend fun refreshAddins() : Either<Failure,UseCaseBase.None> {

        return when (networkHandler.isNetworkAvailable()){
            true -> {
                val sizes = requestApi(coffeeApi.getAddins()) { it.map { addin -> addin.toDataBaseModel() } }

                var result : Either<Failure, UseCaseBase.None> = Either.Right(UseCaseBase.None())
                result = Either.Left(Failure.ServerError())

                sizes.fold({},{ right -> addinsDatabaseDao.refreshAddins(right)})

                return result

            }
            false -> Either.Left(Failure.NetworkConnection())
        }
    }
}