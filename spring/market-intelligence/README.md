# 🌍 FlowForge Global Market Intelligence (Plain Spring version)

This version demonstrates how to use **FlowForge** in a **non-Spring Boot** environment while still leveraging complex features like:
- **Reactive R2DBC**: Manual setup of `ConnectionFactory` and `DatabaseClient`.
- **Annotation-based Config**: Using `AnnotationConfigApplicationContext`.
- **12-Task Workflow**: Identical complexity to the Boot version.

## 🚀 How to Run

```bash
cd spring/market-intelligence
./gradlew run --args="1"
```

## 🛠️ Key Differences from Boot
- **AppConfig.java**: Manually imports `FlowForgeAutoConfiguration` and sets up the R2DBC `ConnectionFactory`.
- **Main.java**: Explicitly initializes the Spring context and manually triggers the `schema.sql` population before running the workflow.
- **Dependencies**: Uses `spring-context` and `spring-data-r2dbc` directly without the Spring Boot BOM.
