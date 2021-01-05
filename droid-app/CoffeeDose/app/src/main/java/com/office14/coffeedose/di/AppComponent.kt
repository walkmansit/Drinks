package com.office14.coffeedose.di

import android.app.Application
import com.office14.coffeedose.CoffeeDoseApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ActivityBuildersModule::class,
        ViewModelBuilder::class,
        AppModule::class
    ]
)
interface AppComponent : AndroidInjector<CoffeeDoseApplication> {

    @Component.Factory
    interface Factory {
        // With @BindsInstance, the Context passed in will be available in the graph
        fun create(@BindsInstance app: Application): AppComponent
    }
}