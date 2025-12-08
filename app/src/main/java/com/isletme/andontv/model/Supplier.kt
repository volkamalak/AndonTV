package com.isletme.andontv.model

import com.google.gson.annotations.SerializedName

data class Supplier(
    @SerializedName("supplierName")
    val supplierName: String,

    @SerializedName("supplierCode")
    val supplierCode: String? = null
)
