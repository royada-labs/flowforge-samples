# 🚀 FlowForge Travel Advisor (Plain Spring Version)

This sample demonstrates how to use **FlowForge** in a **Plain Spring** project (without Spring Boot).

## 🎯 Project Objective
Showcase manual Spring context setup (`AnnotationConfigApplicationContext`) while leveraging FlowForge's auto-configuration through `@Import`.

---

## 🚀 How to Run

### Execution
From the project directory:

```bash
cd spring/travel-advisor
./gradlew run
```

### Passing a Custom Country
```bash
./gradlew run --args="Italy"
```

---

## 🛠️ Key Differences from Boot Version
- No `application.properties` (Standard bean definition in `AppConfig`).
- Manual context initialization in `Main.java`.
- Explicit use of Jackson for `WebClient` in the build file.

---

## 📄 License
Part of [FlowForge Samples](https://github.com/royada-labs/flowforge-samples).
