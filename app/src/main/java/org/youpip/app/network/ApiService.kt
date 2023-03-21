package org.youpip.app.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.youpip.app.model.BaseApi
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*
import java.util.concurrent.TimeUnit

const val TIME_REQUEST: Long = 100
const val BASE_URL = "http://54.254.11.130/api/"
interface ApiService {

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("username") username: String
    ): Observable<BaseApi<Any>>

    @GET("youtube/new")
    fun home(
        @Header("Authorization") token: String,
    ): Observable<BaseApi<Any>>

    @GET("youtube/search")
    fun search(
        @Header("Authorization") token: String,
        @Query("q") q:String
    ): Observable<BaseApi<Any>>

    @GET("youtube/link-video")
    fun findLink(
        @Header("Authorization") token: String,
        @Query("video-id") videoId:String
    ): Observable<BaseApi<Any>>

    @GET("youtube/suggest")
    fun suggest(
        @Header("Authorization") token: String,
        @Query("keyword") keyword:String
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