package com.isletme.andontv.utils

import com.isletme.andontv.model.MachineData
import com.isletme.andontv.model.Supplier
import com.isletme.andontv.model.WorkOrder

object MockDataProvider {

    fun getMockMachineDataBothActive(): MachineData {
        return MachineData(
            machineName = "ELYAF AÇMA MAKİNESİ 1",
            machineIp = "10.0.2.15",
            leftKazan = WorkOrder(
                workOrderNumber = "WO-2025-001",
                isActive = true,
                suppliers = listOf(
                    Supplier("Abalıoğlu Tekstil"),
                    Supplier("Güneş Elyaf"),
                    Supplier("Mavi Kumaş")
                ),
                baleCount = 126,
                totalWeight = 8574.5,
                lastBaleNumber = "S-458",
                shiftBaleCount = 48
            ),
            rightKazan = WorkOrder(
                workOrderNumber = "WO-2025-002",
                isActive = true,
                suppliers = listOf(
                    Supplier("Atlas Elyaf"),
                    Supplier("Dost Tekstil"),
                    Supplier("Kuzey Elyaf")
                ),
                baleCount = 118,
                totalWeight = 7920.0,
                lastBaleNumber = "S-443",
                shiftBaleCount = 42
            ),
            currentShift = "1",
            timestamp = System.currentTimeMillis()
        )
    }

    fun getMockMachineDataLeftOnly(): MachineData {
        return MachineData(
            machineName = "ELYAF AÇMA MAKİNESİ 1",
            machineIp = "10.0.2.15",
            leftKazan = WorkOrder(
                workOrderNumber = "WO-2025-003",
                isActive = true,
                suppliers = listOf(
                    Supplier("Güneş Elyaf"),
                    Supplier("Atılım Tekstil")
                ),
                baleCount = 94,
                totalWeight = 6025.0,
                lastBaleNumber = "S-391",
                shiftBaleCount = 24
            ),
            rightKazan = null,
            currentShift = "2",
            timestamp = System.currentTimeMillis()
        )
    }

    fun getMockMachineDataEmpty(): MachineData {
        return MachineData(
            machineName = "ELYAF AÇMA MAKİNESİ 1",
            machineIp = "10.0.2.15",
            leftKazan = null,
            rightKazan = null,
            currentShift = "3",
            timestamp = System.currentTimeMillis()
        )
    }

    fun getMockMachineDataMultipleSuppliers(): MachineData {
        val supplierList = listOf(
            Supplier("Güneş Elyaf"),
            Supplier("Mavi Kumaş"),
            Supplier("Dost Tekstil"),
            Supplier("Atlas Elyaf"),
            Supplier("Ege Elyaf"),
            Supplier("Karadeniz Tekstil"),
            Supplier("Kuzey Elyaf")
        )

        return MachineData(
            machineName = "ELYAF AÇMA MAKİNESİ 1",
            machineIp = "10.0.2.15",
            leftKazan = WorkOrder(
                workOrderNumber = "WO-2025-004",
                isActive = true,
                suppliers = supplierList,
                baleCount = 132,
                totalWeight = 9012.3,
                lastBaleNumber = "S-462",
                shiftBaleCount = 51
            ),
            rightKazan = WorkOrder(
                workOrderNumber = "WO-2025-005",
                isActive = false,
                suppliers = supplierList.reversed(),
                baleCount = 128,
                totalWeight = 8740.8,
                lastBaleNumber = "S-459",
                shiftBaleCount = 46
            ),
            currentShift = "1",
            timestamp = System.currentTimeMillis()
        )
    }
}
