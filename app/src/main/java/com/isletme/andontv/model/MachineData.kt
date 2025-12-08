package com.isletme.andontv.model

import com.google.gson.annotations.SerializedName

data class MachineData(
    @SerializedName("machineName")
    val machineName: String,

    @SerializedName("machineIp")
    val machineIp: String,

    @SerializedName("leftKazan")
    val leftKazan: WorkOrder?,

    @SerializedName("rightKazan")
    val rightKazan: WorkOrder?,

    @SerializedName("currentShift")
    val currentShift: String,

    @SerializedName("timestamp")
    val timestamp: Long
)
