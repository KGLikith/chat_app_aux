package com.chat.server.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.UuidGenerator
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "messages")
data class MessageEntity(
    @Id
    @GeneratedValue
    @UuidGenerator
    val id: UUID? = null,

    val conversationId: UUID,

    val senderId: UUID,

    val content: String,

    val timestamp: Instant = Instant.now()
)