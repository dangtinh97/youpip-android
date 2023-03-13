package org.youpip.app.network

import android.os.Build
import me.tatarka.inject.annotations.Inject
import okhttp3.Interceptor
import okhttp3.Response
import org.youpip.app.BuildConfig

class HeaderInterceptor @Inject constructor():Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        request.addHeader("os-name","ANDROID")
        request.addHeader("version-app", BuildConfig.VERSION_NAME)
        request.addHeader("time",System.currentTimeMillis().toString())
        request.addHeader("os-version", Build.VERSION.SDK_INT.toString())
        return chain.proceed(request.build())
    }
}