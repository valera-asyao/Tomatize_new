package com.example.tomatize.compose.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tomatize.ShopItem
import com.example.tomatize.UserData
import com.example.tomatize.compose.theme.BackgroundLight
import com.example.tomatize.compose.theme.Black
import com.example.tomatize.compose.theme.GnfFontFamily
import com.example.tomatize.compose.theme.MontserratFontFamily
import com.example.tomatize.compose.theme.NavActive
import com.example.tomatize.compose.theme.White
import com.example.tomatize.compose.theme.WhiteCard
import ui.ShopStorage

@Composable
fun ShopScreen(
    onShowNotification: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var balance by remember { mutableIntStateOf(ShopStorage.getBalance(context)) }
    var selectedType by remember { mutableStateOf<String?>(null) }
    var ownedIds by remember { mutableStateOf(ShopStorage.getOwnedItemIds(context)) }

    fun refreshShop() {
        balance = ShopStorage.getBalance(context)
        ownedIds = ShopStorage.getOwnedItemIds(context)
    }

    LaunchedEffect(Unit) {
        refreshShop()
    }

    val visibleItems = UserData.filterItems(selectedType)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
    ) {
        // --- Top Bar: Balance Pill & Cheat Button ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Black
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$balance",
                        fontFamily = GnfFontFamily,
                        fontSize = 18.sp,
                        color = White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "🍅", fontSize = 16.sp)
                }
            }

            Button(
                onClick = {
                    val prev = ShopStorage.getBalance(context)
                    val newBal = ShopStorage.addBalance(context, 5000)
                    refreshShop()
                    if (prev >= ShopStorage.MAX_BALANCE || newBal == ShopStorage.MAX_BALANCE) {
                        onShowNotification("Ваш баланс достиг максимума, Вы богач!")
                    } else {
                        onShowNotification("Получено 5000 🍅!")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavActive),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "ЧИТ-КОД (+5000)",
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = White
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- Category Filters ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val filterOptions = listOf<String?>(null) + UserData.shopTypes
            filterOptions.forEach { type ->
                val isSelected = selectedType == type
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = if (isSelected) NavActive else Black,
                    modifier = Modifier.clickable {
                        selectedType = type
                    }
                ) {
                    Text(
                        text = UserData.typeLabel(type),
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = White,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- Items Grid ---
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(visibleItems, key = { it.id }) { item ->
                val isOwned = ownedIds.contains(item.id)
                val isEquipped = ShopStorage.isEquipped(context, item)

                ShopItemCard(
                    item = item,
                    isOwned = isOwned,
                    isEquipped = isEquipped,
                    onItemClick = {
                        if (isOwned) {
                            if (isEquipped) {
                                ShopStorage.unequipType(context, item.type)
                                onShowNotification("Снято: ${item.name}")
                            } else {
                                ShopStorage.equipItem(context, item)
                                onShowNotification("Надето: ${item.name}")
                            }
                            refreshShop()
                        } else {
                            if (ShopStorage.buyItem(context, item)) {
                                refreshShop()
                                onShowNotification("Куплено: ${item.name}")
                            } else {
                                onShowNotification("Недостаточно средств")
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ShopItemCard(
    item: ShopItem,
    isOwned: Boolean,
    isEquipped: Boolean,
    onItemClick: () -> Unit
) {
    val grayscaleMatrix = remember {
        ColorMatrix().apply { setToSaturation(0f) }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Black),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isOwned) 0.7f else 1f)
            .clickable { onItemClick() }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Surface(
                color = WhiteCard,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Image(
                    painter = painterResource(id = item.iconRes),
                    contentDescription = item.name,
                    contentScale = ContentScale.Fit,
                    colorFilter = if (isOwned) ColorFilter.colorMatrix(grayscaleMatrix) else null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.name,
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (!isOwned) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${item.price}",
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = White
                    )
                } else {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isEquipped) "НАДЕТО" else "КУПЛЕНО",
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = if (isEquipped) NavActive else Color(0xFFAAAAAA)
                    )
                }
            }
        }
    }
}
