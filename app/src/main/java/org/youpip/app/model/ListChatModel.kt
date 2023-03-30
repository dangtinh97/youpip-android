package org.youpip.app.model

data class ListChatModel (
    val roomOid:String,
    val fullName:String,
    val message:String,
    val time:String,
    val userId:Int
        )