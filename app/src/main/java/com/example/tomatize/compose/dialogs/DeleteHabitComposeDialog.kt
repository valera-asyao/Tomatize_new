package com.example.tomatize.compose.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.tomatize.compose.theme.Black
import com.example.tomatize.compose.theme.MontserratFontFamily
import com.example.tomatize.compose.theme.RedColor
import com.example.tomatize.compose.theme.White
import com.example.tomatize.compose.theme.WhiteCard

@Composable
fun DeleteHabitComposeDialog(
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = WhiteCard,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Удалить привычку?",
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Black
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Вы уверены, что хотите удалить эту привычку? Весь прогресс будет потерян навсегда.",
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                    ) {
                        Text(
                            text = "ОТМЕНА",
                            fontFamily = MontserratFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF999999),
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            onConfirmDelete()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RedColor),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(52.dp)
                    ) {
                        Text(
                            text = "УДАЛИТЬ",
                            fontFamily = MontserratFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
