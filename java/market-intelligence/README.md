# 🌍 FlowForge Global Market Intelligence (Pure Java version)

This is the **"Extreme"** version of the sample, designed to show FlowForge working with **zero frameworks**:
- **Native Java 21 HttpClient**: Asynchronous HTTP calls using `HttpRequest` and `HttpResponse`.
- **Raw R2DBC SPI**: Manual SQL interactions without Spring Data or DatabaseClient.
- **Manual Jackson**: Deserialization of API responses using a standalone `ObjectMapper`.
- **Explicit Graph**: Workflow plan built by manually defining task dependencies.

## 🚀 How to Run

```bash
cd java/market-intelligence
./gradlew run --args="1"
```

## 🛠️ Key Features
- **12 Tasks**: Detailed state-of-the-art workflow.
- **Explicit Parallelism**: Orchestrator handles parallel branches based on the dependency graph.
- **Reactive Stream Orchestration**: Using Project Reactor to bridge native Java Futures with the FlowForge engine.
