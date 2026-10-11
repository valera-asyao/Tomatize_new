package com.example.tomatize.compose.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tomatize.R
import com.example.tomatize.compose.theme.MontserratFontFamily
import com.example.tomatize.compose.theme.RedColor
import com.example.tomatize.compose.theme.White
import com.example.tomatize.compose.theme.WhiteCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameOverComposeSheet(
    onDismiss: () -> Unit,
    onRestartClick: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = White,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.dead_tomato),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Помидор погиб",
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = RedColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Все сердца потеряны. Скины и монеты были сброшены. Постарайтесь в следующий раз!",
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onRestartClick()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = RedColor),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "НАЧАТЬ ЗАНОВО",
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = White,
                    fontSize = 14.sp
                )
            }
        }
    }
}
