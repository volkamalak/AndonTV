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
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import androidx.lifecycle.ViewModelProvider
import com.isletme.andontv.model.WorkOrder
import com.isletme.andontv.utils.DateTimeUtils
import com.isletme.andontv.utils.MockDataProvider
import com.isletme.andontv.utils.NetworkUtils
import com.isletme.andontv.viewmodel.AndonViewModel

class MainActivity : AppCompatActivity() {

    private val TEST_MODE = true
    private val ENABLE_SECONDARY_SCREEN = true

    private lateinit var viewModel: AndonViewModel
    private val timeUpdateHandler = Handler(Looper.getMainLooper())
    private val screenToggleHandler = Handler(Looper.getMainLooper())
    private var isMainScreenVisible = true

    private lateinit var rootLayout: ConstraintLayout
    private lateinit var footerLayout: LinearLayout
    private lateinit var contentFlipper: ViewFlipper


    private lateinit var tvMachineName: TextView
    private lateinit var tvIpAddress: TextView


    private lateinit var leftKazanContainer: ConstraintLayout
    private lateinit var leftTvWorkOrderNumber: TextView
    private lateinit var leftTvNoWorkOrder: TextView
    private lateinit var leftCardsContainer: LinearLayout
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


    private lateinit var rightKazanContainer: ConstraintLayout
    private lateinit var rightTvWorkOrderNumber: TextView
    private lateinit var rightTvNoWorkOrder: TextView
    private lateinit var rightCardsContainer: LinearLayout
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


    private lateinit var singleKazanContainer: ConstraintLayout
    private lateinit var singleTvWorkOrderNumber: TextView
    private lateinit var singleTvNoWorkOrder: TextView
    private lateinit var singleCardsContainer: LinearLayout
    private lateinit var singleSupplierTitle: TextView
    private lateinit var singleSupplierValue: TextView
    private lateinit var singleBaleCountTitle: TextView
    private lateinit var singleBaleCountValue: TextView
    private lateinit var singleTotalWeightTitle: TextView
    private lateinit var singleTotalWeightValue: TextView
    private lateinit var singleLastBaleTitle: TextView
    private lateinit var singleLastBaleValue: TextView
    private lateinit var singleShiftBaleTitle: TextView
    private lateinit var singleShiftBaleValue: TextView


    private lateinit var tvShift: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvTime: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setupFullscreenMode()

        bindViews()
        applySystemBarInsets()

        viewModel = ViewModelProvider(this)[AndonViewModel::class.java]

        setupObservers()

        displayIpAddress()

        startTimeUpdate()


        startScreenToggleIfEnabled()


