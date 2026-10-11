package com.example.tomatize.compose.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.tomatize.compose.theme.BadHabitColor
import com.example.tomatize.compose.theme.Black
import com.example.tomatize.compose.theme.GnfFontFamily
import com.example.tomatize.compose.theme.GoodHabitColor
import com.example.tomatize.compose.theme.MontserratFontFamily
import com.example.tomatize.compose.theme.NavInactive
import com.example.tomatize.compose.theme.White
import com.example.tomatize.compose.theme.WhiteCard
import ui.DEFAULT_BAD_DIFFICULTY
import ui.Habit
import ui.HabitType

@Composable
fun AddHabitComposeDialog(
    onDismiss: () -> Unit,
    onAddHabit: (Habit) -> Unit,
    checkNameExists: (String) -> Boolean
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<HabitType?>(null) }
    var selectedDifficulty by remember { mutableIntStateOf(DEFAULT_BAD_DIFFICULTY) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var typeError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = WhiteCard,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "ДОБАВИТЬ ПРИВЫЧКУ",
                        fontFamily = GnfFontFamily,
                        fontSize = 24.sp,
                        color = Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 32.dp)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Закрыть",
                            tint = Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = null
                    },
                    placeholder = { Text("Название привычки", fontFamily = MontserratFontFamily) },
                    isError = nameError != null,
                    supportingText = nameError?.let { { Text(it, color = BadHabitColor) } },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        focusedBorderColor = Black,
                        unfocusedBorderColor = Color(0xFFDDDDDD)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Описание привычки", fontFamily = MontserratFontFamily) },
                    shape = RoundedCornerShape(16.dp),
                    minLines = 3,
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        focusedBorderColor = Black,
                        unfocusedBorderColor = Color(0xFFDDDDDD)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (typeError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Выберите тип привычки",
                        fontFamily = MontserratFontFamily,
                        fontSize = 13.sp,
                        color = BadHabitColor,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ТИП ПРИВЫЧКИ",
                    fontFamily = GnfFontFamily,
                    fontSize = 16.sp,
                    color = Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            selectedType = HabitType.GOOD
                            typeError = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedType == HabitType.GOOD) GoodHabitColor else NavInactive
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(
                            text = "ХОРОШАЯ",
                            fontFamily = MontserratFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            selectedType = HabitType.BAD
                            typeError = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedType == HabitType.BAD) BadHabitColor else NavInactive
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(
                            text = "ПЛОХАЯ",
                            fontFamily = MontserratFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    }
                }

                if (selectedType == HabitType.BAD) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "СЛОЖНОСТЬ",
                        fontFamily = GnfFontFamily,
                        fontSize = 16.sp,
                        color = Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (diff in 1..3) {
                            Button(
                                onClick = { selectedDifficulty = diff },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedDifficulty == diff) BadHabitColor else NavInactive
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                            ) {
                                Text(
                                    text = diff.toString(),
                                    fontFamily = MontserratFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = White
                                )
                            }
                            if (diff < 3) Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val trimmedName = name.trim()
                        if (trimmedName.isEmpty()) {
                            nameError = "Введите название привычки"
                            return@Button
                        }
                        if (checkNameExists(trimmedName)) {
                            nameError = "Привычка с таким названием уже существует"
                            return@Button
                        }
                        if (selectedType == null) {
                            typeError = true
                            return@Button
                        }

                        val habit = Habit(
                            name = trimmedName,
                            description = description.trim(),
                            type = selectedType!!,
                            streakCount = 0,
                            lastCompleted = null,
                            badDifficulty = if (selectedType == HabitType.BAD) selectedDifficulty else DEFAULT_BAD_DIFFICULTY,
                            createdAt = System.currentTimeMillis()
                        )
                        onAddHabit(habit)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Black),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text(
                        text = "ДОБАВИТЬ",
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = White
                    )
                }
            }
        }
    }
}
