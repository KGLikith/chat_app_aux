package com.chat.server.repository

import com.chat.server.entity.MessageEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface MessageRepository : JpaRepository<MessageEntity, UUID>{
    fun findByConversationId(conversationId: Long): List<MessageEntity>
}