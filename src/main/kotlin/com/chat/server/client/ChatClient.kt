package com.chat.server.client

import com.chat.server.grpc.*
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.*
import java.util.*

fun main() = runBlocking {

    val channel = ManagedChannelBuilder
        .forAddress("localhost", 9090)
        .usePlaintext()
        .build()

    val stub = ChatServiceGrpcKt.ChatServiceCoroutineStub(channel)

    val scanner = Scanner(System.`in`)

    print("Enter your user ID (UUID) [press Enter to auto-generate]: ")
    val inputId = if (scanner.hasNextLine()) scanner.nextLine() else ""
    val userId = if (inputId.isBlank()) UUID.randomUUID().toString() else inputId

    println("Your userId: $userId")

    var currentConversation: String? = null
    var streamJob: Job? = null

    println(
        """
        Commands:
        /join <conversation_id>
        /leave
        /history <conversation_id>
        /send <message>
        /exit
        """.trimIndent()
    )

    while (true) {
        print("> ")

        if (!scanner.hasNextLine()) {
            println("\nInput stream closed. Exiting...")
            break
        }

        val input = scanner.nextLine().trim()

        when {
            input.startsWith("/join") -> {
                val parts = input.split(" ")

                if (parts.size < 2) {
                    println("Usage: /join <conversation_id>")
                    continue
                }

                val conversationId = parts[1]

                streamJob?.cancel()

                currentConversation = conversationId

                println("Joined conversation: $conversationId")

                streamJob = launch(Dispatchers.IO) {
                    try {
                        stub.streamMessages(
                            StreamRequest.newBuilder()
                                .setConversationId(conversationId)
                                .build()
                        ).collect {
                            println("\n[${it.conversationId}] ${it.senderId}: ${it.content}")
                            print("> ")
                        }
                    } catch (e: Exception) {
                        println("\nStream closed: ${e.message}")
                    }
                }
            }

            input.startsWith("/history") -> {
                if (currentConversation == null) {
                    println("Join a conversation first")
                    continue
                }

                try {
                    val response = stub.getMessages(
                        GetMessagesRequest.newBuilder()
                            .setConversationId(currentConversation)
                            .build()
                    )

                    println("---- Message History ----")

                    response.messagesList.forEach {
                        println("[${it.conversationId}] ${it.senderId}: ${it.content}")
                    }

                    println("-------------------------")

                } catch (e: Exception) {
                    println("Failed to fetch history: ${e.message}")
                }
            }

            input == "/leave" -> {
                streamJob?.cancel()
                streamJob = null
                currentConversation = null
                println("Left conversation")
            }

            input.startsWith("/send") -> {
                if (currentConversation == null) {
                    println("Join a conversation first")
                    continue
                }

                val message = input.removePrefix("/send").trim()

                if (message.isBlank()) {
                    println("Message cannot be empty")
                    continue
                }

                try {
                    val response = stub.sendMessage(
                        SendMessageRequest.newBuilder()
                            .setConversationId(currentConversation)
                            .setSenderId(userId)
                            .setContent(message)
                            .build()
                    )

                    println("Sent: ${response.message.content}")
                } catch (e: Exception) {
                    println("Failed to send: ${e.message}")
                }
            }

            input == "/exit" -> {
                println("Exiting...")
                streamJob?.cancel()
                channel.shutdownNow()
                break
            }

            else -> {
                println("Unknown command")
            }
        }
    }
}