package com.isletme.andontv

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.isletme.andontv.model.WorkOrder
import com.isletme.andontv.utils.DateTimeUtils
import com.isletme.andontv.utils.MockDataProvider
import com.isletme.andontv.utils.NetworkUtils
import com.isletme.andontv.viewmodel.AndonViewModel

class MainActivity : AppCompatActivity() {

    // TEST MODU: Backend hazır olmadığında true yapın
    private val TEST_MODE = true

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

        // Test modu veya gerçek veri
        if (TEST_MODE) {
            // Test modu: Dummy data göster
            loadMockData()
        } else {
            // Gerçek mod: Backend'den veri çek
            viewModel.startAutoRefresh(this)
        }
    }

    /**
     * Test için dummy data yükler
     * Backend hazır olmadığında ekranın nasıl göründüğünü test etmek için
     */
    private fun loadMockData() {
        // Farklı senaryolar için mock data'ları değiştirebilirsiniz:

        // Senaryo 1: Her iki kazanda da iş emri var
        val mockData = MockDataProvider.getMockMachineDataBothActive()

        // Senaryo 2: Sadece sol kazanda iş emri var
        // val mockData = MockDataProvider.getMockMachineDataLeftOnly()

        // Senaryo 3: Her iki kazan da boş
        // val mockData = MockDataProvider.getMockMachineDataEmpty()

        // Senaryo 4: Çok tedarikçili test
        // val mockData = MockDataProvider.getMockMachineDataMultipleSuppliers()

        // UI'ı güncelle
        tvMachineName.text = mockData.machineName
        tvShift.text = "Vardiya: ${mockData.currentShift}"

        // Sol kazan güncelle
        updateKazan(
            workOrder = mockData.leftKazan,
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
            workOrder = mockData.rightKazan,
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

    private fun setupFullscreenMode() {
        // ActionBar'ı gizle
        supportActionBar?.hide()

        // Android 11 (API 30) ve üzeri için yeni API
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // Android 10 ve altı için eski API (suppress deprecation warning)
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            )
        }
    }

    private fun bindViews() {
        // Header
        tvMachineName = findViewById(R.id.tvMachineName)
        tvIpAddress = findViewById(R.id.tvIpAddress)

        // Left Kazan - Include ID'si direkt olarak root LinearLayout'a işaret eder
        leftKazanContainer = findViewById(R.id.leftKazanLayout)
        leftKazanContainer.findViewById<TextView>(R.id.tvKazanTitle).text = getString(R.string.left_kazan)
        leftTvWorkOrderNumber = leftKazanContainer.findViewById(R.id.tvWorkOrderNumber)
        leftTvNoWorkOrder = leftKazanContainer.findViewById(R.id.tvNoWorkOrder)
        leftScrollView = leftKazanContainer.findViewById(R.id.scrollViewCards)

        // Left Kazan - Card view'ları
        val leftSupplierCard = leftKazanContainer.findViewById<View>(R.id.supplierCard)
        leftSupplierTitle = leftSupplierCard.findViewById(R.id.tvCardTitle)
        leftSupplierValue = leftSupplierCard.findViewById(R.id.tvCardValue)

        val leftBaleCountCard = leftKazanContainer.findViewById<View>(R.id.baleCountCard)
        leftBaleCountTitle = leftBaleCountCard.findViewById(R.id.tvCardTitle)
        leftBaleCountValue = leftBaleCountCard.findViewById(R.id.tvCardValue)

        val leftTotalWeightCard = leftKazanContainer.findViewById<View>(R.id.totalWeightCard)
        leftTotalWeightTitle = leftTotalWeightCard.findViewById(R.id.tvCardTitle)
        leftTotalWeightValue = leftTotalWeightCard.findViewById(R.id.tvCardValue)

        val leftLastBaleCard = leftKazanContainer.findViewById<View>(R.id.lastBaleCard)
        leftLastBaleTitle = leftLastBaleCard.findViewById(R.id.tvCardTitle)
        leftLastBaleValue = leftLastBaleCard.findViewById(R.id.tvCardValue)

        val leftShiftBaleCard = leftKazanContainer.findViewById<View>(R.id.shiftBaleCountCard)
        leftShiftBaleTitle = leftShiftBaleCard.findViewById(R.id.tvCardTitle)
        leftShiftBaleValue = leftShiftBaleCard.findViewById(R.id.tvCardValue)

        // Right Kazan - Include ID'si direkt olarak root LinearLayout'a işaret eder
        rightKazanContainer = findViewById(R.id.rightKazanLayout)
        rightKazanContainer.findViewById<TextView>(R.id.tvKazanTitle).text = getString(R.string.right_kazan)
        rightTvWorkOrderNumber = rightKazanContainer.findViewById(R.id.tvWorkOrderNumber)
        rightTvNoWorkOrder = rightKazanContainer.findViewById(R.id.tvNoWorkOrder)
        rightScrollView = rightKazanContainer.findViewById(R.id.scrollViewCards)

        // Right Kazan - Card view'ları
        val rightSupplierCard = rightKazanContainer.findViewById<View>(R.id.supplierCard)
        rightSupplierTitle = rightSupplierCard.findViewById(R.id.tvCardTitle)
        rightSupplierValue = rightSupplierCard.findViewById(R.id.tvCardValue)

        val rightBaleCountCard = rightKazanContainer.findViewById<View>(R.id.baleCountCard)
        rightBaleCountTitle = rightBaleCountCard.findViewById(R.id.tvCardTitle)
        rightBaleCountValue = rightBaleCountCard.findViewById(R.id.tvCardValue)

        val rightTotalWeightCard = rightKazanContainer.findViewById<View>(R.id.totalWeightCard)
        rightTotalWeightTitle = rightTotalWeightCard.findViewById(R.id.tvCardTitle)
        rightTotalWeightValue = rightTotalWeightCard.findViewById(R.id.tvCardValue)

        val rightLastBaleCard = rightKazanContainer.findViewById<View>(R.id.lastBaleCard)
        rightLastBaleTitle = rightLastBaleCard.findViewById(R.id.tvCardTitle)
        rightLastBaleValue = rightLastBaleCard.findViewById(R.id.tvCardValue)

        val rightShiftBaleCard = rightKazanContainer.findViewById<View>(R.id.shiftBaleCountCard)
        rightShiftBaleTitle = rightShiftBaleCard.findViewById(R.id.tvCardTitle)
        rightShiftBaleValue = rightShiftBaleCard.findViewById(R.id.tvCardValue)

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
