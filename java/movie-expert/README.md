# 🎬 FlowForge Movie Expert (Pure Java Version)

This sample demonstrates a **Parallel Workflow** (Fork-Join) using FlowForge in a pure Java environment without any Spring dependencies.

## 🎯 Project Objective
Showcase manual orchestration, task definition, and dependency management using `flowforge-core`.

### Parallel Execution (Fork-Join):
1.  **`FetchMovieMetadataTask`**: Sequential start.
2.  **`FetchCastTask`** & **`FetchRatingsTask`**: 🔌 **Parallel Branches**.
3.  **`ConsolidateMovieReportTask`**: 🧩 **Join Point**.

---

## 🚀 How to Run

### Execution
From the project directory:

```bash
cd java/movie-expert
./gradlew run
```

### Passing a Custom Movie
```bash
./gradlew run --args="The Dark Knight"
```

---

## 🛠️ Implementation Details
- **No Spring**: Uses `ReactiveWorkflowOrchestrator` builder.
- **Async HTTP**: Uses Java 11+ `HttpClient` for non-blocking I/O.
- **Parallel Scheduling**: Tasks are scheduled on `Schedulers.parallel()` for true concurrency.

---

## 📄 License
Part of [FlowForge Samples](https://github.com/royada-labs/flowforge-samples).
