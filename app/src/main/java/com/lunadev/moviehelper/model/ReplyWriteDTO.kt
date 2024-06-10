package com.lunadev.moviehelper.model

import kotlinx.serialization.Serializable

@Serializable
data class ReplyWriteDTO(
    val content:String,
    val docId: String?
)