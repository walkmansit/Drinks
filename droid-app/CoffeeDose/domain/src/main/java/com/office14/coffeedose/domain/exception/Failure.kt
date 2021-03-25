package com.office14.coffeedose.domain.exception

/**
 * Base Class for handling errors/failures/exceptions.
 * Every feature specific failure should extend [FeatureFailure] class.
 */
sealed class Failure (val message: String, val description : String = "") {
    class NetworkConnection(msg: String = "NetworkConnection") : Failure(msg)
    class ServerError(msg: String = "ServerError") : Failure(msg)
    class DatabaseError(msg: String = "DatabaseError") : Failure(msg)
    class AuthotizationRequired(msg: String = "AuthotizationRequered") : Failure(msg)
    class DomainError(msg: String = "DomainError") : Failure(msg)
    class NoData(msg: String = "NoData") : Failure(msg)

    /** * Extend this class for feature specific failures.*/
    abstract class FeatureFailure: Failure("FeatureFailure")
}