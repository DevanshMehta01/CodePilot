# CodePilot<>

**CodePilot<>** is an AI-powered codebase assistant built with **Java 21, Spring Boot, Spring AI, Gemini, PostgreSQL, and pgvector**.

It uses **Retrieval-Augmented Generation (RAG)** to retrieve relevant source-code context and provide grounded answers to developers' questions about a codebase.

## Features

- RESTful backend built with Spring Boot
- Project and source-code management
- Source-code chunking and indexing
- Semantic search using PostgreSQL and pgvector
- RAG-based codebase question answering
- AI integration using Spring AI and Gemini
- Source-file references in responses
- Request validation and exception handling
- Swagger/OpenAPI documentation
- Simple HTML/CSS/JavaScript frontend
- Docker-based PostgreSQL and pgvector setup

## Tech Stack

**Backend:** Java 21, Spring Boot, Spring Data JPA, Maven

**AI:** Spring AI, Gemini, Embeddings, RAG

**Database:** PostgreSQL, pgvector

**API:** REST, Swagger/OpenAPI

**Frontend:** HTML, CSS, JavaScript

**Infrastructure:** Docker

## Architecture

```text
                         CodePilot
                             |
              +--------------+--------------+
              |                             |
          Frontend                    Spring Boot
       HTML/CSS/JavaScript               Backend
                                            |
                         +------------------+------------------+
                         |                  |                  |
                    REST APIs         Spring Data JPA      Spring AI
                         |                  |                  |
                         |             PostgreSQL            Gemini
                         |                  |
                         |               pgvector
                         |
                         +---------- RAG Pipeline
                                            |
                                            v
                                   Relevant Code Context
                                            |
                                            v
                                      AI-generated
                                         Answer



How CodePilot Works

CodePilot uses Spring Boot as the central backend layer. It handles REST APIs, business logic, database operations, and orchestration of the RAG and AI workflow.

1. Project and Source-Code Management

The frontend communicates with the Spring Boot backend through REST APIs.

The backend manages projects and their source-code files.

2. Code Indexing

Source files are processed and divided into smaller code chunks.

Each chunk is converted into an embedding using Spring AI.

The embeddings and associated source information are stored in PostgreSQL using pgvector.

3. User Question

A developer asks a natural-language question about the codebase.

Example:

How does authentication work in this project?

The question is received by a Spring Boot REST controller.

4. Semantic Retrieval

The question is converted into an embedding.

CodePilot compares the question embedding with the stored code embeddings using vector similarity search.

The most relevant code chunks are retrieved from PostgreSQL and pgvector.

5. RAG and AI Generation

The retrieved code chunks are provided as context to Gemini through Spring AI.

Gemini uses:

The developer's question
The retrieved code context

to generate a codebase-specific answer.

6. Response and Sources

Spring Boot returns the generated answer through the REST API.

The response also contains relevant source-file references so that developers can verify the explanation against the actual code.

RAG Flow
Source Code
     |
     v
Chunking
     |
     v
Embeddings
     |
     v
PostgreSQL + pgvector
     |
     v
User Question
     |
     v
Question Embedding
     |
     v
Vector Similarity Search
     |
     v
Relevant Code Context
     |
     v
Gemini through Spring AI
     |
     v
Answer + Source References
Spring Boot Responsibilities

Spring Boot forms the core of the CodePilot backend.

It is responsible for:

Exposing REST endpoints
Handling HTTP requests and responses
Request validation
Project and source-code management
Business and service-layer logic
Database interaction using Spring Data JPA
Vector-store integration
RAG workflow orchestration
AI integration through Spring AI
Exception handling
Swagger/OpenAPI integration

The application follows a layered backend design so that controllers, business logic, data access, and AI-related responsibilities remain separated.

Project Structure
CodePilot/
|
+-- backend/
|   +-- Spring Boot application
|
+-- frontend/
|   +-- HTML/CSS/JavaScript UI
|
+-- docker-compose.yml
+-- .env.example
+-- .gitignore
+-- README.md
Getting Started
Prerequisites

Make sure the following are installed:

Java 21
Maven
Docker
Gemini API key
1. Configure Environment Variables

Create a .env file based on .env.example.

Configure your PostgreSQL credentials and Gemini API key.

Never commit .env or API keys to GitHub.

2. Start PostgreSQL and pgvector

From the project root:

docker compose up -d

This starts the PostgreSQL database with pgvector.

3. Run the Backend

Open a terminal and run:

cd backend
mvn spring-boot:run

The application runs on:

http://localhost:8080

Example
Question
Where is the database connection configured?
CodePilot

CodePilot retrieves the most relevant code from the indexed project and generates an explanation using that retrieved context.

The response also provides references to the corresponding source files.

Why RAG?

A general-purpose LLM may not know the structure or implementation details of a specific codebase.

CodePilot addresses this by retrieving relevant parts of the actual source code before generating an answer.

This helps the AI provide answers based on the project's available code instead of relying only on its general knowledge.

Future Improvements
GitHub repository integration
GitHub OAuth
Automatic repository indexing
Conversation history
Streaming responses
Improved code-aware chunking
