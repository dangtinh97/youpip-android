package org.youpip.app.model

data class CommentModel (
    val comment_oid:String,
    val content:String,
    val userId:Int,
    val fullName:String
        )