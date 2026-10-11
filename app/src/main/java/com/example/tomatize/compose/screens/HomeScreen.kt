package com.example.tomatize.compose.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tomatize.R
import com.example.tomatize.ShopItem
import com.example.tomatize.UserData
import com.example.tomatize.compose.components.MascotView
import com.example.tomatize.compose.dialogs.BadHabitFailureComposeSheet
import com.example.tomatize.compose.dialogs.GameOverComposeSheet
import com.example.tomatize.compose.dialogs.HabitDetailsComposeSheet
import com.example.tomatize.compose.theme.BackgroundLight
import com.example.tomatize.compose.theme.Black
import com.example.tomatize.compose.theme.GnfFontFamily
import com.example.tomatize.compose.theme.HabitBadBg
import com.example.tomatize.compose.theme.HabitBadBtn
import com.example.tomatize.compose.theme.HabitBadText
import com.example.tomatize.compose.theme.HabitGoodBg
import com.example.tomatize.compose.theme.HabitGoodBtn
import com.example.tomatize.compose.theme.HabitGoodText
import com.example.tomatize.compose.theme.MontserratFontFamily
import com.example.tomatize.compose.theme.RedColor
import com.example.tomatize.compose.theme.White
import com.example.tomatize.compose.theme.WhiteCard
import ui.Habit
import ui.HabitDatabaseHelper
import ui.HabitType
import ui.ShopStorage
import ui.TomatoHealthState
import ui.TomatoHealthStorage
import java.util.Calendar

