# 🌊 FlowForge Samples Repository

Welcome to the official samples repository for **FlowForge**, the reactive workflow orchestration engine for Java and Spring.

This repository contains multiple ways to integrate FlowForge into your applications, ranging from pure Java to futuristic Spring Boot versions.

---

## 📂 Repository Structure

| Project Path | Technology Stack | Description |
| :--- | :--- | :--- |
| [`spring-boot/basic-demo`](./spring-boot/basic-demo) | Spring Boot 4.0.4 | Basic E-commerce workflow with retries and timeouts. |
| [`spring-boot/travel-advisor`](./spring-boot/travel-advisor) | Spring Boot 4.0.4 + WebFlux | Real API orchestration (Travel info + Weather). |
| [`spring-boot/movie-expert`](./spring-boot/movie-expert) | Spring Boot 4.0.4 + WebFlux | **Parallel Workflow** (Fork/Join) with OMDb real data. |
| [`spring/basic-demo`](./spring/basic-demo) | Plain Spring 6.x | Same E-commerce workflow but for non-Boot Spring apps. |
| [`spring/travel-advisor`](./spring/travel-advisor) | Plain Spring 6.x | Travel Advisor using manual Spring context. |
| [`spring/movie-expert`](./spring/movie-expert) | Plain Spring 6.x | Movie Expert with manual context and OMDb. |
| [`java/basic-demo`](./java/basic-demo) | Pure Java 21 | Manual FlowForge setup without any Spring dependency. |
| [`java/travel-advisor`](./java/travel-advisor) | Pure Java 21 | Travel Advisor using native Java HttpClient. |
| [`java/movie-expert`](./java/movie-expert) | Pure Java 21 | Movie Expert with parallel pure Java tasks. |

---

## 🚦 Getting Started

### Prerequisites
- **Java 21**: All projects are built on the latest LTS.
- **Gradle**: Wrapper included in each subproject.

### Running a Sample
Each subproject has its own `README.md` with specific instructions. Generally, you can run any sample using:

```bash
cd <project-path>
./gradlew run
```

---

## 🔗 Documentation
For more information about FlowForge, visit the [Official Documentation](https://royada-labs.github.io/flowforge/).

---

## 📄 License
MIT License.
