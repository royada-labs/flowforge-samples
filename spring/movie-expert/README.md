# 🎬 FlowForge Movie Expert (Plain Spring Version)

This sample demonstrates a **Parallel Workflow** using FlowForge in a plain Spring application (no Spring Boot).

## 🎯 Project Objective
Showcase manual Spring context initialization and component scanning with FlowForge integration.

### Workflow Structure (Fork-Join):
1.  **`fetchMovieMetadata`**: Entry point. Gets basic info.
2.  **`fetchCastInfo`** & **`fetchRatingInfo`**: 🔌 **Parallel Execution**.
3.  **`generateMovieReport`**: 🧩 **Consolidation**.

---

## 🚀 How to Run

### Execution
From the project directory:

```bash
cd spring/movie-expert
./gradlew run
```

### Passing a Custom Movie
```bash
./gradlew run --args="Interstellar"
```

---

## 🛠️ Key Features
- **Plain Spring**: Uses `AnnotationConfigApplicationContext` and `@Import`.
- **Parallel Processing**: Automatically handles background thread management for independent tasks.
- **Real OMDb API Integration**: Real actors, ratings, and plot details.

---

## 📄 License
Part of [FlowForge Samples](https://github.com/royada-labs/flowforge-samples).
