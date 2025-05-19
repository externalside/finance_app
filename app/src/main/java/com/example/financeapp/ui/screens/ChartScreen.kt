package com.example.financeapp.ui.screens

import android.content.Context
import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.viewinterop.AndroidView
import com.example.financeapp.data.model.TransactionType
import com.example.financeapp.ui.viewmodel.TransactionViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartScreen(
    onNavigateBack: () -> Unit,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }

    val expenseTotals by viewModel.getCategoryTotals(TransactionType.EXPENSE)
        .collectAsState(initial = emptyList())

    val incomeTotals by viewModel.getCategoryTotals(TransactionType.INCOME)
        .collectAsState(initial = emptyList())

    var showChart by remember { mutableStateOf(false) }
    var showChartKey by remember { mutableStateOf(0) }

    var selectedFilter by remember { mutableStateOf("Месяц") }
    val filters = listOf("День", "Месяц", "Год")
    var expandedFilter by remember { mutableStateOf(false) }
    var expandedType by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Статистика") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Тип:")
                Spacer(modifier = Modifier.width(8.dp))
                Box {
                    TextButton(onClick = { expandedType = true }) {
                        Text(if (selectedType == TransactionType.EXPENSE) "Расход" else "Доход")
                    }
                    DropdownMenu(
                        expanded = expandedType,
                        onDismissRequest = { expandedType = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Расход") },
                            onClick = {
                                selectedType = TransactionType.EXPENSE
                                expandedType = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Доход") },
                            onClick = {
                                selectedType = TransactionType.INCOME
                                expandedType = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Фильтр:")
                Spacer(modifier = Modifier.width(8.dp))
                Box {
                    TextButton(onClick = { expandedFilter = true }) {
                        Text(selectedFilter)
                    }
                    DropdownMenu(
                        expanded = expandedFilter,
                        onDismissRequest = { expandedFilter = false }
                    ) {
                        filters.forEach { filter ->
                            DropdownMenuItem(
                                text = { Text(filter) },
                                onClick = {
                                    selectedFilter = filter
                                    expandedFilter = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                showChart = true
                showChartKey++ // обновляем ключ для пересоздания диаграммы
            }) {
                Text("Создать диаграмму")
            }

            if (showChart) {
                Spacer(modifier = Modifier.height(16.dp))
                key(showChartKey) {
                    AndroidView(
                        factory = {
                            val entries = when (selectedType) {
                                TransactionType.EXPENSE -> expenseTotals.map {
                                    PieEntry(it.total.toFloat(), it.category)
                                }
                                TransactionType.INCOME -> incomeTotals.map {
                                    PieEntry(it.total.toFloat(), it.category)
                                }
                            }
                            createPieChart(context, entries)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )
                }
            }
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
        isRotationEnabled = true
        isHighlightPerTapEnabled = true

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(
            Color.parseColor("#1E2D2F"),
            Color.parseColor("#FF5733"),
            Color.parseColor("#84B082"),
            Color.parseColor("#364B54"),
            Color.parseColor("#CC4429")
        )
        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = Color.TRANSPARENT

        dataSet.setDrawValues(true)
        dataSet.setDrawIcons(false)
        setDrawEntryLabels(false)

        data = PieData(dataSet)
        invalidate()
    }
}
