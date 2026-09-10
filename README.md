# CodePilot

**CodePilot** is an AI-powered codebase assistant built with **Java, Spring Boot, Spring AI, Gemini, PostgreSQL, and pgvector**.

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


