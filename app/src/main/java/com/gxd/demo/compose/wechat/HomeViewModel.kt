package com.gxd.demo.compose.wechat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.gxd.demo.compose.ui.theme.MyTheme
import com.gxd.demo.compose.wechat.data.Chat
import com.gxd.demo.compose.wechat.data.ChatMessage
import com.gxd.demo.compose.wechat.data.User
import com.gxd.demo.compose.wechat.data.mock.Mock

class HomeViewModel : ViewModel() {
    val chatList by mutableStateOf(Mock.chatList)
    val contactList by mutableStateOf(Mock.contactList)
    var selectedTab by mutableIntStateOf(0)
    var theme by mutableStateOf(MyTheme.Theme.Light)
    var currentChat by mutableStateOf<Chat?>(null)
    var inChatPage by mutableStateOf(false)

    fun startChat(chat: Chat) {
        currentChat = chat
        inChatPage = true
    }

    fun endChat(): Boolean {
        if (inChatPage) {
            inChatPage = false
            return true
        } else {
            return false
        }
    }

    fun boom(chat: Chat) {
        chat.messageList += ChatMessage(User.Me, "\uD83D\uDCA3", "15:10").apply { read = true }
    }
}