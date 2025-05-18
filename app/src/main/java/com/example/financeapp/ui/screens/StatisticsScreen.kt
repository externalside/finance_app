package com.example.financeapp.ui.screens

import android.content.Context
import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.io.File
import java.io.FileOutputStream
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.financeapp.ui.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Статистика",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

private fun createPieChart(context: Context, entries: List<PieEntry>): PieChart {
    return PieChart(context).apply {
        setUsePercentValues(true)
        description.isEnabled = false
        isDrawHoleEnabled = true
        setHoleColor(Color.WHITE)
        setTransparentCircleColor(Color.WHITE)
        setTransparentCircleAlpha(110)
        holeRadius = 58f
        transparentCircleRadius = 61f
        setDrawCenterText(true)
        centerText = "Расходы"
        isRotationEnabled = true
        isHighlightPerTapEnabled = true

        val dataSet = PieDataSet(entries, "Категории расходов")
        dataSet.colors = listOf(
            Color.parseColor("#1E2D2F"),
            Color.parseColor("#FF5733"),
            Color.parseColor("#84B082"),
            Color.parseColor("#364B54"),
            Color.parseColor("#CC4429")
        )
        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = Color.WHITE

        data = PieData(dataSet)
        invalidate()
    }
}

private fun exportToExcel(context: Context, entries: List<PieEntry>) {
    val workbook = XSSFWorkbook()
    val sheet = workbook.createSheet("Статистика расходов")
    
    // Создаем заголовки
    val headerRow = sheet.createRow(0)
    headerRow.createCell(0).setCellValue("Категория")
    headerRow.createCell(1).setCellValue("Сумма")
    
    // Заполняем данными
    entries.forEachIndexed { index, entry ->
        val row = sheet.createRow(index + 1)
        row.createCell(0).setCellValue(entry.label)
        row.createCell(1).setCellValue(entry.value.toDouble())
    }
    
    // Сохраняем файл
    val file = File(context.getExternalFilesDir(null), "statistics.xlsx")
    FileOutputStream(file).use { stream ->
        workbook.write(stream)
    }
    workbook.close()
} 