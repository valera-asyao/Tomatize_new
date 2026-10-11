package com.example.tomatize.compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tomatize.compose.theme.BackgroundLight
import com.example.tomatize.compose.theme.BadHabitColor
import com.example.tomatize.compose.theme.Black
import com.example.tomatize.compose.theme.GoodHabitColor
import com.example.tomatize.compose.theme.MontserratFontFamily
import com.example.tomatize.compose.theme.White
import com.example.tomatize.compose.theme.WhiteCard
import ui.Habit
import ui.HabitDatabaseHelper
import ui.HabitType
import ui.normalizeBadDifficulty

@Composable
fun StatisticsScreen(
    databaseHelper: HabitDatabaseHelper,
    onHabitClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var habits by remember { mutableStateOf(emptyList<Habit>()) }

    LaunchedEffect(Unit) {
        habits = databaseHelper.getAllHabits().sortedBy { if (it.type == HabitType.BAD) 0 else 1 }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
    ) {
        if (habits.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Нет добавленных привычек",
                    fontFamily = MontserratFontFamily,
                    fontSize = 20.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(habits, key = { it.id }) { habit ->
                    HabitStatisticsItem(
                        habit = habit,
                        onClick = { onHabitClick(habit.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HabitStatisticsItem(
    habit: Habit,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = WhiteCard,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.name,
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Стрик ${habit.streakCount} дн.",
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Indicator segments
            val isGood = habit.type == HabitType.GOOD
            val indicatorColor = if (isGood) GoodHabitColor else BadHabitColor
            val segmentCount = if (isGood) 1 else normalizeBadDifficulty(habit.badDifficulty)

            Column(
                modifier = Modifier.height(48.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (i in 0 until segmentCount) {
                    val topRadius = if (i == 0) 6.dp else 0.dp
                    val bottomRadius = if (i == segmentCount - 1) 6.dp else 0.dp

                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .weight(1f)
                            .clip(
                                RoundedCornerShape(
                                    topStart = topRadius,
                                    topEnd = topRadius,
                                    bottomStart = bottomRadius,
                                    bottomEnd = bottomRadius
                                )
                            )
                            .background(indicatorColor)
                    )
                }
            }
        }
    }
}
