package com.gxd.demo.compose.case

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationEndReason
import androidx.compose.animation.core.AnimationResult
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Preview
@Composable
fun ReboundCase() {
    var toggle by remember { mutableStateOf<Boolean?>(null) }
    val animatableX = remember { Animatable(0.dp, Dp.VectorConverter, label = "labelX") }
    val animatableY = remember { Animatable(0.dp, Dp.VectorConverter, label = "labelY") }

    BoxWithConstraints {
        val offsetX = remember(animatableX.value) {
            val maxOffsetWidth = maxWidth - 100.dp
            var cacOffsetX = animatableX.value
            while (cacOffsetX > maxOffsetWidth * 2) cacOffsetX -= maxOffsetWidth * 2
            if (cacOffsetX < maxOffsetWidth) cacOffsetX else maxOffsetWidth * 2 - cacOffsetX
        }
        val offsetY = remember(animatableY.value) {
            val maxOffsetHeight = maxHeight - 100.dp
            var cacOffsetY = animatableY.value
            while (cacOffsetY > maxOffsetHeight * 2) cacOffsetY -= maxOffsetHeight * 2
            if (cacOffsetY < maxOffsetHeight) cacOffsetY else maxOffsetHeight * 2 - cacOffsetY
        }
//        animatableX.updateBounds(0.dp, maxWidth - 100.dp)
//        animatableY.updateBounds(0.dp, maxHeight - 100.dp)

        Box(Modifier.fillMaxSize()) {
            Box(Modifier.size(100.dp).offset(offsetX, offsetY).background(Color.Green).clickable { toggle = !(toggle ?: false) })
        }
    }

    if (toggle == null) return

    val decay = remember { exponentialDecay<Dp>(1f, 1.2f) }
    LaunchedEffect(toggle) {
        launch {
            var result: AnimationResult<Dp, AnimationVector1D>? = null
            do {
                val initialVelocity = result?.endState?.velocity?.times(-1) ?: 3000.dp
                result = animatableX.animateDecay(initialVelocity, decay)
            } while (result?.endReason == AnimationEndReason.BoundReached)
        }
        launch {
            var result: AnimationResult<Dp, AnimationVector1D>? = null
            do {
                val initialVelocity = result?.endState?.velocity?.times(-1) ?: 4000.dp
                result = animatableY.animateDecay(initialVelocity, decay)
            } while (result?.endReason == AnimationEndReason.BoundReached)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Preview(showBackground = true)
@Composable
fun UpdateTransitionCase() {
    var targetState by remember { mutableStateOf(MyState.End) }
    val transitionState = remember { MutableTransitionState(MyState.Init) }
    transitionState.targetState = targetState
    val transition = updateTransition(transitionState, label = "transitionA")
    transition.AnimatedVisibility({ state -> state == MyState.End }) {
        Box(Modifier.size(50.dp).background(Color.Red))
    }
    val offsetX by transition.animateDp(label = "offsetX", transitionSpec = {
        when {
            MyState.Init isTransitioningTo MyState.Middle -> tween(1_000)
            MyState.Init isTransitioningTo MyState.End -> tween(2_000)
            MyState.Middle isTransitioningTo MyState.End -> spring()
            else -> tween(0)
        }
    }) { state ->
        when (state) {
            MyState.Init -> 0.dp
            MyState.Middle -> 100.dp
            MyState.End -> 200.dp
        }
    }
    val bgColor by transition.animateColor(label = "bgColor", transitionSpec = {
        when {
            MyState.Init isTransitioningTo MyState.Middle -> tween(1_000)
            MyState.Init isTransitioningTo MyState.End -> tween(2_000)
            MyState.Middle isTransitioningTo MyState.End -> spring()
            else -> tween(0)
        }
    }) { state ->
        when (state) {
            MyState.Init -> Color.Red
            MyState.Middle -> Color.Green
            MyState.End -> Color.Blue
        }
    }
    Box(Modifier.fillMaxSize()) {
        Box(Modifier.size(100.dp).offset(offsetX).background(bgColor).clickable { targetState = targetState.next() })
    }
}

enum class MyState {
    Init, Middle, End;

    fun next(): MyState = when (this) {
        Init -> Middle
        Middle -> End
        End -> Init
    }
}

@Preview
@Composable
fun AnimateVisibilityCase() {
    Row {
        var toggle by remember { mutableStateOf(true) }
        Button(modifier = Modifier.align(Alignment.CenterVertically), onClick = { toggle = !toggle }) { Text("切换") }
        AnimatedVisibility(
            toggle,
            enter = scaleIn(
                tween(2_000), transformOrigin = TransformOrigin(1f, 1f),
            ),
            exit = shrinkOut(
                tween(2_000), shrinkTowards = Alignment.Center, clip = false,
                targetSize = { IntSize(it.width / 2, it.height / 2) }
            )
        ) {
            Box(Modifier.size(100.dp).background(Color.Red))
        }
    }
}

@Preview
@Composable
fun CrossfadeCase() {
    Column {
        var targetState by remember { mutableStateOf(MyState.Init) }
        Crossfade(targetState, label = "crossfade") { state ->
            when (state) {
                MyState.Init -> Box(Modifier.size(100.dp).background(Color.Red))
                MyState.Middle -> Box(Modifier.size(80.dp).background(Color.Green))
                MyState.End -> Box(Modifier.size(120.dp).background(Color.Blue))
            }
        }
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = { targetState = targetState.next() }
        ) { Text("切换") }
    }
}

@Preview
@Composable
fun AnimatedContentCase() {
    Column {
        var targetState by remember { mutableStateOf(true) }
        AnimatedContent(targetState, transitionSpec = {
            when (targetState) {
                true -> fadeIn(tween(1_000)) togetherWith fadeOut(tween(2_000))
                false -> fadeIn(tween(3_000)) togetherWith fadeOut(tween(4_000))
            }
        }, label = "animatedContent") { state ->
            when (state) {
                true -> Box(Modifier.size(200.dp).background(Color.Red))
                false -> Box(Modifier.size(100.dp).background(Color.Green))
            }
        }
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = { targetState = !targetState }
        ) { Text("切换") }
    }
}