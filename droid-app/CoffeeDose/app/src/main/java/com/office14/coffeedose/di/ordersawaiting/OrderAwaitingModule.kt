package com.office14.coffeedose.di.ordersawaiting

import com.google.api.AuthRequirement
import com.office14.coffeedose.di.catalog.CatalogScope
import com.office14.coffeedose.di.orders.OrdersScope
import com.office14.coffeedose.domain.exception.RequireAuthHandler
import com.office14.coffeedose.domain.repository.OrderDetailsRepository
import com.office14.coffeedose.domain.repository.OrdersRepository
import com.office14.coffeedose.domain.repository.PreferencesRepository
import com.office14.coffeedose.domain.repository.UserRepository
import com.office14.coffeedose.domain.usecase.ConfirmOrder
import com.office14.coffeedose.domain.usecase.GetLastOrderInfo
import com.office14.coffeedose.domain.usecase.MarkOrderAsFinishedForUser
import dagger.Module
import dagger.Provides

@Module
abstract class OrderAwaitingModule {

    @OrdersAwaitingScope
    //@Module
    companion object {
        @OrdersAwaitingScope
        @Provides
        fun provideGetLastOrderInfoUseCase(ordersRepository: OrdersRepository, userRepository: UserRepository,authHandler: RequireAuthHandler)  =
            GetLastOrderInfo(ordersRepository,userRepository,authHandler)

        @OrdersAwaitingScope
        @Provides
        fun provideMarkOrderAsFinishedForUserUseCase(ordersRepository: OrdersRepository, preferencesRepository: PreferencesRepository)  =
            MarkOrderAsFinishedForUser(ordersRepository)
    }
}