@Composable
fun HomeScreen(
    databaseHelper: HabitDatabaseHelper,
    onShowNotification: (String) -> Unit,
    onGoToStats: (Long) -> Unit,
    onEditHabitRequest: (Habit) -> Unit,
    onDeleteHabitRequest: (Habit) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var habits by remember { mutableStateOf(emptyList<Habit>()) }
    var healthState by remember { mutableStateOf(TomatoHealthStorage.getState(context)) }
    var balance by remember { mutableIntStateOf(ShopStorage.getBalance(context)) }
    var equippedItems by remember { mutableStateOf(getEquippedItems(context)) }

    var selectedDetailsHabit by remember { mutableStateOf<Habit?>(null) }
    var failingBadHabit by remember { mutableStateOf<Habit?>(null) }
    var showGameOverSheet by remember { mutableStateOf(false) }

    fun refreshAll() {
        habits = databaseHelper.getAllHabits().sortedWith(
            compareBy<Habit> { it.type }
                .thenBy { it.type == HabitType.GOOD && isCompletedToday(it) }
        )
        healthState = TomatoHealthStorage.getState(context)
        balance = ShopStorage.getBalance(context)
        equippedItems = getEquippedItems(context)
    }

    LaunchedEffect(Unit) {
        refreshAll()
    }

    val maxStreak = habits.maxOfOrNull { it.streakCount } ?: 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // --- Top Stat Header Bar ---
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = WhiteCard,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Streak counter
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Дней серии",
                        fontFamily = GnfFontFamily,
                        fontSize = 18.sp,
                        color = Black
                    )
                    Text(
                        text = "$maxStreak",
                        fontFamily = GnfFontFamily,
                        fontSize = 30.sp,
                        color = Black
                    )
                }

                // Hearts pill
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = RedColor,
                    modifier = Modifier.padding(end = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 0 until TomatoHealthStorage.MAX_HEARTS) {
                            Icon(
                                painter = painterResource(
                                    id = if (i < healthState.hearts) R.drawable.ic_heart_filled_white
                                    else R.drawable.ic_heart_outline_white
                                ),
                                contentDescription = null,
                                tint = White,
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(end = if (i < TomatoHealthStorage.MAX_HEARTS - 1) 4.dp else 0.dp)
                            )
                        }
                    }
                }

                // Currency pill
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFF2B1010)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$balance",
                            fontFamily = GnfFontFamily,
                            fontSize = 16.sp,
                            color = White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "🍅",
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- Mascot Card ---
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = WhiteCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            MascotView(
                equippedItems = equippedItems,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- Habits List Card ---
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = WhiteCard,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (habits.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "У вас пока нет привычек\nНажмите + чтобы добавить первую",
                        fontFamily = MontserratFontFamily,
                        fontSize = 16.sp,
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(habits, key = { it.id }) { habit ->
                        HabitItemCard(
                            habit = habit,
                            isCompletedToday = isCompletedToday(habit),
                            onCardClick = { selectedDetailsHabit = habit },
                            onCompleteClick = {
                                if (habit.type == HabitType.BAD) {
                                    failingBadHabit = habit
                                } else {
                                    val success = databaseHelper.completeHabit(habit.id)
                                    if (success) {
                                        awardHabitReward(context, onShowNotification)
                                        refreshAll()
                                        onShowNotification("Привычка ${habit.name} соблюдена!")
                                    }
                                }
                            },
                            onUndoClick = {
                                undoHabitReward(context, onShowNotification)
                                val success = databaseHelper.undoCompleteHabit(habit.id)
                                if (success) {
                                    refreshAll()
                                    onShowNotification("Выполнение отменено")
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // --- Details Sheet ---
    selectedDetailsHabit?.let { habit ->
        HabitDetailsComposeSheet(
            habit = habit,
            onDismiss = { selectedDetailsHabit = null },
            onEditClick = {
                selectedDetailsHabit = null
                onEditHabitRequest(habit)
            },
            onDeleteClick = {
                selectedDetailsHabit = null
                onDeleteHabitRequest(habit)
            },
            onGoToStatsClick = { habitId ->
                selectedDetailsHabit = null
                onGoToStats(habitId)
            }
        )
    }

    // --- Bad Habit Failure Sheet ---
    failingBadHabit?.let { habit ->
        BadHabitFailureComposeSheet(
            habit = habit,
            onDismiss = { failingBadHabit = null },
            onConfirmFailure = {
                if (databaseHelper.recordFailure(habit.id)) {
                    applyFailurePenalty(context)
                    val change = TomatoHealthStorage.loseHearts(context, habit.heartDamage)
                    refreshAll()
                    if (change.died) {
                        showGameOverSheet = true
                    } else {
                        onShowNotification("Вы потеряли 100 🍅")
                    }
                }
            }
        )
    }

    // --- Game Over Sheet ---
    if (showGameOverSheet) {
        GameOverComposeSheet(
            onDismiss = { showGameOverSheet = false },
            onRestartClick = {
                TomatoHealthStorage.resetHearts(context)
                refreshAll()
            }
        )
    }
}

@Composable
private fun HabitItemCard(
    habit: Habit,
    isCompletedToday: Boolean,
    onCardClick: () -> Unit,
    onCompleteClick: () -> Unit,
    onUndoClick: () -> Unit
) {
    val isGood = habit.type == HabitType.GOOD
    val bgColor = if (isGood) HabitGoodBg else HabitBadBg
    val btnColor = if (isGood) HabitGoodBtn else HabitBadBtn
    val textColor = if (isGood) HabitGoodText else HabitBadText

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = bgColor,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.name,
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${habit.streakCount} дн.",
                    fontFamily = MontserratFontFamily,
                    fontSize = 14.sp,
                    color = textColor
                )
            }

            if (isGood) {
                if (isCompletedToday) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(btnColor)
                            .clickable { onUndoClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_habit_cross),
                            contentDescription = "Отменить выполнение",
                            tint = White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(btnColor)
                            .clickable { onCompleteClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_habit_tick),
                            contentDescription = "Выполнить",
                            tint = White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            } else {
                if (!isCompletedToday) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(btnColor)
                            .clickable { onCompleteClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_habit_cross),
                            contentDescription = "Зафиксировать срыв",
                            tint = White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun isCompletedToday(habit: Habit): Boolean {
    val lastCompleted = habit.lastCompleted ?: return false
    val habitCal = Calendar.getInstance().apply { timeInMillis = lastCompleted }
    val todayCal = Calendar.getInstance()
    return habitCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
            habitCal.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR)
}

private fun getEquippedItems(context: Context): List<ShopItem> {
    val allEquipped = UserData.shopTypes
        .mapNotNull { type -> ShopStorage.getEquippedItemId(context, type) }
        .mapNotNull(UserData::findItemById)

    val hasOtherItem = allEquipped.any { it.type == "other" }
    return if (hasOtherItem) {
        allEquipped.filter { it.type == "other" || it.type == "mustache" }
    } else {
        allEquipped
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
