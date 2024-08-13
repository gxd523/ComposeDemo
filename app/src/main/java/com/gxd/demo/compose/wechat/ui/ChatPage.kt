package com.gxd.demo.compose.wechat.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gxd.demo.compose.R
import com.gxd.demo.compose.ui.theme.MyTheme
import com.gxd.demo.compose.wechat.HomeViewModel
import com.gxd.demo.compose.wechat.data.ChatMessage
import com.gxd.demo.compose.wechat.data.User
import com.gxd.demo.compose.wechat.data.mock.Mock
import kotlin.math.roundToInt

@Composable
fun ChatPage() {
    val viewModel: HomeViewModel = viewModel()
    val targetValue = if (viewModel.inChatPage) 0f else 1f
    val offsetPercent by animateFloatAsState(targetValue, label = "")
    val chat = viewModel.currentChat ?: return
    Column(Modifier.offsetPercent(offsetPercent).background(MyTheme.colorScheme.background).fillMaxSize()) {
        TopBar(chat.friend.name) { viewModel.endChat() }
        LazyColumn(Modifier.weight(1f)) {
            items(chat.messageList.size) { index ->
                val msg = chat.messageList[index]
                MessageItem(msg)
            }
        }
        ChatBottomBar {
        }
    }
}

@Composable
fun MessageItem(message: ChatMessage) {
    val isMe = message.from == User.Me
    Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start) {
        if (!isMe) Avatar(message)

        val bubbleColor = if (isMe) MyTheme.colorScheme.bubbleMe else MyTheme.colorScheme.bubbleOthers
        Text(
            message.text, color = if (isMe) MyTheme.colorScheme.textPrimaryMe else MyTheme.colorScheme.textPrimary,
            modifier = Modifier.drawBehind {
                val bubble = Path().apply {
                    val rect = RoundRect(
                        10.dp.toPx(),
                        0f,
                        size.width - 10.dp.toPx(),
                        size.height,
                        4.dp.toPx(),
                        4.dp.toPx()
                    )
                    addRoundRect(rect)
                    val x = if (isMe) size.width - 10.dp.toPx() else 10.dp.toPx()
                    val x1 = if (isMe) size.width - 5.dp.toPx() else 5.dp.toPx()
                    moveTo(x, 15.dp.toPx())
                    lineTo(x1, 20.dp.toPx())
                    lineTo(x, 25.dp.toPx())
                    close()
                }
                drawPath(bubble, bubbleColor)
            }.padding(20.dp, 10.dp)
        )

        if (isMe) Avatar(message)
    }
}

@Composable
fun Avatar(message: ChatMessage) {
    val isMe = message.from == User.Me
    Image(
        painterResource(message.from.avatar), contentDescription = message.from.name,
        Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(4.dp))
    )
}

@Composable
fun ChatBottomBar(onBombClicked: () -> Unit) {
    var editText by remember { mutableStateOf("") }
    Row(Modifier.background(MyTheme.colorScheme.bottomBar).fillMaxWidth().padding(4.dp, 0.dp)) {
        Icon(
            painterResource(R.drawable.ic_voice), null,
            Modifier.size(32.dp).padding(4.dp).align(Alignment.CenterVertically),
            tint = MyTheme.colorScheme.icon
        )
        BasicTextField(
            editText, { inputText -> editText = inputText },
            Modifier
                .weight(1f)
                .padding(4.dp, 8.dp)
                .height(40.dp)
                .align(Alignment.CenterVertically)
                .clip(RoundedCornerShape(4.dp))
                .background(MyTheme.colorScheme.textFieldBackground)
                .padding(start = 8.dp, top = 10.dp, end = 8.dp),
            cursorBrush = SolidColor(MyTheme.colorScheme.textPrimary)
        )
        Text(
            "\uD83D\uDCA3",
            Modifier.align(Alignment.CenterVertically).clickable { onBombClicked() }.padding(4.dp),
            fontSize = 24.sp,
        )
        Icon(
            painterResource(R.drawable.ic_add), null,
            Modifier.size(32.dp).padding(4.dp).align(Alignment.CenterVertically),
            tint = MyTheme.colorScheme.icon
        )
    }
}

fun Modifier.offsetPercent(offsetPercentX: Float = 0f, offsetPercentY: Float = 0f) =
    this.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            val offsetX = (offsetPercentX * placeable.width).roundToInt()
            val offsetY = (offsetPercentY * placeable.height).roundToInt()
            placeable.placeRelative(offsetX, offsetY)
        }
    }

@Preview
@Composable
private fun MessageItemPreview() {
    val message = Mock.chatList.firstOrNull()?.messageList?.firstOrNull() ?: return
    MessageItem(message)
}

@Preview
@Composable
private fun ChatBottomBarPreview() {
    ChatBottomBar {}
}

@Preview
@Composable
private fun ChatPagePreview() {
    val viewModel: HomeViewModel = viewModel()
    Mock.chatList.firstOrNull()?.let(viewModel::startChat)
    ChatPage()
}