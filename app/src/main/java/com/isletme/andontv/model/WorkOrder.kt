package com.isletme.andontv.model

import com.google.gson.annotations.SerializedName

data class WorkOrder(
    @SerializedName("workOrderNumber")
    val workOrderNumber: String,

    @SerializedName("isActive")
    val isActive: Boolean,

    @SerializedName("suppliers")
    val suppliers: List<Supplier>,

    @SerializedName("baleCount")
    val baleCount: Int,

    @SerializedName("totalWeight")
    val totalWeight: Double,

    @SerializedName("lastBaleNumber")
    val lastBaleNumber: String,

    @SerializedName("shiftBaleCount")
    val shiftBaleCount: Int
)
