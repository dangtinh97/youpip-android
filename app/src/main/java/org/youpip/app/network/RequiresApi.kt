package org.youpip.app.network

import android.annotation.SuppressLint
import android.content.Context
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
class RequiresApi {
    companion object{
        @SuppressLint("CheckResult")
        fun <T> callApi(
            context: Context,
            observable: Observable<T>,
            callbackResponse: (response: T?) -> Unit
        ) {
            observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        println("====>ApiSuccess:${it}")
                        callbackResponse.invoke(it)
                    },
                    {
                        println("====>ApiError:${it}")
                        callbackResponse.invoke(null)
                    }
                )
        }
    }
}