package com.isletme.andontv.api

import com.isletme.andontv.model.MachineData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AndonApiService {

    /**
     * TV'nin IP adresine göre makine verilerini çeker
     * Backend'de bu IP ile hangi makinenin verisini döneceğinizi belirleyeceksiniz
     */
    @GET("api/machine-data")
    suspend fun getMachineData(
        @Query("ip") ipAddress: String
    ): Response<MachineData>
}
