package org.youpip.app.model

data class PostModel (
    val userOid:String?,
    val full_name:String,
    val image:String,
    val content:String,
    val post_oid:String,
    val time:String,
    val liked:Boolean
        )