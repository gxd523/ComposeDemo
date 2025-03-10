package com.gxd.demo.android.util

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

fun <T> SnapshotStateList<T>.clearAndAddAll(newItems: List<T>) {
    this.clear()
    this.addAll(newItems)
}

@Composable
fun Modifier.screenHeightPercent(percent: Int = 50): Modifier = screenSizePercent(100, percent)

@Composable
fun Modifier.screenSizePercent(widthPercent: Int = 50, heightPercent: Int = 50): Modifier {
    val configuration = LocalConfiguration.current
    val width = configuration.screenWidthDp * widthPercent / 100
    val height = configuration.screenHeightDp * heightPercent / 100
    return size(width.dp, height.dp)
}