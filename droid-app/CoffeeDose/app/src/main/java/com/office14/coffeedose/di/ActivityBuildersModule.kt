package com.office14.coffeedose.di

import com.office14.coffeedose.data.database.CoffeeDatabase
import com.office14.coffeedose.data.network.CoffeeApiService
import com.office14.coffeedose.data.repository.CoffeeRepositoryImpl
import com.office14.coffeedose.di.catalog.*
import com.office14.coffeedose.di.orders.OrdersModule
import com.office14.coffeedose.di.orders.OrdersScope
import com.office14.coffeedose.di.orders.OrdersVMModule
import com.office14.coffeedose.di.ordersawaiting.OrderAwaitingVMModule
import com.office14.coffeedose.di.ordersawaiting.OrdersAwaitingScope
import com.office14.coffeedose.domain.exception.NetworkHandler
import com.office14.coffeedose.domain.repository.CoffeeRepository
import com.office14.coffeedose.ui.*
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import java.security.AuthProvider
import javax.inject.Singleton

@Module
abstract class ActivityBuildersModule {

    //@Singleton
    @ContributesAndroidInjector(modules = [ActivityVMModule::class, ActivityBuildersSubModule::class])
    abstract fun contributeCoffeeDoseActivity(): CoffeeDoseActivity

}


@Module
abstract class ActivityBuildersSubModule {

    @CatalogScope
    @ContributesAndroidInjector(modules = [DrinksVMModule::class, DrinksModule::class])
    abstract fun contributeDrinksFragment(): DrinksFragment

    @CatalogScope
    @ContributesAndroidInjector(modules = [SelectDoseVMModule::class, SelectDoseModule::class])
    abstract fun contributeSelectDoseAndAddinsFragment(): SelectDoseAndAddinsFragment

    @OrdersScope
    @ContributesAndroidInjector(modules = [OrdersVMModule::class, OrdersModule::class])
    abstract fun contributeOrderDetailsFragment(): OrderDetailsFragment

    @OrdersAwaitingScope
    @ContributesAndroidInjector(modules = [OrderAwaitingVMModule::class])
    abstract fun contributeOrderAwaitingFragment(): OrderAwaitingFragment

   /* @Singleton
    companion object {
        @Singleton
        @Provides
        fun provideAuthProvider(activity: CoffeeDoseActivity): AuthProvider = activity as AuthProvider
    }*/
}
