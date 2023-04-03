package org.youpip.app.model

data class MessageModel (
    val lastOid:String?,
    val message:String,
    val me:Boolean,
    val time:Int?
        )