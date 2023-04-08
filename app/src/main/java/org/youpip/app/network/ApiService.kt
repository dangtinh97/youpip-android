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
const val BASE_URL = "https://youpip.net/api/"
const val SOCKET_URL = "youpip.net"
interface ApiService {

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("username") username: String
    ): Observable<BaseApi<Any>>

    @GET("youtube/new")
    fun home(
        @Header("Authorization") token: String,
        @Query("type") type:String,
        @Query("last_oid") lastOid:String
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

    @GET("youtube/suggest-by-video-id")
    fun suggestByVideoId(
        @Header("Authorization") token: String,
        @Query("video-id") videoId :String
    ): Observable<BaseApi<Any>>

    @GET("posts")
    fun posts(
        @Header("Authorization") token: String,
        @Query("post_last_oid") lastPostOid :String?
    ): Observable<BaseApi<Any>>

    @GET("chats/join-room")
    fun joinRoom(
        @Header("Authorization") token: String,
        @Query("user_oid") userOid :String?
    ): Observable<BaseApi<Any>>

    @GET("posts/feed")
    fun feed(
        @Header("Authorization") token: String,
        @Query("post_last_oid") lastPostOid :String?
    ): Observable<BaseApi<Any>>

    @FormUrlEncoded
    @POST("attachment")
    fun uploadFile(
        @Header("Authorization") token: String,
        @Field("file") file :String
    ): Observable<BaseApi<Any>>

    @DELETE("posts/{id}")
    fun deletePost(
        @Header("Authorization") token: String,
        @Path("id") file :String
    ): Observable<BaseApi<Any>>

    @FormUrlEncoded
    @POST("posts")
    fun createPost(
        @Header("Authorization") token: String,
        @Field("attachment_id") attachmentId: String,
        @Field("content") content: String
    ): Observable<BaseApi<Any>>

    @FormUrlEncoded
    @POST("posts/{id}/reaction")
    fun reaction(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Field("action") action: String
    ): Observable<BaseApi<Any>>

    @FormUrlEncoded
    @POST("posts/{id}/comment")
    fun comment(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Field("content") content: String
    ): Observable<BaseApi<Any>>

    @FormUrlEncoded
    @POST("chats/{id}")
    fun sendMessage(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Field("message") message: String
    ): Observable<BaseApi<Any>>

    @GET("posts/{id}/comment")
    fun getComment(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Query("last_comment_oid") lastCommentOid: String
    ): Observable<BaseApi<Any>>


    @GET("chats/search-user")
    fun searchUser(
        @Header("Authorization") token: String,
        @Query("username") username: String
    ): Observable<BaseApi<Any>>


    @GET("chats")
    fun listChat(
        @Header("Authorization") token: String,
        @Query("last_oid") id: String
    ): Observable<BaseApi<Any>>

    @GET("chats/{id}")
    fun message(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Query("last_oid") lastOid: String,
    ): Observable<BaseApi<Any>>

    @GET("youtube/detail")
    fun detail(
        @Header("Authorization") token: String,
        @Query("url") url: String
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