# 🌍 FlowForge Travel Advisor Sample

This sample application demonstrates how to build a **Linear Workflow** using **FlowForge** integrated with **Spring Boot 4.0.4** and **Java 21**.

## 🎯 Project Objective
The goal is to showcase a real-world scenario where multiple external APIs are orchestrated using a reactive workflow. The application fetches country details, determines the current weather for its capital, and consolidates the information into a travel report.

### 🔄 Workflow Structure
The workflow follows a strict linear execution:
1.  **`fetchCountryInfo`**: Queries [REST Countries](https://restcountries.com/) for a given country name to get capital coordinates and currency data.
2.  **`fetchWeatherInfo`**: Uses the coordinates from Step 1 to query [Open-Meteo](https://open-meteo.com/) for current weather conditions.
3.  **`consolidateReport`**: Combines data from both previous steps into a final `TravelReport` object.

---

## 🛠️ Technology Stack
- **Framework**: Spring Boot 4.0.4
- **Language**: Java 21
- **Orchestration**: [FlowForge](https://royada-labs.github.io/flowforge/)
- **Reactive Stack**: Project Reactor (Mono/Flux)
- **HTTP Client**: Spring WebFlux (WebClient)
- **Build Tool**: Gradle

---

## 🚀 How to Run

### Prerequisites
- Java 21+ installed.
- Internet connection (to reach external APIs).

### Execution
From the project root directory, use the following Gradle command:

```bash
./gradlew run
```

### Passing a Custom Country
By default, the advisor looks for "Spain". You can search for any other country by passing it as an argument:

```bash
./gradlew run --args="Mexico"
./gradlew run --args="Japan"
./gradlew run --args="Germany"
```

---

## 📝 Configuration
- **Logging**: The application uses `AsyncLoggingWorkflowMonitor` to show the internal state of the workflow in the console.
- **Auto-configuration**: FlowForge components are automatically registered via the `flowforge-spring-boot-starter`.

---

## 📄 License
This sample is part of the [FlowForge Samples Repository](https://github.com/royada-labs/flowforge-samples).
