# Project Documentation

## Overview
This repository contains a full-stack chatbot application with:

- `backend/`: Spring Boot service that exposes `/api/chat`
- `frontend/`: Next.js application that calls the backend API
- `docker-compose.yml`: optional convenience for running both services together

The backend is configured to call a local Ollama instance at `http://localhost:11434/v1/chat/completions` and uses an injected model name via Spring Boot properties.

---

## Backend Architecture

### Key files

- `backend/src/main/java/com/example/assistant/AssistantApplication.java`
  - Spring Boot entry point with `main()`.

- `backend/src/main/java/com/example/assistant/controller/ChatController.java`
  - REST controller exposing `POST /api/chat`.
  - Accepts `ChatRequest` and returns `ChatResponse`.

- `backend/src/main/java/com/example/assistant/service/ChatService.java`
  - Business logic layer.
  - Delegates message handling to `OllamaClient`.

- `backend/src/main/java/com/example/assistant/client/OllamaClient.java`
  - Responsible for calling the Ollama HTTP API.
  - Reads configuration from Spring properties:
    - `ollama.url`
    - `ollama.model`
    - `ollama.models.url`
  - Builds a chat request payload with `messages` and sends it to Ollama.
  - Parses the JSON response and returns the model's `content`.
  - Handles `404 model not found` errors and reports installed models.

- `backend/src/main/java/com/example/assistant/dto/ChatRequest.java`
  - Records the shape of the request body:
    - `prompt`

- `backend/src/main/java/com/example/assistant/dto/ChatResponse.java`
  - Records the shape of the response body:
    - `response`

- `backend/src/main/resources/application.properties`
  - Default backend configuration.
  - Contains Ollama settings and model defaults.

### Backend behavior

1. Frontend sends a `POST` request to `http://localhost:8080/api/chat`.
2. `ChatController.chat()` receives `ChatRequest`.
3. `ChatService.askAI()` forwards the prompt to `OllamaClient.sendPrompt()`.
4. `OllamaClient` sends the HTTP request to Ollama.
5. The backend returns the extracted `content` as `ChatResponse`.

---

## Frontend Architecture

### Key files

- `frontend/app/page.tsx`
  - The Next.js home page.
  - Renders the `ChatWindow` component.

- `frontend/components/ChatWindow.tsx`
  - React UI for the chat.
  - Contains a textarea, send button, and response display.
  - Calls `sendMessage()` from `frontend/services/api.ts`.

- `frontend/services/api.ts`
  - Sends a `POST` request to `http://localhost:8080/api/chat`.
  - Serializes `{ prompt }` and returns the parsed JSON response.

### Frontend behavior

1. User enters a prompt into the textarea.
2. Clicking Send calls `handleSend()`.
3. `handleSend()` uses `sendMessage(prompt)` to call the backend.
4. Response is shown in the UI.

---

## Configuration

### Backend properties

The backend uses Spring Boot properties in `backend/src/main/resources/application.properties`:

```properties
ollama.url=http://localhost:11434/v1/chat/completions
ollama.models.url=http://localhost:11434/v1/models
ollama.model=llama3
```

### Environment variables

You can override these with environment variables:

- `OLLAMA_URL`
- `OLLAMA_MODELS_URL`
- `OLLAMA_MODEL`

Example in PowerShell:

```powershell
$env:OLLAMA_URL = "http://localhost:11434/v1/chat/completions"
$env:OLLAMA_MODEL = "llama3"
mvn spring-boot:run
```

> Note: Spring Boot maps environment variables to property names automatically when the variable is uppercase and underscores are used.

---

## Running Locally

### Backend

1. Open a terminal in `backend/`
2. Build the project:
   ```powershell
   mvn clean package
   ```
3. Run the backend:
   ```powershell
   mvn spring-boot:run
   ```

The backend listens on `http://localhost:8080`.

### Frontend

1. Open a terminal in `frontend/`
2. Install dependencies:
   ```powershell
   npm install
   ```
3. Start the frontend:
   ```powershell
   npm run dev
   ```

The frontend runs on `http://localhost:3000`.

---

## Docker Setup

### Backend Dockerfile

- `backend/Dockerfile`
- Uses `eclipse-temurin:21`
- Copies `target/*.jar` into the image
- Runs the jar using `java -jar /app.jar`

#### Build and run backend container

```powershell
cd backend
mvn clean package
docker build -t assistant-backend .

docker run -p 8080:8080 --name assistant-backend \
  -e OLLAMA_URL=http://host.docker.internal:11434/v1/chat/completions \
  -e OLLAMA_MODEL=llama3 \
  assistant-backend
```

> On Windows, use `host.docker.internal` so the container can reach the Ollama instance running on the host.

### Frontend Dockerfile

- `frontend/Dockerfile`
- Uses `node:20`
- Installs dependencies and builds the Next.js app
- Runs the app with `npm start`

#### Build and run frontend container

```powershell
cd frontend
docker build -t assistant-frontend .

docker run -p 3000:3000 --name assistant-frontend assistant-frontend
```

