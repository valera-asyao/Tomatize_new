package com.example.tomatize.compose.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.tomatize.compose.theme.White
import com.example.tomatize.compose.theme.WhiteCard
import ui.Habit

@Composable
fun EditHabitComposeDialog(
    habit: Habit,
    onDismiss: () -> Unit,
    onSaveHabit: (String, String) -> Unit,
    checkNameExistsExceptId: (String, Long) -> Boolean
) {
    var name by remember { mutableStateOf(habit.name) }
    var description by remember { mutableStateOf(habit.description) }
    var nameError by remember { mutableStateOf<String?>(null) }

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
                Text(
                    text = "Редактирование",
                    fontFamily = GnfFontFamily,
                    fontSize = 24.sp,
                    color = Black,
                    textAlign = TextAlign.Center
                )

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

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = White),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Отменить",
                            fontFamily = MontserratFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF666666)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            val trimmedName = name.trim()
                            if (trimmedName.isEmpty()) {
                                nameError = "Введите название привычки"
                                return@Button
                            }
                            if (checkNameExistsExceptId(trimmedName, habit.id)) {
                                nameError = "Привычка с таким названием уже существует"
                                return@Button
                            }
                            onSaveHabit(trimmedName, description.trim())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoodHabitColor),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Сохранить",
                            fontFamily = MontserratFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    }
                }
            }
        }
    }
}
