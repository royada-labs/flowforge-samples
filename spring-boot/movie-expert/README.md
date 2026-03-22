# 🎬 FlowForge Movie Expert (Spring Boot Version)

This sample demonstrates a **Parallel Workflow** using FlowForge in a Spring Boot application.

## 🎯 Project Objective
Showcase the ability to execute multiple tasks in parallel and consolidate their results into a single report. It models a "Movie Expert" system that fetches metadata, cast info, and ratings simultaneously.

### Workflow Structure (Fork-Join):
1.  **`fetchMovieMetadata`**: Entry point. Gets basic info.
2.  **`fetchCastInfo`** & **`fetchRatingInfo`**: 🔌 **Parallel Execution**. Both depend on metadata.
3.  **`generateMovieReport`**: 🧩 **Consolidation**. Depends on BOTH parallel steps.

---

## 🚀 How to Run

### Execution
From the project directory:

```bash
cd spring-boot/movie-expert
./gradlew run
```

### Passing a Custom Movie
```bash
./gradlew run --args="Interstellar"
```

---

## 🛠️ Key Features
- **Parallel Processing**: Automatically handles background thread management for independent tasks.
- **Fluent DSL**: Define the workflow graph using a Spring Bean.
- **Reactive Models**: Uses mock services with `delayElement()` to simulate real network latency.

---

## 📄 License
Part of [FlowForge Samples](https://github.com/royada-labs/flowforge-samples).
