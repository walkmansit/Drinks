package com.office14.coffeedose.di.orders

import com.office14.coffeedose.domain.exception.RequireAuthHandler
import com.office14.coffeedose.domain.repository.OrderDetailsRepository
import com.office14.coffeedose.domain.repository.OrdersRepository
import com.office14.coffeedose.domain.repository.PreferencesRepository
import com.office14.coffeedose.domain.repository.UserRepository
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
        fun provideConfirmOrderUseCase(ordersRepository: OrdersRepository,orderDetailsRepository: OrderDetailsRepository,preferencesRepository: PreferencesRepository,authHandler: RequireAuthHandler)  =
            ConfirmOrder(ordersRepository,orderDetailsRepository,preferencesRepository,authHandler)

        @OrdersScope
        @Provides
        fun provideClearOrderDetailsUseCase(orderDetailsRepository: OrderDetailsRepository,userRepository: UserRepository)  =
            ClearOrderDetails(orderDetailsRepository,userRepository)

        @OrdersScope
        @Provides
        fun provideDeleteOrderDetailsItemUseCase(orderDetailsRepository: OrderDetailsRepository)  =
            DeleteOrderDetailsItem(orderDetailsRepository)

        /*@OrdersScope
        @Provides
        fun provideGetUnattachedOrderDetailsUseCase(orderDetailsRepository: OrderDetailsRepository)  =
            GetUnattachedOrderDetails(orderDetailsRepository)*/


    }
}
