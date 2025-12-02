package com.donchik.akadeska.domain.model

enum class PostType { INFO, EVENT, LISTING }

data class PostWrite(
    val type: PostType,
    val title: String,
    val body: String,
    val price: Double? = null,         // tylko dla LISTING
    val status: String = "pending",    // POC: wszystko leci jako pending
    val createdBy: String,
    val createdAt: com.google.firebase.Timestamp =
        com.google.firebase.Timestamp.now(),
    val imageUrl: String? = null
)