        if (TEST_MODE) {
            loadMockData()
        } else {
            viewModel.startAutoRefresh(this)
        }
    }

    private fun loadMockData() {
        val mockData = MockDataProvider.getMockMachineDataBothActive()
        tvMachineName.text = mockData.machineName
        tvShift.text = "Vardiya: ${mockData.currentShift}"

        updateKazan(
            workOrder = mockData.leftKazan,
            container = leftKazanContainer,
            tvWorkOrder = leftTvWorkOrderNumber,
            tvNoWorkOrder = leftTvNoWorkOrder,
            cardsContainer = leftCardsContainer,
            supplierValue = leftSupplierValue,
            baleCountValue = leftBaleCountValue,
            totalWeightValue = leftTotalWeightValue,
            lastBaleValue = leftLastBaleValue,
            shiftBaleValue = leftShiftBaleValue
        )

        updateKazan(
            workOrder = mockData.rightKazan,
            container = rightKazanContainer,
            tvWorkOrder = rightTvWorkOrderNumber,
            tvNoWorkOrder = rightTvNoWorkOrder,
            cardsContainer = rightCardsContainer,
            supplierValue = rightSupplierValue,
            baleCountValue = rightBaleCountValue,
            totalWeightValue = rightTotalWeightValue,
            lastBaleValue = rightLastBaleValue,
            shiftBaleValue = rightShiftBaleValue
        )

        val singleWorkOrder = mockData.leftKazan ?: mockData.rightKazan
        updateSingleKazan(singleWorkOrder)
    }

    private fun setupFullscreenMode() {
        supportActionBar?.hide()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
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

        rootLayout = findViewById(R.id.rootLayout)
        footerLayout = findViewById(R.id.footerLayout)
        contentFlipper = findViewById(R.id.contentFlipper)


        tvMachineName = findViewById(R.id.tvMachineName)
        tvIpAddress = findViewById(R.id.tvIpAddress)

        leftKazanContainer = findViewById(R.id.leftKazanLayout)
        leftKazanContainer.findViewById<TextView>(R.id.tvKazanTitle).text = getString(R.string.left_kazan)
        leftTvWorkOrderNumber = leftKazanContainer.findViewById(R.id.tvWorkOrderNumber)
        leftTvNoWorkOrder = leftKazanContainer.findViewById(R.id.tvNoWorkOrder)
        leftCardsContainer = leftKazanContainer.findViewById(R.id.cardsContainer)

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

        rightKazanContainer = findViewById(R.id.rightKazanLayout)
        rightKazanContainer.findViewById<TextView>(R.id.tvKazanTitle).text = getString(R.string.right_kazan)
        rightTvWorkOrderNumber = rightKazanContainer.findViewById(R.id.tvWorkOrderNumber)
        rightTvNoWorkOrder = rightKazanContainer.findViewById(R.id.tvNoWorkOrder)
        rightCardsContainer = rightKazanContainer.findViewById(R.id.cardsContainer)

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

        singleKazanContainer = findViewById(R.id.singleKazanLayout)
        singleTvWorkOrderNumber = findViewById(R.id.tvSingleWorkOrderNumber)
        singleTvNoWorkOrder = findViewById(R.id.tvSingleNoWorkOrder)
        singleCardsContainer = findViewById(R.id.singleCardsContainer)

        val singleSupplierCard = findViewById<View>(R.id.singleSupplierCard)
        singleSupplierTitle = singleSupplierCard.findViewById(R.id.tvCardTitle)
        singleSupplierValue = singleSupplierCard.findViewById(R.id.tvCardValue)

        val singleBaleCountCard = findViewById<View>(R.id.singleBaleCountCard)
        singleBaleCountTitle = singleBaleCountCard.findViewById(R.id.tvCardTitle)
        singleBaleCountValue = singleBaleCountCard.findViewById(R.id.tvCardValue)

        val singleTotalWeightCard = findViewById<View>(R.id.singleTotalWeightCard)
        singleTotalWeightTitle = singleTotalWeightCard.findViewById(R.id.tvCardTitle)
        singleTotalWeightValue = singleTotalWeightCard.findViewById(R.id.tvCardValue)

        val singleLastBaleCard = findViewById<View>(R.id.singleLastBaleCard)
        singleLastBaleTitle = singleLastBaleCard.findViewById(R.id.tvCardTitle)
        singleLastBaleValue = singleLastBaleCard.findViewById(R.id.tvCardValue)

        val singleShiftBaleCard = findViewById<View>(R.id.singleShiftBaleCard)
        singleShiftBaleTitle = singleShiftBaleCard.findViewById(R.id.tvCardTitle)
        singleShiftBaleValue = singleShiftBaleCard.findViewById(R.id.tvCardValue)


        tvShift = findViewById(R.id.tvShift)
        tvDate = findViewById(R.id.tvDate)
        tvTime = findViewById(R.id.tvTime)

        setupCardTitles()
    }

    private fun applySystemBarInsets() {
        val rootInitialPadding = rootLayout.paddingBottom
        val footerInitialPadding = footerLayout.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(view.paddingLeft, view.paddingTop, view.paddingRight, rootInitialPadding + systemBars.bottom)
            footerLayout.setPadding(
                footerLayout.paddingLeft,
                footerLayout.paddingTop,
                footerLayout.paddingRight,
                footerInitialPadding + systemBars.bottom
            )

            insets
        }
    }

    private fun setupCardTitles() {
        leftSupplierTitle.text = getString(R.string.supplier)
        leftBaleCountTitle.text = getString(R.string.bale_count)
        leftTotalWeightTitle.text = getString(R.string.total_weight)
        leftLastBaleTitle.text = getString(R.string.last_bale)
        leftShiftBaleTitle.text = getString(R.string.shift_bale_count)

        rightSupplierTitle.text = getString(R.string.supplier)
        rightBaleCountTitle.text = getString(R.string.bale_count)
        rightTotalWeightTitle.text = getString(R.string.total_weight)
        rightLastBaleTitle.text = getString(R.string.last_bale)
        rightShiftBaleTitle.text = getString(R.string.shift_bale_count)

        singleSupplierTitle.text = getString(R.string.supplier)
        singleBaleCountTitle.text = getString(R.string.bale_count)
        singleTotalWeightTitle.text = getString(R.string.total_weight)
        singleLastBaleTitle.text = getString(R.string.last_bale)
        singleShiftBaleTitle.text = getString(R.string.shift_bale_count)
    }

    private fun setupObservers() {
        viewModel.machineData.observe(this) { machineData ->
            machineData?.let {
                tvMachineName.text = it.machineName
                tvShift.text = "Vardiya: ${it.currentShift}"

                updateKazan(
                    workOrder = it.leftKazan,
                    container = leftKazanContainer,
                    tvWorkOrder = leftTvWorkOrderNumber,
                    tvNoWorkOrder = leftTvNoWorkOrder,
                    cardsContainer = leftCardsContainer,
                    supplierValue = leftSupplierValue,
                    baleCountValue = leftBaleCountValue,
                    totalWeightValue = leftTotalWeightValue,
                    lastBaleValue = leftLastBaleValue,
                    shiftBaleValue = leftShiftBaleValue
                )

                updateKazan(
                    workOrder = it.rightKazan,
                    container = rightKazanContainer,
                    tvWorkOrder = rightTvWorkOrderNumber,
                    tvNoWorkOrder = rightTvNoWorkOrder,
                    cardsContainer = rightCardsContainer,
                    supplierValue = rightSupplierValue,
                    baleCountValue = rightBaleCountValue,
                    totalWeightValue = rightTotalWeightValue,
                    lastBaleValue = rightLastBaleValue,
                    shiftBaleValue = rightShiftBaleValue
                )

                val singleWorkOrder = it.leftKazan ?: it.rightKazan
                updateSingleKazan(singleWorkOrder)
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                android.util.Log.e("AndonTV", "Error: $it")
            }
        }
    }

    private fun updateKazan(
        workOrder: WorkOrder?,
        container: ConstraintLayout,
        tvWorkOrder: TextView,
        tvNoWorkOrder: TextView,
        cardsContainer: View,
        supplierValue: TextView,
        baleCountValue: TextView,
        totalWeightValue: TextView,
        lastBaleValue: TextView,
        shiftBaleValue: TextView
    ) {
        if (workOrder == null) {
            tvWorkOrder.visibility = View.GONE
            cardsContainer.visibility = View.GONE
            tvNoWorkOrder.visibility = View.VISIBLE
            container.setBackgroundColor(ContextCompat.getColor(this, R.color.inactive_kazan))
        } else {
            tvNoWorkOrder.visibility = View.GONE
            tvWorkOrder.visibility = View.VISIBLE
            cardsContainer.visibility = View.VISIBLE
            tvWorkOrder.text = "İŞ EMRİ: ${workOrder.workOrderNumber}"

            if (workOrder.isActive) {
                container.setBackgroundColor(ContextCompat.getColor(this, R.color.active_kazan))
            } else {
                container.setBackgroundColor(ContextCompat.getColor(this, R.color.inactive_kazan))
            }

            val supplierNames = workOrder.suppliers.joinToString("\n") { it.supplierName }
            supplierValue.text = supplierNames

            baleCountValue.text = "${workOrder.baleCount} ${getString(R.string.adet)}"
            totalWeightValue.text = "${workOrder.totalWeight} ${getString(R.string.kg)}"
            lastBaleValue.text = workOrder.lastBaleNumber
            shiftBaleValue.text = "${workOrder.shiftBaleCount} ${getString(R.string.adet)}"
        }
    }

    private fun updateSingleKazan(workOrder: WorkOrder?) {
        if (workOrder == null) {
            singleTvWorkOrderNumber.visibility = View.GONE
            singleCardsContainer.visibility = View.GONE
            singleTvNoWorkOrder.visibility = View.VISIBLE
            singleKazanContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.inactive_kazan))
        } else {
            singleTvNoWorkOrder.visibility = View.GONE
            singleTvWorkOrderNumber.visibility = View.VISIBLE
            singleCardsContainer.visibility = View.VISIBLE
            singleTvWorkOrderNumber.text = "İŞ EMRİ: ${workOrder.workOrderNumber}"

            if (workOrder.isActive) {
                singleKazanContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.active_kazan))
            } else {
                singleKazanContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.inactive_kazan))
            }

            val supplierNames = workOrder.suppliers.joinToString("\n") { it.supplierName }
            singleSupplierValue.text = supplierNames

            singleBaleCountValue.text = "${workOrder.baleCount} ${getString(R.string.adet)}"
            singleTotalWeightValue.text = "${workOrder.totalWeight} ${getString(R.string.kg)}"
            singleLastBaleValue.text = workOrder.lastBaleNumber
            singleShiftBaleValue.text = "${workOrder.shiftBaleCount} ${getString(R.string.adet)}"
        }
    }

    private fun startScreenToggleIfEnabled() {
        if (!ENABLE_SECONDARY_SCREEN) {
            return
        }

        val toggleRunnable = object : Runnable {
            override fun run() {
                isMainScreenVisible = !isMainScreenVisible
                contentFlipper.displayedChild = if (isMainScreenVisible) 0 else 1
                screenToggleHandler.postDelayed(this, 30_000)
            }
        }

        screenToggleHandler.postDelayed(toggleRunnable, 30_000)
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
                timeUpdateHandler.postDelayed(this, 1000)
            }
        }
        timeUpdateHandler.post(updateTimeRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        timeUpdateHandler.removeCallbacksAndMessages(null)
        screenToggleHandler.removeCallbacksAndMessages(null)
        viewModel.stopAutoRefresh()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setupFullscreenMode()
        }
    }
}