### Docker Compose

The repository already contains `docker-compose.yml`:

```yaml
version: '3'
services:
  backend:
    build: ./backend
    ports:
      - "8080:8080"
  frontend:
    build: ./frontend
    ports:
      - "3000:3000"
    depends_on:
      - backend
```

#### Run with Docker Compose

```powershell
docker-compose up --build
```

If Ollama runs on the host, the backend container still needs to connect to it. Add an env file or override the service with `host.docker.internal`:

```yaml
services:
  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      - OLLAMA_URL=http://host.docker.internal:11434/v1/chat/completions
      - OLLAMA_MODEL=llama3
```

---

## Troubleshooting

### Ollama model not found
Run:

```powershell
ollama list
```

Then set the exact model name in `OLLAMA_MODEL`.

### Backend cannot reach Ollama from Docker
Use `host.docker.internal` inside the container.

### Backend configuration file
The backend loads `application.properties` from `backend/src/main/resources`. If you prefer a `.env` workflow, set environment variables before running Docker or Maven.

---

## Helpful Notes

- The frontend currently posts to the backend at `http://localhost:8080/api/chat`.
- If you want to change the API URL in the frontend, update `frontend/services/api.ts`.
- The backend currently expects `ChatRequest` JSON of the form:
  ```json
  { "prompt": "Hello" }
  ```
- Backend response JSON is:
  ```json
  { "response": "…" }
  ```

---

## Developer Reference

### Backend classes

#### `AssistantApplication`
- Location: `backend/src/main/java/com/example/assistant/AssistantApplication.java`
- Role: Spring Boot application entry point.
- Behavior: `SpringApplication.run(AssistantApplication.class, args)` boots the embedded Tomcat server and initializes Spring context.

#### `ChatController`
- Location: `backend/src/main/java/com/example/assistant/controller/ChatController.java`
- Role: REST API layer.
- Main endpoint: `POST /api/chat`
- Input: `ChatRequest` record parsed from JSON.
- Output: `ChatResponse` record returned as JSON.
- Flow: receives the prompt, forwards to `ChatService`, and returns the result.

#### `ChatService`
- Location: `backend/src/main/java/com/example/assistant/service/ChatService.java`
- Role: service / business logic layer.
- Responsibility: abstract the AI request processing away from the controller.
- Behavior: calls `OllamaClient.sendPrompt(prompt)` to get the model reply.

#### `OllamaClient`
- Location: `backend/src/main/java/com/example/assistant/client/OllamaClient.java`
- Role: external API client.
- Responsibility: send HTTP requests to Ollama and parse responses.
- Configuration:
  - `ollama.url` → chat completions endpoint
  - `ollama.models.url` → models discovery endpoint
  - `ollama.model` → selected model name
- Processing steps:
  1. Build request payload with `model` and `messages`.
  2. POST to Ollama.
  3. Parse `choices[0].message.content` from JSON.
  4. Handle `404` model errors and return a helpful message.

#### `ChatRequest`
- Location: `backend/src/main/java/com/example/assistant/dto/ChatRequest.java`
- Role: request DTO.
- Structure: `record ChatRequest(String prompt)`.
- Usage: automatically mapped from incoming JSON by Spring Boot.

#### `ChatResponse`
- Location: `backend/src/main/java/com/example/assistant/dto/ChatResponse.java`
- Role: response DTO.
- Structure: `record ChatResponse(String response)`.
- Usage: returned by `ChatController.chat()` as JSON.

---

### Frontend components

#### `page.tsx`
- Location: `frontend/app/page.tsx`
- Role: page entry point for the Next.js app.
- Behavior: renders the page shell with the app title and `ChatWindow`.
- Notes: this file is under the Next.js App Router and is server-rendered by default.

#### `ChatWindow`
- Location: `frontend/components/ChatWindow.tsx`
- Role: user chat UI.
- State:
  - `prompt` → current text in the textarea.
  - `response` → AI response shown after submission.
- Behavior:
  1. User types a prompt.
  2. `handleSend()` calls `sendMessage(prompt)`.
  3. Updates `response` with backend output.
- Important: UI currently does not handle loading/error states; this is a good extension point.

#### `sendMessage()`
- Location: `frontend/services/api.ts`
- Role: API client helper.
- Behavior: sends a `POST` request to the backend at `http://localhost:8080/api/chat`.
- Request body: `{ prompt }`
- Response: expected `{ response: string }`.

---

### How the pieces connect

1. `ChatWindow` calls `sendMessage(prompt)`.
2. `sendMessage()` sends a POST to backend `http://localhost:8080/api/chat`.
3. `ChatController.chat()` receives the prompt and maps it to `ChatRequest`.
4. `ChatService.askAI()` forwards to `OllamaClient.sendPrompt(prompt)`.
5. `OllamaClient` calls Ollama and returns the model text.
6. The backend returns `ChatResponse`.
7. `ChatWindow` displays `response`.
