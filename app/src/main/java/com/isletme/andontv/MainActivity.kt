package com.isletme.andontv

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.isletme.andontv.model.WorkOrder
import com.isletme.andontv.utils.DateTimeUtils
import com.isletme.andontv.utils.NetworkUtils
import com.isletme.andontv.viewmodel.AndonViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: AndonViewModel
    private val timeUpdateHandler = Handler(Looper.getMainLooper())

    // Header Views
    private lateinit var tvMachineName: TextView
    private lateinit var tvIpAddress: TextView

    // Left Kazan Views
    private lateinit var leftKazanContainer: LinearLayout
    private lateinit var leftTvWorkOrderNumber: TextView
    private lateinit var leftTvNoWorkOrder: TextView
    private lateinit var leftScrollView: View
    private lateinit var leftSupplierTitle: TextView
    private lateinit var leftSupplierValue: TextView
    private lateinit var leftBaleCountTitle: TextView
    private lateinit var leftBaleCountValue: TextView
    private lateinit var leftTotalWeightTitle: TextView
    private lateinit var leftTotalWeightValue: TextView
    private lateinit var leftLastBaleTitle: TextView
    private lateinit var leftLastBaleValue: TextView
    private lateinit var leftShiftBaleTitle: TextView
    private lateinit var leftShiftBaleValue: TextView

    // Right Kazan Views
    private lateinit var rightKazanContainer: LinearLayout
    private lateinit var rightTvWorkOrderNumber: TextView
    private lateinit var rightTvNoWorkOrder: TextView
    private lateinit var rightScrollView: View
    private lateinit var rightSupplierTitle: TextView
    private lateinit var rightSupplierValue: TextView
    private lateinit var rightBaleCountTitle: TextView
    private lateinit var rightBaleCountValue: TextView
    private lateinit var rightTotalWeightTitle: TextView
    private lateinit var rightTotalWeightValue: TextView
    private lateinit var rightLastBaleTitle: TextView
    private lateinit var rightLastBaleValue: TextView
    private lateinit var rightShiftBaleTitle: TextView
    private lateinit var rightShiftBaleValue: TextView

    // Footer Views
    private lateinit var tvShift: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvTime: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ekranı sürekli açık tut
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Tam ekran modunu ayarla
        setupFullscreenMode()

        // View'ları bağla
        bindViews()

        // ViewModel'i başlat
        viewModel = ViewModelProvider(this)[AndonViewModel::class.java]

        // Observer'ları kur
        setupObservers()

        // IP adresini göster
        displayIpAddress()

        // Saat güncellemesini başlat
        startTimeUpdate()

        // İlk veri çekimini yap ve otomatik yenilemeyi başlat
        viewModel.startAutoRefresh(this)
    }

    private fun setupFullscreenMode() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
        actionBar?.hide()
    }

    private fun bindViews() {
        // Header
        tvMachineName = findViewById(R.id.tvMachineName)
        tvIpAddress = findViewById(R.id.tvIpAddress)

        // Left Kazan
        val leftLayout = findViewById<View>(R.id.leftKazanLayout)
        leftKazanContainer = leftLayout.findViewById(R.id.kazanContainer)
        leftLayout.findViewById<TextView>(R.id.tvKazanTitle).text = getString(R.string.left_kazan)
        leftTvWorkOrderNumber = leftLayout.findViewById(R.id.tvWorkOrderNumber)
        leftTvNoWorkOrder = leftLayout.findViewById(R.id.tvNoWorkOrder)
        leftScrollView = leftLayout.findViewById(R.id.scrollViewCards)

        leftSupplierTitle = leftLayout.findViewById<View>(R.id.supplierCard).findViewById(R.id.tvCardTitle)
        leftSupplierValue = leftLayout.findViewById<View>(R.id.supplierCard).findViewById(R.id.tvCardValue)
        leftBaleCountTitle = leftLayout.findViewById<View>(R.id.baleCountCard).findViewById(R.id.tvCardTitle)
        leftBaleCountValue = leftLayout.findViewById<View>(R.id.baleCountCard).findViewById(R.id.tvCardValue)
        leftTotalWeightTitle = leftLayout.findViewById<View>(R.id.totalWeightCard).findViewById(R.id.tvCardTitle)
        leftTotalWeightValue = leftLayout.findViewById<View>(R.id.totalWeightCard).findViewById(R.id.tvCardValue)
        leftLastBaleTitle = leftLayout.findViewById<View>(R.id.lastBaleCard).findViewById(R.id.tvCardTitle)
        leftLastBaleValue = leftLayout.findViewById<View>(R.id.lastBaleCard).findViewById(R.id.tvCardValue)
        leftShiftBaleTitle = leftLayout.findViewById<View>(R.id.shiftBaleCountCard).findViewById(R.id.tvCardTitle)
        leftShiftBaleValue = leftLayout.findViewById<View>(R.id.shiftBaleCountCard).findViewById(R.id.tvCardValue)

        // Right Kazan
        val rightLayout = findViewById<View>(R.id.rightKazanLayout)
        rightKazanContainer = rightLayout.findViewById(R.id.kazanContainer)
        rightLayout.findViewById<TextView>(R.id.tvKazanTitle).text = getString(R.string.right_kazan)
        rightTvWorkOrderNumber = rightLayout.findViewById(R.id.tvWorkOrderNumber)
        rightTvNoWorkOrder = rightLayout.findViewById(R.id.tvNoWorkOrder)
        rightScrollView = rightLayout.findViewById(R.id.scrollViewCards)

        rightSupplierTitle = rightLayout.findViewById<View>(R.id.supplierCard).findViewById(R.id.tvCardTitle)
        rightSupplierValue = rightLayout.findViewById<View>(R.id.supplierCard).findViewById(R.id.tvCardValue)
        rightBaleCountTitle = rightLayout.findViewById<View>(R.id.baleCountCard).findViewById(R.id.tvCardTitle)
        rightBaleCountValue = rightLayout.findViewById<View>(R.id.baleCountCard).findViewById(R.id.tvCardValue)
        rightTotalWeightTitle = rightLayout.findViewById<View>(R.id.totalWeightCard).findViewById(R.id.tvCardTitle)
        rightTotalWeightValue = rightLayout.findViewById<View>(R.id.totalWeightCard).findViewById(R.id.tvCardValue)
        rightLastBaleTitle = rightLayout.findViewById<View>(R.id.lastBaleCard).findViewById(R.id.tvCardTitle)
        rightLastBaleValue = rightLayout.findViewById<View>(R.id.lastBaleCard).findViewById(R.id.tvCardValue)
        rightShiftBaleTitle = rightLayout.findViewById<View>(R.id.shiftBaleCountCard).findViewById(R.id.tvCardTitle)
        rightShiftBaleValue = rightLayout.findViewById<View>(R.id.shiftBaleCountCard).findViewById(R.id.tvCardValue)

        // Footer
        tvShift = findViewById(R.id.tvShift)
        tvDate = findViewById(R.id.tvDate)
        tvTime = findViewById(R.id.tvTime)

        // Kart başlıklarını ayarla
        setupCardTitles()
    }

    private fun setupCardTitles() {
        // Left Kazan
        leftSupplierTitle.text = getString(R.string.supplier)
        leftBaleCountTitle.text = getString(R.string.bale_count)
        leftTotalWeightTitle.text = getString(R.string.total_weight)
        leftLastBaleTitle.text = getString(R.string.last_bale)
        leftShiftBaleTitle.text = getString(R.string.shift_bale_count)

        // Right Kazan
        rightSupplierTitle.text = getString(R.string.supplier)
        rightBaleCountTitle.text = getString(R.string.bale_count)
        rightTotalWeightTitle.text = getString(R.string.total_weight)
        rightLastBaleTitle.text = getString(R.string.last_bale)
        rightShiftBaleTitle.text = getString(R.string.shift_bale_count)
    }

    private fun setupObservers() {
        viewModel.machineData.observe(this) { machineData ->
            machineData?.let {
                tvMachineName.text = it.machineName
                tvShift.text = "Vardiya: ${it.currentShift}"

                // Sol kazan güncelle
                updateKazan(
                    workOrder = it.leftKazan,
                    container = leftKazanContainer,
                    tvWorkOrder = leftTvWorkOrderNumber,
                    tvNoWorkOrder = leftTvNoWorkOrder,
                    scrollView = leftScrollView,
                    supplierValue = leftSupplierValue,
                    baleCountValue = leftBaleCountValue,
                    totalWeightValue = leftTotalWeightValue,
                    lastBaleValue = leftLastBaleValue,
                    shiftBaleValue = leftShiftBaleValue
                )

                // Sağ kazan güncelle
                updateKazan(
                    workOrder = it.rightKazan,
                    container = rightKazanContainer,
                    tvWorkOrder = rightTvWorkOrderNumber,
                    tvNoWorkOrder = rightTvNoWorkOrder,
                    scrollView = rightScrollView,
                    supplierValue = rightSupplierValue,
                    baleCountValue = rightBaleCountValue,
                    totalWeightValue = rightTotalWeightValue,
                    lastBaleValue = rightLastBaleValue,
                    shiftBaleValue = rightShiftBaleValue
                )
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                // Hata durumunda log tutabilir veya gösterebilirsiniz
                android.util.Log.e("AndonTV", "Error: $it")
            }
        }
    }

    private fun updateKazan(
        workOrder: WorkOrder?,
        container: LinearLayout,
        tvWorkOrder: TextView,
        tvNoWorkOrder: TextView,
        scrollView: View,
        supplierValue: TextView,
        baleCountValue: TextView,
        totalWeightValue: TextView,
        lastBaleValue: TextView,
        shiftBaleValue: TextView
    ) {
        if (workOrder == null) {
            // İş emri yok
            tvWorkOrder.visibility = View.GONE
            scrollView.visibility = View.GONE
            tvNoWorkOrder.visibility = View.VISIBLE
            container.setBackgroundColor(ContextCompat.getColor(this, R.color.inactive_kazan))
        } else {
            // İş emri var
            tvNoWorkOrder.visibility = View.GONE
            tvWorkOrder.visibility = View.VISIBLE
            scrollView.visibility = View.VISIBLE
            tvWorkOrder.text = "İŞ EMRİ: ${workOrder.workOrderNumber}"

            // Aktif kazan arka plan rengi
            if (workOrder.isActive) {
                container.setBackgroundColor(ContextCompat.getColor(this, R.color.active_kazan))
            } else {
                container.setBackgroundColor(ContextCompat.getColor(this, R.color.inactive_kazan))
            }

            // Tedarikçi bilgileri (birden fazla olabilir)
            val supplierNames = workOrder.suppliers.joinToString("\n") { it.supplierName }
            supplierValue.text = supplierNames

            // Diğer bilgiler
            baleCountValue.text = "${workOrder.baleCount} ${getString(R.string.adet)}"
            totalWeightValue.text = "${workOrder.totalWeight} ${getString(R.string.kg)}"
            lastBaleValue.text = workOrder.lastBaleNumber
            shiftBaleValue.text = "${workOrder.shiftBaleCount} ${getString(R.string.adet)}"
        }
    }

    private fun displayIpAddress() {
        val ipAddress = NetworkUtils.getLocalIpAddress(this)
        tvIpAddress.text = "IP: $ipAddress"
    }

    private fun startTimeUpdate() {
        val updateTimeRunnable = object : Runnable {
            override fun run() {
                tvDate.text = DateTimeUtils.getCurrentDate()
                tvTime.text = DateTimeUtils.getCurrentTime()
                timeUpdateHandler.postDelayed(this, 1000) // Her saniye güncelle
            }
        }
        timeUpdateHandler.post(updateTimeRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        timeUpdateHandler.removeCallbacksAndMessages(null)
        viewModel.stopAutoRefresh()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setupFullscreenMode()
        }
    }
}
