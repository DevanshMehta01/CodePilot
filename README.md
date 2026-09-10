	````markdown
	# CodePilot<>

	**CodePilot<>** is an AI-powered codebase assistant built with **Java 21, Spring Boot, Spring AI, Gemini, PostgreSQL, and pgvector**.

	It uses **Retrieval-Augmented Generation (RAG)** to retrieve relevant source-code context and provide grounded answers to developers' questions about a codebase.

	## Features

	- RESTful backend built with **Spring Boot**
	- Project and source-code management
	- Source-code chunking and indexing
	- Semantic search using **PostgreSQL + pgvector**
	- RAG-based codebase question answering
	- AI integration using **Spring AI + Gemini**
	- Source-file references in responses
	- Swagger/OpenAPI documentation
	- Simple HTML/CSS/JavaScript frontend
	- Docker-based PostgreSQL + pgvector setup

	## Tech Stack

	**Backend:** Java 21, Spring Boot, Spring AI, Maven  
	**AI:** Gemini, Retrieval-Augmented Generation (RAG), Embeddings  
	**Database:** PostgreSQL, pgvector, Spring Data JPA  
	**API:** REST, Swagger/OpenAPI  
	**Frontend:** HTML, CSS, JavaScript  
	**Infrastructure:** Docker

	## Architecture

	```text
	                    CodePilot
	                        │
	        ┌───────────────┴───────────────┐
	        │                               │
	   Frontend                         Spring Boot
	 HTML/CSS/JS                         Backend
	                                        │
	                    ┌───────────────────┼───────────────────┐
	                    │                   │                   │
	               REST APIs          Spring Data JPA      Spring AI
	                    │                   │                   │
	                    │              PostgreSQL          Gemini
	                    │                   │
	                    │                pgvector
	                    │
	                    └──────── RAG Pipeline ────────┐
	                                                    │
	                                      Retrieve relevant
	                                         code context
	                                                    │
	                                                    ↓
	                                              AI-generated
	                                                 answer
	````

	## How It Works

	CodePilot uses **Spring Boot as the main backend application**, handling REST APIs, business logic, database interaction, and integration with the AI/RAG pipeline.

	### 1. Project & Code Management

	The Spring Boot REST API receives project and source-code data from the frontend and manages the application workflow.

	### 2. Code Indexing

	Source files are divided into smaller code chunks. **Spring AI** is used to generate embeddings for these chunks, which are stored in **PostgreSQL with pgvector**.

	### 3. User Question

	A developer asks a natural-language question about the codebase:

	```text
	How does authentication work in this project?
	```

	The request is received by a **Spring Boot REST controller**.

	### 4. Retrieval

	Spring AI converts the question into an embedding and performs a vector similarity search against pgvector.

	The most relevant code chunks are retrieved as context.

	### 5. AI Generation

	The retrieved code context is passed to **Gemini through Spring AI**.

	Gemini generates an answer grounded in the retrieved project context.

	### 6. Response

	Spring Boot processes the AI response and returns the answer and relevant source references through the REST API.

	```text
	Developer
	    ↓
	Frontend
	    ↓
	Spring Boot REST API
	    ↓
	RAG Service
	    ↓
	Spring AI
	    ├── Embedding Model
	    ├── pgvector Similarity Search
	    └── Gemini
	    ↓
	Answer + Source References
	    ↓
	Frontend
	```

	## Spring Boot Responsibilities

	Spring Boot acts as the central backend layer and is responsible for:

	* Exposing REST endpoints
	* Handling HTTP requests and responses
	* Request validation
	* Project and source-code management
	* Business/service-layer logic
	* Database interaction through Spring Data JPA
	* Vector-store integration
	* RAG orchestration
	* AI integration through Spring AI
	* Exception handling
	* API documentation with Swagger/OpenAPI

	This keeps the frontend, business logic, database, and AI components separated into manageable layers.

	## Project Structure

	```text
	CodePilot/
	├── backend/
	│   └── Spring Boot application
	├── frontend/
	│   └── HTML/CSS/JavaScript UI
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

	### 1. Configure Environment Variables

	Create a `.env` file based on `.env.example` and configure the PostgreSQL and Gemini credentials.

	> Never commit `.env` or API keys to GitHub.

	### 2. Start PostgreSQL + pgvector

	```bash
	docker compose up -d
	```

	### 3. Run the Backend

	```bash
	cd backend
	mvn spring-boot:run
	```

	Application:

	**[http://localhost:8080](http://localhost:8080)**

	
	## Example

	```text
	Question:
	Where is the database connection configured?

	CodePilot:
	Retrieves the relevant code from the indexed project and
	generates an explanation with the corresponding source reference.
	```

	## Future Improvements

	* GitHub repository integration
	* GitHub OAuth
	* Automatic repository indexing
	* Conversation history
	* Streaming responses
	* Improved code-aware chunking
