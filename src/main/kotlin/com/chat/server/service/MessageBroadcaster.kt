package com.chat.server.service

import com.chat.server.grpc.ChatMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import org.springframework.stereotype.Component
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Component
class MessageBroadcaster {
    private val conversationFlows = ConcurrentHashMap<UUID, MutableSharedFlow<ChatMessage>>()

    fun getFlow(conversationId: UUID): MutableSharedFlow<ChatMessage> {
        return conversationFlows.computeIfAbsent(conversationId) {
            MutableSharedFlow(
                replay = 0,
                extraBufferCapacity = 100
            )
        }
    }
}