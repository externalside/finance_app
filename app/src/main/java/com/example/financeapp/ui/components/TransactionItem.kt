package com.example.financeapp.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.financeapp.data.model.Category
import com.example.financeapp.data.model.Transaction
import com.example.financeapp.data.model.TransactionType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.background


@Composable
fun TransactionItem(
    transaction: Transaction,
    onItemClick: () -> Unit,
    onLongClick: () -> Unit,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier
) {
    val numberFormat = remember { NumberFormat.getCurrencyInstance(Locale("ru", "RU")) }
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy", Locale("ru", "RU")) }

    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(),
        exit = slideOutHorizontally(
            targetOffsetX = { -it },
            animationSpec = tween(durationMillis = 300)
        ) + fadeOut()
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onItemClick() },
                        onLongPress = { onLongClick() }
                    )
                }
                .background(backgroundColor),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            ),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Левая часть: иконка + описание + дата/категория
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = when (transaction.category) {
                            Category.FOOD -> Icons.Default.RestaurantMenu
                            Category.TRANSPORT -> Icons.Default.DirectionsCar
                            Category.SHOPPING -> Icons.Default.ShoppingBag
                            Category.ENTERTAINMENT -> Icons.Default.Theaters
                            Category.HEALTH -> Icons.Default.HealthAndSafety
                            Category.HOUSING -> Icons.Default.Home
                            Category.CAFE -> Icons.Default.LocalCafe
                            Category.EDUCATION -> Icons.Default.School
                            Category.SALARY -> Icons.Default.Payments
                            Category.TRANSFER -> Icons.Default.SwapHoriz
                            Category.OTHER -> Icons.Default.Label
                        },
                        contentDescription = transaction.category.title,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Column {
                        Text(
                            text = transaction.description,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${transaction.category.title} • ${dateFormat.format(transaction.date)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(24.dp)) // Отступ между датой и суммой

                // Сумма справа
                Text(
                    text = numberFormat.format(transaction.amount),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (transaction.type == TransactionType.INCOME)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
