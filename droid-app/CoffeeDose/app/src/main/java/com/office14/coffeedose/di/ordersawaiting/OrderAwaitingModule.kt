package com.office14.coffeedose.di.ordersawaiting

import com.office14.coffeedose.di.catalog.CatalogScope
import com.office14.coffeedose.di.orders.OrdersScope
import com.office14.coffeedose.domain.repository.OrderDetailsRepository
import com.office14.coffeedose.domain.repository.OrdersRepository
import com.office14.coffeedose.domain.repository.PreferencesRepository
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
        fun provideGetLastOrderInfoUseCase(ordersRepository: OrdersRepository, preferencesRepository: PreferencesRepository)  =
            GetLastOrderInfo(ordersRepository,preferencesRepository)

        @OrdersAwaitingScope
        @Provides
        fun provideMarkOrderAsFinishedForUserUseCase(ordersRepository: OrdersRepository, preferencesRepository: PreferencesRepository)  =
            MarkOrderAsFinishedForUser(ordersRepository)
    }
}