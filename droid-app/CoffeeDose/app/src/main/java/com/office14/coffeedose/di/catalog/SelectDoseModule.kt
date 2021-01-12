package com.office14.coffeedose.di.catalog

import com.office14.coffeedose.data.database.CoffeeDatabase
import com.office14.coffeedose.data.network.CoffeeApiService
import com.office14.coffeedose.data.repository.AddinsRepositoryImpl
import com.office14.coffeedose.data.repository.SizesRepositoryImpl
import com.office14.coffeedose.domain.exception.NetworkHandler
import com.office14.coffeedose.domain.repository.AddinsRepository
import com.office14.coffeedose.domain.repository.CoffeeRepository
import com.office14.coffeedose.domain.repository.OrderDetailsRepository
import com.office14.coffeedose.domain.repository.SizesRepository
import com.office14.coffeedose.domain.usecase.*
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
abstract class SelectDoseModule {

    @CatalogScope
    //@Module
    companion object {

       /* @CatalogScope
        @Provides
        fun provideIoDispatcher() = Dispatchers.IO*/

        @CatalogScope
        @Provides
        fun provideSizesRepository(database: CoffeeDatabase, apiService: CoffeeApiService, networkHandler: NetworkHandler) : SizesRepository =
            SizesRepositoryImpl(database.sizeDatabaseDao, apiService, networkHandler)

        @CatalogScope
        @Provides
        fun provideAddinsRepository(database: CoffeeDatabase, apiService: CoffeeApiService, networkHandler: NetworkHandler) : AddinsRepository =
            AddinsRepositoryImpl(database.addinsDatabaseDao, apiService,networkHandler)

        @CatalogScope
        @Provides
        fun provideGetSizesUseCase(sizesRepository: SizesRepository)  = GetSizes(sizesRepository)

        @CatalogScope
        @Provides
        fun provideGetAddinsUseCase(addinsRepository: AddinsRepository)  = GetAddins(addinsRepository)

        @CatalogScope
        @Provides
        fun provideRefreshSizesUseCase(sizesRepository: SizesRepository)  = RefreshSizes(sizesRepository)

        @CatalogScope
        @Provides
        fun provideRefreshAddinsUseCase(addinsRepository: AddinsRepository)  = RefreshAddins(addinsRepository)

        @CatalogScope
        @Provides
        fun provideGetSummaryPriceUseCase()  = GetSummaryPrice()

        @CatalogScope
        @Provides
        fun provideMergeOrderDetailsInUseCase(orderDetailsRepository: OrderDetailsRepository)  = MergeOrderDetailsIn(orderDetailsRepository)
    }

}