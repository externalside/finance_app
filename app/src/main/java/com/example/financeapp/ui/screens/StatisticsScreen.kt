package com.example.financeapp.ui.screens

import android.content.Context
import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.financeapp.ui.viewmodel.TransactionViewModel
import com.example.financeapp.data.model.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val incomeTotals by viewModel.getCategoryTotals(TransactionType.INCOME)
        .collectAsState(initial = emptyList())
    val expenseTotals by viewModel.getCategoryTotals(TransactionType.EXPENSE)
        .collectAsState(initial = emptyList())

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
            // Доходы
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Доходы по категориям",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    incomeTotals.forEach { categoryTotal ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = categoryTotal.category)
                            Text(
                                text = "₽${categoryTotal.total}",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Расходы
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Расходы по категориям",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    expenseTotals.forEach { categoryTotal ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = categoryTotal.category)
                            Text(
                                text = "₽${categoryTotal.total}",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
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