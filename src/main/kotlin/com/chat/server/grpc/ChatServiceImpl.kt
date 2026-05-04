package com.chat.server.grpc

import com.chat.server.service.MessageBroadcaster
import com.chat.server.service.MessageService
import io.grpc.stub.StreamObserver
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import net.devh.boot.grpc.server.service.GrpcService
import com.google.protobuf.Timestamp


@GrpcService
class ChatServiceImpl(
    private val messageService: MessageService,
    private val broadcaster: MessageBroadcaster
) : ChatServiceGrpc.ChatServiceImplBase() {

    override fun sendMessage(request: SendMessageRequest, responseObserver: StreamObserver<SendMessageResponse>) {

        val message = messageService.sendMessage(request)

        val response = SendMessageResponse.newBuilder()
            .setMessage(message)
            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    override fun getMessages(request: GetMessagesRequest, responseObserver: StreamObserver<GetMessagesResponse>) {

        val messages = messageService.getMessages(request.conversationId)

        val response = GetMessagesResponse.newBuilder()
            .addAllMessages(messages)
            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    override fun streamMessages(request: StreamRequest, responseObserver: StreamObserver<ChatMessage>) {

        val flow = broadcaster.getFlow(request.conversationId)

        runBlocking {
            flow.collect {
                responseObserver.onNext(it)
            }
        }
    }
}