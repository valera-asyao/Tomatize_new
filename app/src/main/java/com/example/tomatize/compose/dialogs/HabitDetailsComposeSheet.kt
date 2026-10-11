package com.example.tomatize.compose.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tomatize.compose.theme.BadHabitColor
import com.example.tomatize.compose.theme.Black
import com.example.tomatize.compose.theme.GoodHabitColor
import com.example.tomatize.compose.theme.MontserratFontFamily
import com.example.tomatize.compose.theme.RedColor
import com.example.tomatize.compose.theme.White
import com.example.tomatize.compose.theme.WhiteCard
import ui.Habit
import ui.HabitType
import ui.normalizeBadDifficulty
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailsComposeSheet(
    habit: Habit,
    onDismiss: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onGoToStatsClick: (Long) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val isActionToday = habit.lastCompleted?.let {
        val lastCal = Calendar.getInstance().apply { timeInMillis = it }
        val nowCal = Calendar.getInstance()
        lastCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR) &&
                lastCal.get(Calendar.DAY_OF_YEAR) == nowCal.get(Calendar.DAY_OF_YEAR)
    } ?: false

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = WhiteCard
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = habit.name,
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Black,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Редактировать",
                        tint = GoodHabitColor
                    )
                }

                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить",
                        tint = BadHabitColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (habit.type == HabitType.GOOD) "Тип: Хорошая" else "Тип: Плохая",
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color(0xFF666666)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (habit.type == HabitType.GOOD) "Сложность: нет" else "Сложность: ${normalizeBadDifficulty(habit.badDifficulty)}/3",
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color(0xFF666666)
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ОПИСАНИЕ",
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = Color(0xFF999999)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (habit.description.isBlank()) "Описание отсутствует" else habit.description,
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Black
            )

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF5EBEB),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "СТАТУС СЕГОДНЯ",
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    val (statusText, statusColor) = if (habit.type == HabitType.GOOD) {
                        if (isActionToday) "Выполнено" to GoodHabitColor else "Не выполнено" to RedColor
                    } else {
                        if (isActionToday) "Срыв зафиксирован" to RedColor else "Срывов не было" to GoodHabitColor
                    }

                    Text(
                        text = statusText,
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Black),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "ПОНЯТНО",
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Открыть полную статистику",
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = Color(0xFF999999),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onGoToStatsClick(habit.id) }
                    .padding(8.dp)
            )
        }
    }
}
