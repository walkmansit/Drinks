package com.office14.coffeedose.data.network

import retrofit2.Call
import retrofit2.http.*

interface CoffeeApiService {
    @GET("drinks")
    fun getDrinks(): Call<ResponseContainer<List<CoffeeJso>>>

    @GET("/api/drinks/{drinkId}/sizes")
    fun getSizesByDrinkId(
        @Path(
            value = "drinkId",
            encoded = true
        ) drinkId: Int
    ): Call<ResponseContainer<List<SizeJso>>>

    @GET("add-ins")
    fun getAddins(): Call<ResponseContainer<List<AddinJso>>>

    @GET("/api/user/orders/last")
    @Headers("Cache-Control: no-cache")
    fun getLastOrderForUser(@Header("Authorization") authHeader: String): Call<ResponseContainer<LastOrderJso>>

    @GET("/api/user/orders/last/status")
    @Headers("Cache-Control: no-cache")
    fun getLastOrderStatusForUser(@Header("Authorization") authHeader: String): Call<ResponseContainer<LastOrderStatusJso>>

    @POST("/api/orders")
    fun createOrder(
        @Body body: CreateOrderBody,
        @Header("Authorization") authHeader: String
    ): Call<ResponseContainer<CreateOrderResponseJso>>

    @POST("/api/user/device-tokens/update")
    fun updateFcmDeviceToken(
        @Body body: PostFcmDeviceTokenBody,
        @Header("Authorization") authHeader: String
    ): Call<ResponseContainer<Any>>

    @POST("/api/user/device-tokens/delete")
    fun deleteFcmDeviceToken(
        @Body body: DeleteFcmDeviceTokenBody,
        @Header("Authorization") authHeader: String
    ): Call<ResponseContainer<Any>>
}