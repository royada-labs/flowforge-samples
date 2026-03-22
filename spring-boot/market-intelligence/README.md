# 🌍 FlowForge Global Market Intelligence (High Complexity Sample)

This application pushes **FlowForge** to its limits by implementing a 12-task workflow with multi-level parallelism, reactive database access (R2DBC), and conditional execution.

## 🎯 Architecture & Workflow
The workflow analyzes a product's market position globally through several stages:

### 1. Sequential Initialization
- `ValidateProduct` & `FetchBaseDetails`: Sets up the core product data (via **FakeStoreAPI**).
- `CheckInventoryDB`: Real-time stock check using **Reactive SQL (R2DBC + H2)**.

### 2. Massively Parallel Forks (3 Branches)
- **Branch A (Nested parallel)**: Checks multiple competitor prices concurrently, then joins them to calculate a competition index.
- **Branch B (Sequential in fork)**: Fetches social mentions and runs an AI sentiment analysis.
- **Branch C (External API)**: Fetches live currency exchange rates.

### 3. Conditional & Consolidation
- **Risk Assessment**: **Conditional step** that only activates for high-value items (>$500).
- **Consolidation**: Joins all 10 preceding data points into a single "Master Intelligence Report".
- **Finalization**: Cleanup and report delivery.

---

## 🚀 How to Run

### Run for Low-Value Item (Skipping Risk Assessment)
```bash
cd spring-boot/market-intelligence
./gradlew bootRun --args="1"
```

### Run for High-Value Item (Executing Risk Assessment)
```bash
./gradlew bootRun --args="14"
```

---

## 🛠️ Tech Features Demonstrated
- **Spring Data R2DBC**: Integration with reactive SQL repositories inside FlowTasks.
- **Nested Forking**: Defining forks within parallel branches.
- **Selective Join**: Using `ReactiveExecutionContext` to join disparate task results.
- **Error Resilience**: Using `onErrorResume` to maintain workflow continuity even if external APIs fail.

---

## 📄 License
Part of [FlowForge Samples](https://github.com/royada-labs/flowforge-samples).
