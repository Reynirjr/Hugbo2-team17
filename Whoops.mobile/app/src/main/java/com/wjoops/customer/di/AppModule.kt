package com.wjoops.customer.di

import android.content.Context
import com.wjoops.customer.BuildConfig
import com.wjoops.customer.data.datastore.AppDataStore
import com.wjoops.customer.data.datastore.AuthStore
import com.wjoops.customer.data.datastore.BasketSnapshotStore
import com.wjoops.customer.data.network.AuthInterceptor
import com.wjoops.customer.data.repositories.AuthRepository
import com.wjoops.customer.data.repositories.AuthRepositoryImpl
import com.wjoops.customer.data.repositories.BasketRepository
import com.wjoops.customer.data.repositories.BasketRepositoryImpl
import com.wjoops.customer.data.repositories.MenuRepository
import com.wjoops.customer.data.repositories.MenuRepositoryImpl
import com.wjoops.customer.data.repositories.OrderRepository
import com.wjoops.customer.data.repositories.OrderRepositoryImpl
import com.wjoops.customer.data.repositories.TokenProvider
import com.wjoops.customer.location.FusedLocationService
import com.wjoops.customer.location.LocationService
import com.wjoops.customer.data.network.defaultJson
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class Bindings {
    @Binds abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
    @Binds abstract fun bindTokenProvider(impl: AuthRepositoryImpl): TokenProvider
    @Binds abstract fun bindMenuRepository(impl: MenuRepositoryImpl): MenuRepository
    @Binds abstract fun bindBasketRepository(impl: BasketRepositoryImpl): BasketRepository
    @Binds abstract fun bindOrderRepository(impl: OrderRepositoryImpl): OrderRepository
    @Binds abstract fun bindLocationService(impl: FusedLocationService): LocationService

    @Binds abstract fun bindAuthStore(impl: AppDataStore): AuthStore
    @Binds abstract fun bindBasketSnapshotStore(impl: AppDataStore): BasketSnapshotStore
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideJson(): Json = defaultJson()

    @Provides
    @Singleton
    fun provideAppDataStore(
        @ApplicationContext context: Context,
        json: Json,
    ): AppDataStore = AppDataStore(context, json)

    @Provides
    @Singleton
    fun provideOkHttp(
        tokenProvider: TokenProvider,
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenProvider))
            .addInterceptor(logging)
            .build()
    }

    // Retrofit is wired for later use (not required by Fake repositories yet).
    // TODO: Provide Retrofit + WjoopsApiService and switch repos to use it.
    @Provides
    @Singleton
    fun provideBaseUrl(): String = BuildConfig.BASE_URL
}

