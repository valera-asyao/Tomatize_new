package com.example.tomatize.compose.components

import android.widget.FrameLayout
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.tomatize.R
import com.example.tomatize.ShopItem
import com.example.tomatize.compose.theme.WhiteCard
import ui.MascotOverlayRenderer

@Composable
fun MascotView(
    equippedItems: List<ShopItem>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(WhiteCard)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                val root = FrameLayout(context).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                }

                val baseImage = ImageView(context).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                    scaleType = ImageView.ScaleType.FIT_CENTER
                    setImageResource(R.drawable.tomagochi)
                }
                root.addView(baseImage)

                val overlayContainer = FrameLayout(context).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                    importantForAccessibility = android.view.View.IMPORTANT_FOR_ACCESSIBILITY_NO
                }
                root.addView(overlayContainer)

                MascotOverlayRenderer.render(context, overlayContainer, equippedItems)
                root
            },
            update = { root ->
                val overlayContainer = root.getChildAt(1) as? FrameLayout
                if (overlayContainer != null) {
                    MascotOverlayRenderer.render(root.context, overlayContainer, equippedItems)
                }
            }
        )
    }
}
