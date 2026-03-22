# 🚀 FlowForge Travel Advisor (Pure Java Version)

This sample demonstrates how to use **FlowForge** in a **Pure Java** project without any Spring dependencies.

## 🎯 Project Objective
Showcase manual orchestration and task definition using only `flowforge-core`. This version uses `java.net.http.HttpClient` for non-blocking API calls.

---

## 🚀 How to Run

### Execution
From the project directory:

```bash
cd java/travel-advisor
./gradlew run
```

### Passing a Custom Country
```bash
./gradlew run --args="Mexico"
```

---

## 🛠️ Implementation Details
- **HttpClient**: Uses Java 11+ native `HttpClient.sendAsync()`.
- **Reactor**: Tasks return `Mono`, wrapping the `CompletableFuture` from the HTTP client.
- **Manual Wiring**: Dependencies are defined by overriding the `dependencies()` method in each task.
- **Plan Builder**: Uses `WorkflowPlanBuilder` to assemble the execution graph from task descriptors.

---

## 📄 License
Part of [FlowForge Samples](https://github.com/royada-labs/flowforge-samples).
