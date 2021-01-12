package com.office14.coffeedose.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.office14.coffeedose.data.database.CoffeeDatabase
import com.office14.coffeedose.data.network.CoffeeApiService
import com.office14.coffeedose.data.repository.*
import com.office14.coffeedose.di.orders.OrdersScope
import com.office14.coffeedose.plugins.PreferencesRepositoryImpl
import com.office14.coffeedose.domain.exception.NetworkHandler
import com.office14.coffeedose.domain.repository.*
import com.office14.coffeedose.domain.usecase.GetCurrentQueueOrderByUser
import com.office14.coffeedose.networkhandler.NetworkHandlerImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
abstract class AppModule {

    @Singleton
    //@Module
    companion object {

        @Singleton
        @Provides
        fun provideContext(application: Application): Context {
            return application.applicationContext
        }

        @Singleton
        @Provides
        fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

        @Singleton
        @Provides
        fun provideRetrofit(moshi: Moshi): Retrofit {
            return Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .baseUrl(PreferencesRepositoryImpl.getBaseUrl())
                .build()
        }

        @Singleton
        @Provides
        fun provideApiService(retrofit: Retrofit): CoffeeApiService = retrofit.create(CoffeeApiService::class.java)

        @Singleton
        @Provides
        fun provideDataBase(context: Context): CoffeeDatabase {
            return Room.databaseBuilder(
                context,
                CoffeeDatabase::class.java,
                "drinks_database"
            ).fallbackToDestructiveMigration().build()
        }

        @Singleton
        @Provides
        fun provideNetworkHandler(context: Context) : NetworkHandler = NetworkHandlerImpl(context)

        @Singleton
        @Provides
        fun provideIoDispatcher() = Dispatchers.IO

        @Singleton
        @Provides
        fun provideOrderDetailsRepository(database: CoffeeDatabase) : OrderDetailsRepository =
            OrderDetailsRepositoryImpl(database.orderDetailsDatabaseDao)

        @Singleton
        @Provides
        fun provideOrdersRepository(database: CoffeeDatabase, apiService: CoffeeApiService) : OrdersRepository =
            OrdersRepositoryImpl(database.ordersDatabaseDao,apiService)


        @Singleton
        @Provides
        fun provideUsersRepository(database: CoffeeDatabase) : UsersRepository =
            UsersRepositoryImpl(database.usersDatabaseDao)

        @Singleton
        @Provides
        fun providePreferencesRepository() : PreferencesRepository = PreferencesRepositoryImpl

        @Singleton
        @Provides
        fun provideGetCurrentQueueOrderByUserUseCase(ordersRepository: OrdersRepository)  =
            GetCurrentQueueOrderByUser(ordersRepository)
    }
}