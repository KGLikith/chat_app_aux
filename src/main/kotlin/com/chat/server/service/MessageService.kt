package com.chat.server.service

import com.chat.server.entity.MessageEntity
import com.chat.server.grpc.*
import com.chat.server.repository.MessageRepository
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class MessageService(
    private val messageRepository: MessageRepository,
    private val broadcaster: MessageBroadcaster
) {
    fun sendMessage(request: SendMessageRequest): ChatMessage {
        val entity = MessageEntity(
            conversationId = request.conversationId,
            senderId = UUID.fromString(request.senderId),
            content = request.content
        )

        val saved = messageRepository.save(entity)

        val message = ChatMessage.newBuilder()
            .setId(saved.id!!.toString())
            .setConversationId(saved.conversationId.toString())
            .setSenderId(saved.senderId.toString())
            .setContent(saved.content)
            .build()

        runBlocking {
            broadcaster.getFlow(saved.conversationId).emit(message)
        }

        return message
    }

    fun getMessages(conversationId: String): List<ChatMessage> {
        return messageRepository.findByConversationId(conversationId)
            .map {
                ChatMessage.newBuilder()
                    .setId(it.id!!.toString())
                    .setConversationId(it.conversationId)
                    .setSenderId(it.senderId.toString())
                    .setContent(it.content)
                    .build()
            }
    }
}