# CodePilot<>

**CodePilot<>** is an AI-powered codebase assistant built with **Java, Spring Boot, Spring AI, Gemini, PostgreSQL, and pgvector**.

It uses **Retrieval-Augmented Generation (RAG)** to retrieve relevant source code and provide grounded answers to developers' questions about their codebase.

## Features

* Project and source-code management
* Source-code chunking and indexing
* Semantic search using pgvector
* RAG-based codebase Q&A
* Source references in responses
* REST APIs with Swagger/OpenAPI
* Simple HTML/CSS/JavaScript frontend
* Docker-based PostgreSQL + pgvector setup

## Tech Stack

**Java 21 · Spring Boot · Spring AI · Gemini · PostgreSQL · pgvector · Maven · Docker · HTML/CSS/JavaScript**

## How CodePilot Works

CodePilot uses Spring Boot as the central backend layer. It handles REST APIs, business logic, database operations, and orchestration of the RAG and AI workflow.

1.Project and Source-Code Management

The frontend communicates with the Spring Boot backend through REST APIs.

The backend manages projects and their source-code files.

2.Code Indexing

Source files are processed and divided into smaller code chunks.

Each chunk is converted into an embedding using Spring AI.

The embeddings and associated source information are stored in PostgreSQL using pgvector.

3.User Question

A developer asks a natural-language question about the codebase.

Example:

How does authentication work in this project?

The question is received by a Spring Boot REST controller.

4.Semantic Retrieval

The question is converted into an embedding.

CodePilot compares the question embedding with the stored code embeddings using vector similarity search.

The most relevant code chunks are retrieved from PostgreSQL and pgvector.

5.RAG and AI Generation

The retrieved code chunks are provided as context to Gemini through Spring AI.

Gemini uses:

The developer's question
The retrieved code context

to generate a codebase-specific answer.

6.Response and Sources

Spring Boot returns the generated answer through the REST API.

The response also contains relevant source-file references so that developers can verify the explanation against the actual code.

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


The application follows a layered backend design so that controllers, business logic, data access, and AI-related responsibilities remain separated.
## RAG Workflow

```text
Source Code
    ↓
Chunking & Embeddings
    ↓
PostgreSQL + pgvector
    ↓
User Question
    ↓
Similarity Search
    ↓
Relevant Code Context
    ↓
Gemini
    ↓
Answer + Sources
```

## Project Structure

```text
CodePilot/
├── backend/
├── frontend/
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md
```

## Getting Started

### Prerequisites

* Java 21
* Maven
* Docker
* Gemini API key

### 1. Configure environment variables

Create a `.env` file based on `.env.example` and provide your local database credentials and Gemini API key.

> Never commit `.env` or API keys to GitHub.

### 2. Start PostgreSQL + pgvector

```bash
docker compose up -d
```

### 3. Run the application

```bash
cd backend
mvn spring-boot:run
```

The application runs on:

**[http://localhost:8080](http://localhost:8080)**


## Example

```text
How does authentication work in this project?
```

CodePilot retrieves the most relevant code from the indexed project and uses it as context to generate a grounded response.

## Future Improvements

* GitHub repository integration
* GitHub OAuth
* Automatic repository indexing
* Conversation history
* Streaming responses
* Improved code-aware chunking


