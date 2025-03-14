package com.gxd.demo.android.compose.case

import android.graphics.Camera
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.gxd.demo.android.R
import kotlin.math.roundToInt

@Preview(showBackground = true)
@Composable
fun CustomDrawCase() {
    val imageBitmap = ImageBitmap.imageResource(R.drawable.avatar_png) // 内部有包「remember」
    val paint by remember { mutableStateOf(Paint()) }
    val camera by remember { mutableStateOf(Camera()) }
    val rotationAnimatable = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        val animationSpec = infiniteRepeatable<Float>(tween(2_000))
        rotationAnimatable.animateTo(360f, animationSpec)
    }

    Canvas(Modifier.background(Color.LightGray).padding(50.dp).size(100.dp)) {
        drawIntoCanvas { canvas ->
            val width = size.width
            val height = size.height
            val translateX = width / 2
            val translateY = height / 2
            val rotateDegrees = 45f

            canvas.translate(translateX, translateY)
            canvas.rotate(-rotateDegrees)

            camera.save()
            camera.rotateX(rotationAnimatable.value)
            camera.applyToCanvas(canvas.nativeCanvas)
            camera.restore()

            canvas.rotate(rotateDegrees)
            canvas.translate(-translateX, -translateY)

            val dstSize = IntSize(width.roundToInt(), height.roundToInt())
            canvas.drawImageRect(imageBitmap, dstSize = dstSize, paint = paint)
        }
    }
}

/**
 * 自定义「Layout」实现「Row」效果
 */
@Composable
fun CustomRowLayout(modifier: Modifier = Modifier, spacing: Dp = 0.dp, content: @Composable () -> Unit) {
    Layout(content, modifier) { measurables, constraints ->
        val spacingPx = spacing.roundToPx()
        val newConstraints = constraints.copy(minHeight = 0, minWidth = 0)
        val placeables = measurables.map { measurable -> measurable.measure(newConstraints) }

        val totalWidth = placeables.sumOf { it.width } + (placeables.size - 1) * spacingPx
        val maxHeight = placeables.maxOfOrNull { it.height } ?: 0

        layout(totalWidth, maxHeight) {
            var xOffset = 0
            placeables.forEach { placeable ->
                placeable.placeRelative(xOffset, 0)
                xOffset += placeable.width + spacingPx
            }
        }
    }
}

@Preview
@Composable
fun CustomRowLayoutExample() {
    CustomRowLayout(Modifier.size(200.dp).background(Color.LightGray).padding(16.dp), 16.dp) {
        Box(Modifier.size(50.dp).background(Color.Red))
        Box(Modifier.size(50.dp).background(Color.Green))
        Box(Modifier.size(50.dp).background(Color.Blue))
    }
}

@Preview(showBackground = true)
@Composable
fun SubcomposeLayoutCase(title: String = "title", content: String = "This is the content below the title.") {
    SubcomposeLayout { constraints ->
        val titlePlaceableList = subcompose("title") {
            Text(
                text = title,
                color = Color.White,
                modifier = Modifier.background(Color.Blue).padding(8.dp)
            )
        }.map { it.measure(constraints) }

        val contentPlaceableList = subcompose("content") {
            Text(
                text = content,
                color = Color.Black,
                modifier = Modifier.background(Color.LightGray).padding(8.dp)
            )
        }.map { it.measure(constraints.copy(minHeight = 0)) }

        val titleHeight = titlePlaceableList.maxOf { it.height }
        val contentHeight = contentPlaceableList.maxOf { it.height }
        val spaceHeight = 16.dp.roundToPx()
        // 设置布局的总高度和宽度
//        val layoutHeight = titleHeight + contentHeight + spaceHeight
        val layoutHeight = with(density) { 200.dp.roundToPx() }
//        val layoutWidth = max(titlePlaceableList.maxOf { it.width }, contentPlaceableList.maxOf { it.width })
        val layoutWidth = with(density) { 200.dp.roundToPx() }

        layout(layoutWidth, layoutHeight) {// 布局子组件
            var yOffset = 0

            titlePlaceableList.forEach { placeable ->// 布局标题
                placeable.placeRelative((layoutWidth - placeable.width) / 2, yOffset)
                yOffset += placeable.height
            }

            yOffset += spaceHeight// 添加间距

            contentPlaceableList.forEach { placeable ->// 布局内容
                placeable.placeRelative(0, yOffset)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LookaheadScopeCase() {
    LookaheadScope {
        val colorList = remember { listOf(Color.Red, Color.Green, Color.Blue) }
        val layoutContent = remember {
            movableContentOf {// 动态生成布局内容，支持在 Row 和 Column 间切换
                colorList.forEach { color -> Box(Modifier.size(50.dp).background(color)) }
            }
        }

        var isColumn by remember { mutableStateOf(true) }
        Box(
            Modifier.size(200.dp).clickable { isColumn = !isColumn },
            Alignment.Center
        ) {// 点击切换布局
            if (isColumn) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) { layoutContent() }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) { layoutContent() }
            }
        }
    }
}