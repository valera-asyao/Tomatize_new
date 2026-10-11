package com.example.tomatize.compose.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.tomatize.R
import com.example.tomatize.compose.theme.NavActive
import com.example.tomatize.compose.theme.NavInactive
import com.example.tomatize.compose.theme.NavOvalColor
import com.example.tomatize.compose.theme.WhiteCard
import kotlin.math.roundToInt

@Composable
fun BottomNavBar(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = WhiteCard,
        shadowElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val totalWidthPx = constraints.maxWidth.toFloat()
            val tabWidthPx = totalWidthPx / 5f
            val ovalWidthPx = 60.dp.value * (totalWidthPx / maxWidth.value)
            
            val targetOffsetPx = if (selectedIndex in listOf(0, 1, 3, 4)) {
                selectedIndex * tabWidthPx + (tabWidthPx - ovalWidthPx) / 2f
            } else {
                -200f
            }

            val animatedOffsetPx by animateFloatAsState(
                targetValue = targetOffsetPx,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "navSelectorOffset"
            )

            if (selectedIndex in listOf(0, 1, 3, 4)) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(animatedOffsetPx.roundToInt(), 0) }
                        .align(Alignment.CenterStart)
                        .size(width = 60.dp, height = 36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(NavOvalColor)
                )
            }

            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 0: Home
                NavTabItem(
                    iconRes = R.drawable.ic_home,
                    isSelected = selectedIndex == 0,
                    onClick = { onTabSelected(0) },
                    modifier = Modifier.weight(1f)
                )

                // Tab 1: Statistics
                NavTabItem(
                    iconRes = R.drawable.ic_statistics,
                    isSelected = selectedIndex == 1,
                    onClick = { onTabSelected(1) },
                    modifier = Modifier.weight(1f)
                )

                // Tab 2: Add Habit (Center Tomato Button)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onAddClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.tomato_icon),
                        contentDescription = "Добавить привычку",
                        modifier = Modifier.size(54.dp)
                    )
                }

                // Tab 3: Shop
                NavTabItem(
                    iconRes = R.drawable.ic_shop,
                    isSelected = selectedIndex == 3,
                    onClick = { onTabSelected(3) },
                    modifier = Modifier.weight(1f)
                )

                // Tab 4: Profile
                NavTabItem(
                    iconRes = R.drawable.ic_profile,
                    isSelected = selectedIndex == 4,
                    onClick = { onTabSelected(4) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun NavTabItem(
    iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = if (isSelected) NavActive else NavInactive,
            modifier = Modifier.size(24.dp)
        )
    }
}
