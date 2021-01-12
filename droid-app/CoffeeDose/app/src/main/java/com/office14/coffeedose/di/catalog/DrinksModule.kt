package com.office14.coffeedose.di.catalog

import com.office14.coffeedose.data.database.CoffeeDatabase
import com.office14.coffeedose.data.network.CoffeeApiService
import com.office14.coffeedose.data.repository.CoffeeRepositoryImpl
import com.office14.coffeedose.domain.exception.NetworkHandler
import com.office14.coffeedose.domain.repository.CoffeeRepository
import com.office14.coffeedose.domain.usecase.GetDrinks
import com.office14.coffeedose.domain.usecase.RefreshDrinks
import dagger.Module
import dagger.Provides

@Module
abstract class DrinksModule {

    @CatalogScope
    //@Module
    companion object {
       /* @CatalogScope
        @Provides
        fun provideIoDispatcher() = Dispatchers.IO*/

        @CatalogScope
        @Provides
        fun provideCoffeeRepository(database: CoffeeDatabase, apiService: CoffeeApiService, networkHandler: NetworkHandler) : CoffeeRepository =
            CoffeeRepositoryImpl(database.drinksDatabaseDao, apiService,networkHandler)

        @CatalogScope
        @Provides
        fun provideGetDrinksUseCase(coffeeRepository: CoffeeRepository) : GetDrinks = GetDrinks(coffeeRepository)

        @CatalogScope
        @Provides
        fun provideRefreshDrinksUseCase(coffeeRepository: CoffeeRepository) : RefreshDrinks = RefreshDrinks(coffeeRepository)

    }
}