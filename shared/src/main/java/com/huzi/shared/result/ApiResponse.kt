package com.huzi.shared.result

import android.util.Log
import com.huzi.shared.parser.ApiErrorParser
import com.huzi.shared.retrofit.NoInternetException
import com.huzi.shared.retrofit.ServerDownException
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.UnknownHostException
import java.security.cert.CertPathValidatorException
import javax.net.ssl.SSLHandshakeException

/**
 * Common class used by API responses.
 * @param <T> the type of the response object
</T> */
@Suppress("unused") // T is used in extending classes
sealed class ApiResponse<T> {

    companion object {
        fun <T> create(error: Throwable): ApiErrorResponse<T> {
            val ex = if (error is IOException) {
                when (error) {
                    is ConnectException -> {
                        val msg = error.cause.toString()
                        when {
                            msg.contains("Network is unreachable", true) -> {
                                NoInternetException("Сеть недоступна", error)
                            }
                            msg.contains("Connection refused", true) -> {
                                ServerDownException("Ошибка сервера", error)
                            }
                            else -> error
                        }
                    }
                    is UnknownHostException -> {
                        NoInternetException("Сеть недоступна", error)
                    }
                    is CertPathValidatorException, is SSLHandshakeException -> {
                        ServerDownException("Истек сертификат сервера", error)
                    }
                    else -> {
                        error
                    }
                }
            } else {
                error
            }
            Log.e("ApiResponse", "create Exception:", ex)
            return ApiErrorResponse(errorMessage = ex.message ?: "Неизвестная ошибка сервера",
                                    errorCode = 500,
                                    ex = ex)
        }

        fun <T> create(response: Response<T>): ApiResponse<T> {
            return if (response.isSuccessful) {
                val body = response.body()
                if (body == null || response.code() == 204) {
                    ApiEmptyResponse()
                } else {
                    ApiSuccessResponse(body = body)
                }
            } else {
                val msg = response.errorBody()?.string()
                val errorMsg = if (msg.isNullOrEmpty()) {
                    response.message()
                } else {
                    msg
                }
                val error = try {
                    val jo = JSONObject(errorMsg)
                    val ms = jo.getString("message")
                    ApiErrorParser.parseError(ms) ?: ms
                } catch (ex: Exception) {
                    errorMsg
                }
                ApiErrorResponse(errorMessage = error ?: "unknown error",
                                 errorCode = response.code())
            }
        }

        // Processes summary tags in the feed.
    }
}

/**
 * separate class for HTTP 204 responses so that we can make ApiSuccessResponse's body non-null.
 */

data class ApiSuccessResponse<T>(val body: T) : ApiResponse<T>()

data class ApiErrorResponse<T>(val errorMessage: String, val errorCode: Int, val ex: Throwable? = null) : ApiResponse<T>()

class ApiEmptyResponse<T> : ApiResponse<T>()
