# Real-Time Chat Application

This is a real-time chat application built using gRPC for communication, Kotlin with Spring Boot for the backend, and PostgreSQL for message persistence. It supports multiple clients, conversation-based messaging, and message history retrieval.

## Running with Docker

1. **Build the project**
   ```bash
   ./gradlew build -x test
   ```

2. Start server and database  
    ```bash
    docker compose up --build
    ```

## Services

* **gRPC Server:** localhost:9090
* **PostgreSQL:**
    * **Host:** localhost
    * **Port:** 5432
    * **Database:** chatdb
    * **User:** postgres
    * **Password:** password

## Running Clients

Open separate terminals and run:
```bash
./gradlew runClient --console=plain --quiet
```

Run multiple clients to simulate multiple users.

## Client Commands
* `/join <conversation_id>` Join or switch conversation  
* `/leave` Leave current conversation  
* `/send <message>` Send message
* `/history <conversation_id>` View message history
* `/exit` Exit client

## Functionality
* Real-time messaging using gRPC streaming
* Multiple clients can join the same conversation
* Messages are stored in PostgreSQL
* Message history can be retrieved using /history
* Conversations are identified using a conversation_id

## To reset the database:
```bash
docker compose down -v
