package com.example.tomatize.compose.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tomatize.UserData
import com.example.tomatize.compose.dialogs.BadHabitFailureComposeSheet
import com.example.tomatize.compose.dialogs.DeleteHabitComposeDialog
import com.example.tomatize.compose.dialogs.EditHabitComposeDialog
import com.example.tomatize.compose.dialogs.GameOverComposeSheet
import com.example.tomatize.compose.theme.BackgroundLight
import com.example.tomatize.compose.theme.BadHabitColor
import com.example.tomatize.compose.theme.Black
import com.example.tomatize.compose.theme.GnfFontFamily
import com.example.tomatize.compose.theme.GoodHabitColor
import com.example.tomatize.compose.theme.MontserratFontFamily
import com.example.tomatize.compose.theme.RedColor
import com.example.tomatize.compose.theme.White
import com.example.tomatize.compose.theme.WhiteCard
import ui.Habit
import ui.HabitDatabaseHelper
import ui.HabitStatistics
import ui.HabitType
import ui.ShopStorage
import ui.TomatoHealthStorage
import java.util.Calendar

@Composable
fun HabitDetailStatisticsScreen(
    habitId: Long,
    databaseHelper: HabitDatabaseHelper,
    onBackClick: () -> Unit,
    onShowNotification: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var habit by remember { mutableStateOf(databaseHelper.getHabitById(habitId)) }
    var statistics by remember { mutableStateOf(databaseHelper.getHabitStatistics(habitId)) }

    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var pendingFailureDayMillis by remember { mutableStateOf<Long?>(null) }
    var showGameOverSheet by remember { mutableStateOf(false) }

    fun reload() {
        habit = databaseHelper.getHabitById(habitId)
        statistics = databaseHelper.getHabitStatistics(habitId)
    }

    LaunchedEffect(habitId) {
        reload()
    }

    val currentHabit = habit
    if (currentHabit == null) {
        LaunchedEffect(Unit) { onBackClick() }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // --- Header ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    tint = Black
                )
            }

            Text(
                text = currentHabit.name.uppercase(),
                fontFamily = GnfFontFamily,
                fontSize = 20.sp,
                color = Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = { showEditDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Редактировать",
                    tint = GoodHabitColor
                )
            }

            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = BadHabitColor
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- Description Card ---
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = WhiteCard,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (currentHabit.description.isBlank()) "Описания привычки нет" else currentHabit.description,
                fontFamily = MontserratFontFamily,
                fontSize = 15.sp,
                color = Black,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- Weekly Calendar & Streaks Summary Card ---
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = WhiteCard,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Weekday headers
                val weekDaysNames = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
                Row(modifier = Modifier.fillMaxWidth()) {
                    weekDaysNames.forEach { name ->
                        Text(
                            text = name,
                            fontFamily = MontserratFontFamily,
                            fontSize = 12.sp,
                            color = Color(0xFF666666),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 7 days of current week
                val weekCal = Calendar.getInstance().apply {
                    firstDayOfWeek = Calendar.MONDAY
                    set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    for (i in 0 until 7) {
                        val dayMillis = weekCal.timeInMillis
                        val dayNum = weekCal.get(Calendar.DAY_OF_MONTH)

                        CalendarDayCell(
                            dayNumber = dayNum,
                            dayMillis = dayMillis,
                            habit = currentHabit,
                            completions = statistics.completions,
                            onDayClick = {
                                handleDayClick(
                                    dayMillis = dayMillis,
                                    habit = currentHabit,
                                    databaseHelper = databaseHelper,
                                    context = context,
                                    onShowNotification = onShowNotification,
                                    onRequestFailureSheet = { pendingFailureDayMillis = dayMillis },
                                    onReload = ::reload
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )

                        weekCal.add(Calendar.DAY_OF_MONTH, 1)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFDDDDDD))
                Spacer(modifier = Modifier.height(16.dp))

                // Streak record text
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "СЕРИЯ ИЗ\n${statistics.currentStreak} ${getDayWord(statistics.currentStreak)}",
                        fontFamily = MontserratFontFamily,
                        fontSize = 14.sp,
                        color = Black
                    )
                    Text(
                        text = "РЕКОРД:\n${statistics.recordStreak} ${getStreak(statistics.recordStreak)}",
                        fontFamily = MontserratFontFamily,
                        fontSize = 14.sp,
                        color = Black,
                        textAlign = TextAlign.End
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- Full Month Calendar Card ---
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = WhiteCard,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "КАЛЕНДАРЬ ВЫПОЛНЕНИЯ",
                    fontFamily = GnfFontFamily,
                    fontSize = 16.sp,
                    color = Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                )

                // Month Weekday headers
                val weekDaysNames = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
                Row(modifier = Modifier.fillMaxWidth()) {
                    weekDaysNames.forEach { name ->
                        Text(
                            text = name,
                            fontFamily = MontserratFontFamily,
                            fontSize = 12.sp,
                            color = Color(0xFF666666),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Month matrix: 6 rows x 7 cols
                val monthCal = Calendar.getInstance().apply {
                    set(Calendar.DAY_OF_MONTH, 1)
                }
                val currentMonth = monthCal.get(Calendar.MONTH)
                val currentYear = monthCal.get(Calendar.YEAR)
                val firstDayOfWeek = monthCal.get(Calendar.DAY_OF_WEEK)
                val daysInMonth = monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)

                val startPosition = when (firstDayOfWeek) {
                    Calendar.MONDAY -> 0
                    Calendar.TUESDAY -> 1
                    Calendar.WEDNESDAY -> 2
                    Calendar.THURSDAY -> 3
                    Calendar.FRIDAY -> 4
                    Calendar.SATURDAY -> 5
                    Calendar.SUNDAY -> 6
                    else -> 0
                }

                var dayCounter = 1
                for (row in 0 until 6) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        for (col in 0 until 7) {
                            val pos = row * 7 + col
                            if (pos >= startPosition && dayCounter <= daysInMonth) {
                                val currentDay = dayCounter
                                val calForDay = Calendar.getInstance().apply {
                                    set(currentYear, currentMonth, currentDay)
                                }
                                val dayMillis = calForDay.timeInMillis

                                CalendarDayCell(
                                    dayNumber = currentDay,
                                    dayMillis = dayMillis,
                                    habit = currentHabit,
                                    completions = statistics.completions,
                                    onDayClick = {
                                        handleDayClick(
                                            dayMillis = dayMillis,
                                            habit = currentHabit,
                                            databaseHelper = databaseHelper,
                                            context = context,
                                            onShowNotification = onShowNotification,
                                            onRequestFailureSheet = { pendingFailureDayMillis = dayMillis },
                                            onReload = ::reload
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                dayCounter++
                            } else {
                                Box(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }

    // --- Dialogs ---
    if (showEditDialog) {
        EditHabitComposeDialog(
            habit = currentHabit,
            onDismiss = { showEditDialog = false },
            onSaveHabit = { newName, newDesc ->
                databaseHelper.updateHabit(currentHabit.id, newName, newDesc)
                showEditDialog = false
                reload()
                onShowNotification("Привычка обновлена")
            },
            checkNameExistsExceptId = { name, id ->
                databaseHelper.existsByNameExceptId(name, id)
            }
        )
    }

    if (showDeleteDialog) {
        DeleteHabitComposeDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirmDelete = {
                databaseHelper.deleteHabit(currentHabit.id)
                showDeleteDialog = false
                onShowNotification("Привычка удалена")
                onBackClick()
            }
        )
    }

    pendingFailureDayMillis?.let { dayMillis ->
        BadHabitFailureComposeSheet(
            habit = currentHabit,
            onDismiss = { pendingFailureDayMillis = null },
            onConfirmFailure = {
                val changed = if (isToday(dayMillis)) {
                    databaseHelper.recordFailure(currentHabit.id)
                } else {
                    databaseHelper.toggleHabitCompletionOnDate(currentHabit.id, dayMillis)
                }

                if (changed) {
                    applyFailurePenalty(context)
                    val change = TomatoHealthStorage.loseHearts(context, currentHabit.heartDamage)
                    reload()
                    if (change.died) {
                        showGameOverSheet = true
                    } else {
                        onShowNotification("Вы потеряли 100 🍅")
                    }
                } else {
                    onShowNotification("Нельзя изменить эту дату")
                }
            }
        )
    }

    if (showGameOverSheet) {
        GameOverComposeSheet(
            onDismiss = { showGameOverSheet = false },
            onRestartClick = {
                TomatoHealthStorage.resetHearts(context)
                reload()
            }
        )
    }
}

@Composable
private fun CalendarDayCell(
    dayNumber: Int,
    dayMillis: Long,
    habit: Habit,
    completions: List<Long>,
    onDayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cal = Calendar.getInstance().apply { timeInMillis = dayMillis }
    val today = Calendar.getInstance()
    val createdAtCal = Calendar.getInstance().apply { timeInMillis = habit.createdAt }

    val isMarkedInDb = isDateInCompletions(dayMillis, completions)
    val isToday = isSameDay(cal, today)
    val isBeforeToday = cal.before(today) && !isToday
    val existedAtThatDay = isSameDay(cal, createdAtCal) || cal.after(createdAtCal)
    val canChange = canChangeCalendarDate(habit, dayMillis)

    val (bgColor, textColor, borderModifier) = if (!existedAtThatDay) {
        Triple(Color.Transparent, Black, Modifier.alpha(0.3f))
    } else if (habit.type == HabitType.GOOD) {
        when {
            isMarkedInDb -> Triple(GoodHabitColor, White, Modifier)
            isToday -> Triple(Color(0xFF2B1010), White, Modifier)
            isBeforeToday -> Triple(RedColor, White, Modifier)
            else -> Triple(Color.Transparent, Black, Modifier)
        }
    } else {
        when {
            isMarkedInDb -> Triple(RedColor, White, Modifier)
            isToday -> Triple(Color(0xFF2B1010), White, Modifier)
            isBeforeToday -> Triple(GoodHabitColor, White, Modifier)
            else -> Triple(Color.Transparent, Black, Modifier)
        }
    }

    Box(
        modifier = modifier
            .padding(2.dp)
            .aspectRatio(1f)
            .then(borderModifier)
            .clip(CircleShape)
            .background(bgColor)
            .clickable(enabled = canChange) { onDayClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$dayNumber",
            fontFamily = MontserratFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}

private fun handleDayClick(
    dayMillis: Long,
    habit: Habit,
    databaseHelper: HabitDatabaseHelper,
    context: Context,
    onShowNotification: (String) -> Unit,
    onRequestFailureSheet: () -> Unit,
    onReload: () -> Unit
) {
    val wasMarked = databaseHelper.isHabitCompletedOnDate(habit.id, dayMillis)
    val isToday = isToday(dayMillis)

    if (habit.type == HabitType.BAD) {
        if (wasMarked && isToday) {
            onShowNotification("Сегодняшний срыв нельзя отменить")
            return
        }
        if (!wasMarked) {
            onRequestFailureSheet()
            return
        }
    }

    val changed = databaseHelper.toggleHabitCompletionOnDate(habit.id, dayMillis)
    if (changed) {
        if (habit.type == HabitType.GOOD && isToday) {
            if (wasMarked) {
                undoHabitReward(context, onShowNotification)
                onShowNotification("Выполнение отменено")
            } else {
                awardHabitReward(context, onShowNotification)
                onShowNotification("Привычка ${habit.name} соблюдена!")
            }
        } else if (habit.type == HabitType.BAD && wasMarked) {
            onShowNotification("Срыв отменен")
        }
        onReload()
    } else {
        onShowNotification("Нельзя изменить эту дату")
    }
}

private fun canChangeCalendarDate(habit: Habit, dateInMillis: Long): Boolean {
    val dayStart = getStartOfDay(dateInMillis)
    val createdStart = getStartOfDay(habit.createdAt)
    val todayStart = getStartOfDay(System.currentTimeMillis())
    return dayStart in createdStart..todayStart
}

private fun getStartOfDay(dateInMillis: Long): Long {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = dateInMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.timeInMillis
}

private fun isDateInCompletions(dateInMillis: Long, completions: List<Long>): Boolean {
    val targetDate = Calendar.getInstance().apply { timeInMillis = dateInMillis }
    return completions.any { completion ->
        val completionDate = Calendar.getInstance().apply { timeInMillis = completion }
        isSameDay(targetDate, completionDate)
    }
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

private fun isToday(dateInMillis: Long): Boolean {
    val date = Calendar.getInstance().apply { timeInMillis = dateInMillis }
    return isSameDay(date, Calendar.getInstance())
}

private fun getDayWord(count: Int): String {
    return when {
        count % 10 == 1 && count % 100 != 11 -> "ДНЯ"
        else -> "ДНЕЙ"
    }
}

private fun getStreak(count: Int): String {
    return when {
        count % 10 == 1 -> "ДЕНЬ"
        count in 2..4 -> "ДНЯ"
        else -> "ДНЕЙ"
    }
}

private fun awardHabitReward(context: Context, onShowNotification: (String) -> Unit) {
    val prefs = context.getSharedPreferences(UserData.PREFS_NAME, Context.MODE_PRIVATE)
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    val currentDate = sdf.format(java.util.Date())

    val lastRewardDate = prefs.getString("LAST_REWARD_DATE", "")
    var rewardsToday = try {
        prefs.getInt("REWARDED_HABITS_TODAY", 0)
    } catch (e: Exception) {
        0
    }

    if (currentDate != lastRewardDate) {
        rewardsToday = 0
        prefs.edit()
            .putString("LAST_REWARD_DATE", currentDate)
            .putInt("REWARDED_HABITS_TODAY", 0)
            .apply()
    }

    if (rewardsToday >= 3) return

    val currentBalance = prefs.getInt(UserData.KEY_CURRENCY, 0)
    val rewardAmount = 50
    val newBalance = minOf(currentBalance + rewardAmount, ShopStorage.MAX_BALANCE)
    val newRewardsToday = rewardsToday + 1

    prefs.edit()
        .putInt(UserData.KEY_CURRENCY, newBalance)
        .putInt("REWARDED_HABITS_TODAY", newRewardsToday)
        .apply()

    if (currentBalance >= ShopStorage.MAX_BALANCE || newBalance == ShopStorage.MAX_BALANCE) {
        onShowNotification("Ваш баланс достиг максимума, Вы богач!")
    } else {
        onShowNotification("Награда $rewardAmount 🍅! ($newRewardsToday/3)")
    }
}

private fun undoHabitReward(context: Context, onShowNotification: (String) -> Unit) {
    val prefs = context.getSharedPreferences(UserData.PREFS_NAME, Context.MODE_PRIVATE)
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    val currentDate = sdf.format(java.util.Date())

    val lastRewardDate = prefs.getString("LAST_REWARD_DATE", "")
    if (currentDate == lastRewardDate) {
        var rewardsToday = try {
            prefs.getInt("REWARDED_HABITS_TODAY", 0)
        } catch (e: Exception) {
            0
        }

        if (rewardsToday > 0) {
            val currentBalance = prefs.getInt(UserData.KEY_CURRENCY, 0)
            val penaltyAmount = 50
            rewardsToday -= 1

            prefs.edit()
                .putInt(UserData.KEY_CURRENCY, maxOf(0, currentBalance - penaltyAmount))
                .putInt("REWARDED_HABITS_TODAY", rewardsToday)
                .apply()

            onShowNotification("Списано $penaltyAmount 🍅")
        }
    }
}

private fun applyFailurePenalty(context: Context) {
    val prefs = context.getSharedPreferences(UserData.PREFS_NAME, Context.MODE_PRIVATE)
    val currentBalance = prefs.getInt(UserData.KEY_CURRENCY, 0)
    val penalty = 100
    prefs.edit()
        .putInt(UserData.KEY_CURRENCY, maxOf(0, currentBalance - penalty))
        .apply()
}
