package com.office14.coffeedose.di.orders

import com.office14.coffeedose.di.catalog.CatalogScope
import com.office14.coffeedose.domain.repository.OrderDetailsRepository
import com.office14.coffeedose.domain.repository.OrdersRepository
import com.office14.coffeedose.domain.repository.PreferencesRepository
import com.office14.coffeedose.domain.repository.SizesRepository
import com.office14.coffeedose.domain.usecase.*
import dagger.Module
import dagger.Provides


@Module
abstract class OrdersModule {

    @OrdersScope
    //@Module
    companion object {

        @OrdersScope
        @Provides
        fun provideConfirmOrderUseCase(ordersRepository: OrdersRepository,orderDetailsRepository: OrderDetailsRepository,preferencesRepository: PreferencesRepository)  =
            ConfirmOrder(ordersRepository,orderDetailsRepository,preferencesRepository)

        @OrdersScope
        @Provides
        fun provideClearOrderDetailsUseCase(orderDetailsRepository: OrderDetailsRepository,preferencesRepository: PreferencesRepository)  =
            ClearOrderDetails(orderDetailsRepository,preferencesRepository)

        @OrdersScope
        @Provides
        fun provideDeleteOrderDetailsItemUseCase(orderDetailsRepository: OrderDetailsRepository,preferencesRepository: PreferencesRepository)  =
            DeleteOrderDetailsItem(orderDetailsRepository,preferencesRepository)

        /*@OrdersScope
        @Provides
        fun provideGetUnattachedOrderDetailsUseCase(orderDetailsRepository: OrderDetailsRepository)  =
            GetUnattachedOrderDetails(orderDetailsRepository)*/


    }
}
