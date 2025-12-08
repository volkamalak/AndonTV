package com.isletme.andontv.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isletme.andontv.api.RetrofitClient
import com.isletme.andontv.model.MachineData
import com.isletme.andontv.utils.NetworkUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AndonViewModel : ViewModel() {

    private val _machineData = MutableLiveData<MachineData?>()
    val machineData: LiveData<MachineData?> = _machineData

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var isAutoRefreshActive = false

    /**
     * Makine verilerini API'den çeker
     */
    fun fetchMachineData(context: Context) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val ipAddress = NetworkUtils.getLocalIpAddress(context)

                if (ipAddress == "IP Bulunamadı") {
                    _errorMessage.value = "IP adresi alınamadı"
                    _isLoading.value = false
                    return@launch
                }

                val response = RetrofitClient.apiService.getMachineData(ipAddress)

                if (response.isSuccessful && response.body() != null) {
                    _machineData.value = response.body()
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "Veri alınamadı: ${response.code()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Hata: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 30 saniyede bir otomatik yenileme başlatır
     */
    fun startAutoRefresh(context: Context) {
        if (isAutoRefreshActive) return

        isAutoRefreshActive = true
        viewModelScope.launch {
            while (isAutoRefreshActive) {
                fetchMachineData(context)
                delay(30000) // 30 saniye bekle
            }
        }
    }

    /**
     * Otomatik yenilemeyi durdurur
     */
    fun stopAutoRefresh() {
        isAutoRefreshActive = false
    }

    override fun onCleared() {
        super.onCleared()
        stopAutoRefresh()
    }
}
