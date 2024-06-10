package com.lunadev.moviehelper.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ReplyElement(
    val replyId:Long?,
    val content:String,
    val created_at: String?,
    val docId: String? // 영화 고유 ID
)