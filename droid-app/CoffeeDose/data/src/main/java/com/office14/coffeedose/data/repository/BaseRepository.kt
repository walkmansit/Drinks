package com.office14.coffeedose.data.repository

import com.office14.coffeedose.data.network.ResponseContainer
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import retrofit2.Call

open class BaseRepository {
    protected fun <T,V,R> requestApi(call: Call<T>, transform: (V) -> R): Either<Failure, R> where T : ResponseContainer<V> {
        return try {
            val response = call.execute()
            when (response.isSuccessful) {
                true -> if (response.body() == null)
                            Either.Left(Failure.ServerError(RESPONSE_BODY_NULL))
                        else
                            transformPayload(response.body()!!,transform)
                false -> Either.Left(if (response.code() == 401)  Failure.AuthotizationRequired(response.message()) else Failure.ServerError(RESPONSE_NOT_SUCCESSFUL))
            }
        } catch (exception: Throwable) {
            Either.Left(Failure.ServerError())
        }
    }

    private fun <T,R> transformPayload(container: ResponseContainer<T>,transform: (T) -> R) : Either<Failure, R> {
        return if (container.hasError()){
            Either.Left(Failure.ServerError(container.title?:"ServerError"))
        }
        else{
            if (container.payload == null)
                Either.Left(Failure.NoData(PAYLOAD_NULL))
            else
                Either.Right(transform.invoke(container.payload))
        }
    }

    protected fun composeAuthHeader(token: String?) = "Bearer $token"

    companion object {
        const val RESPONSE_NOT_SUCCESSFUL = "response not successful"
        const val RESPONSE_BODY_NULL = "response body is null"
        const val PAYLOAD_NULL = "response body is null"
    }

    /*protected fun <T, R> requestDb(call: Call<T>, transform: (T) -> R, default: T): Either<Failure, R> {
        return try {
            val response = call.execute()
            when (response.isSuccessful) {
                true -> Either.Right(transform((response.body() ?: default)))
                false -> Either.Left(Failure.ServerError)
            }
        } catch (exception: Throwable) {
            Either.Left(Failure.ServerError)
        }
    }*/
}