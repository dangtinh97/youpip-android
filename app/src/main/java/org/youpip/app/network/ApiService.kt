package org.youpip.app.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.youpip.app.model.BaseApi
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit

const val TIME_REQUEST: Long = 100
const val BASE_URL = "http://54.254.11.130/api/"
interface ApiService {

    @GET("youtube/new")
    fun home(
        @Query("token") token:String
    ): Observable<BaseApi<Any>>

    companion object{
        private val gson = GsonBuilder().setLenient().create()
        private var headerInterceptor:HeaderInterceptor = HeaderInterceptor()
        private val client = OkHttpClient.Builder()
            .connectTimeout(TIME_REQUEST,TimeUnit.SECONDS)
            .writeTimeout(TIME_REQUEST,TimeUnit.SECONDS)
            .readTimeout(TIME_REQUEST,TimeUnit.SECONDS)
            .addInterceptor(headerInterceptor)
            .build()
        fun getClient():ApiService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(BASE_URL)
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}