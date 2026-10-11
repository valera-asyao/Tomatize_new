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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tomatize.ShopItem
import com.example.tomatize.UserData
import com.example.tomatize.compose.components.MascotView
import com.example.tomatize.compose.theme.BackgroundLight
import com.example.tomatize.compose.theme.Black
import com.example.tomatize.compose.theme.MontserratFontFamily
import com.example.tomatize.compose.theme.NavActive
import com.example.tomatize.compose.theme.White
import com.example.tomatize.compose.theme.WhiteCard
import ui.ShopStorage

@Composable
fun ProfileScreen(
    onShowNotification: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedType by remember { mutableStateOf<String?>(null) }
    var equippedItems by remember { mutableStateOf(getEquippedItems(context)) }
    var ownedItems by remember { mutableStateOf(emptyList<ShopItem>()) }

    fun refreshProfile() {
        val ownedIds = ShopStorage.getOwnedItemIds(context)
        val allOwned = UserData.allShopItems.filter { it.id in ownedIds }
        ownedItems = if (selectedType == null) allOwned else allOwned.filter { it.type == selectedType }
        equippedItems = getEquippedItems(context)
    }

    LaunchedEffect(selectedType) {
        refreshProfile()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
    ) {
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

        // --- Owned Items Grid ---
        if (ownedItems.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = WhiteCard,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "В гардеробе пока пусто.\nКупите аксессуары в магазине!",
                        fontFamily = MontserratFontFamily,
                        fontSize = 16.sp,
                        color = Black,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(ownedItems, key = { it.id }) { item ->
                    val isEquipped = ShopStorage.isEquipped(context, item)

                    InventoryItemCard(
                        item = item,
                        isEquipped = isEquipped,
                        onItemClick = {
                            if (isEquipped) {
                                ShopStorage.unequipType(context, item.type)
                                onShowNotification("Снято: ${item.name}")
                            } else {
                                ShopStorage.equipItem(context, item)
                                onShowNotification("Надето: ${item.name}")
                            }
                            refreshProfile()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun InventoryItemCard(
    item: ShopItem,
    isEquipped: Boolean,
    onItemClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Black),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isEquipped) 0.85f else 1f)
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isEquipped) "НАДЕТО" else item.name,
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (isEquipped) NavActive else White,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
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
