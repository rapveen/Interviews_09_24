# ** Architectural round **
## **1. Asked how I've improved ** ==> Optimized payments SDK for Adyen integration, reducing development time by 25% for new payment methods. Leveraging TypeScript Shadow DOM for encapsulation.
## **2. asked on **==> Migrated and refactored APIs, boosting code efficiency by 25%.
## **3. design a state machine**
'''
The state machine should be able to handle a finite set of states and transitions between them, as well as actions associated with each transition.
Your implementation should include:
• A set of states and transitions that represent the states of the state machine
• Actions that should be executed when a transition occurs
• A mechanism to trigger transitions between states
• Unit tests to verify the correct behaviour of the state machine
You can assume that the state machine has a single initial state and can transition between states in response to external events. The actions associated with each transition should be simple print statements to the console indicating the current state of the state machine.
State 1 - fraud check - state2 - preauth - booking confirmation -> email → fin

'''
```mermaid
sequenceDiagram
    participant C as Client
    participant O as Orchestrator
    participant DB as State Store (DB)
    participant Q as Message Queue
    participant S1 as Service 1
    participant S2 as Service 2
    participant S3 as Service 3
    
    Note over O,DB: Saga Instance Created
    
    C->>O: Start Saga Transaction
    O->>DB: Create Saga Entry
    DB-->>O: Saga ID
    
    rect rgb(200, 255, 200)
        Note over O,S3: Normal Flow
        O->>Q: Publish Command (Service 1)
        Q->>S1: Process Command
        S1-->>Q: Success Response
        Q-->>O: Success Response
        O->>DB: Update Step 1 Status
        
        O->>Q: Publish Command (Service 2)
        Q->>S2: Process Command
        S2-->>Q: Success Response
        Q-->>O: Success Response
        O->>DB: Update Step 2 Status
        
        O->>Q: Publish Command (Service 3)
        Q->>S3: Process Command
        S3-->>Q: Failure Response
        Q-->>O: Failure Response
    end
    
    rect rgb(255, 200, 200)
        Note over O,S1: Compensation Flow
        O->>DB: Mark Saga Failed
        O->>Q: Compensation Command (Service 2)
        Q->>S2: Rollback Changes
        S2-->>Q: Compensation Complete
        Q-->>O: Compensation Confirmed
        
        O->>Q: Compensation Command (Service 1)
        Q->>S1: Rollback Changes
        S1-->>Q: Compensation Complete
        Q-->>O: Compensation Confirmed
        
        O->>DB: Mark Saga Compensated
    end
    
    O-->>C: Saga Complete (Rolled Back)
    
    Note over DB: Database Schema
    
    classDiagram
        class saga_instances {
            +String saga_id
            +String saga_type
            +Int current_step
            +String status
            +JSON payload
            +Timestamp created_at
            +Timestamp updated_at
        }
        
        class saga_steps {
            +String step_id
            +String saga_id
            +String step_name
            +String status
            +JSON response_data
            +Int retry_count
        }
        
        saga_instances "1" -- "many" saga_steps
        
    Note over Q: Queue Structure
    
    classDiagram
        class command_queue {
            +String message_id
            +String saga_id
            +String service_id
            +JSON command_data
            +Timestamp timestamp
        }
        
        class response_queue {
            +String message_id
            +String saga_id
            +String service_id
            +JSON response_data
            +Boolean success
            +Timestamp timestamp
        }

```

Let me explain the key aspects of this diagram:

1. Communication Patterns:
   - Orchestrator → Services: Async via Message Queue
   - Services → Orchestrator: Async via Response Queue
   - Orchestrator → Database: Sync
   - Client → Orchestrator: Sync/Async (configurable)

2. Database Interactions:
   - saga_instances: Tracks overall saga state
   - saga_steps: Tracks individual step states
   - Maintains full audit trail and recovery information

3. Rollback Flow:
   - Triggered on any service failure
   - Executes compensating transactions in reverse order
   - Uses same queue infrastructure as forward flow
   - Maintains compensation state in database

4. Queue Structure:
   - command_queue: For initiating service actions
   - response_queue: For receiving service responses
   - Ensures reliable message delivery and processing

5. Key Features Shown:
   - Full lifecycle of saga transaction
   - Compensation flow triggered by failure
   - State management in database
   - Asynchronous service communication
   - Queue-based message routing

Would you like me to explain any specific part of the flow in more detail?

---
## **4. how the state changes ?**
## **5. is it sync vs async**
## **6. where do we store the state info**
## **7. How does it rollback all happened steps**
## **8. basically its internal implementation of saga-orchestration pattern**

## **9. Can load tests be done in CI stage?**

Yes, **load tests can be integrated into a CI pipeline**, which allows automated performance testing as part of the continuous integration and delivery process. Integrating load tests into CI pipelines ensures that performance standards are consistently met and helps catch potential bottlenecks early. Here’s how it’s typically done:

---

### **1. Set Up Load Testing in the CI Pipeline**

- **Choose a CI Tool:** Most CI/CD tools like **Jenkins**, **GitLab CI**, **GitHub Actions**, and **CircleCI** support integration with load testing tools such as **JMeter**, **Gatling**, and **Locust**.
- **Configure Load Test Scripts:** Load testing scripts should be designed to simulate realistic traffic. They can be defined as code (e.g., `.jmx` files for JMeter or `.scala` files for Gatling), allowing version control and consistency across CI runs.
- **Define Thresholds:** Set acceptable performance thresholds (e.g., response time, error rate) as pass/fail criteria for the CI pipeline.

---

### **2. Automate Execution and Reporting**

- **Automated Execution:** Configure the CI pipeline to run load tests at specific triggers, such as after major code changes, before deploying to production, or at scheduled intervals.
- **Result Analysis:** Integrate reporting tools or plugins (e.g., Jenkins Performance Plugin, Gatling reports) to visualize results and automatically compare them against predefined performance thresholds.
- **Alerts and Notifications:** Configure the CI system to notify teams if load tests fail, allowing prompt identification and resolution of performance regressions.

---

### **Benefits of CI Pipeline Integration for Load Testing**

1. **Early Detection of Performance Issues:** Automating load tests helps detect bottlenecks early, reducing the risk of performance degradation in production.
2. **Consistent Performance Benchmarks:** Continuous load testing ensures that every change meets performance standards, maintaining a high-quality user experience over time.
3. **Efficient Resource Utilization:** Integrating load tests allows teams to optimize resources by regularly validating system performance rather than waiting for dedicated testing cycles.

---

By incorporating load testing in CI pipelines, you create a robust, automated workflow that continuously validates application performance, enabling faster and safer releases.


## ** 10. what type of quality checks can be done in CI stage**
In the **CI (Continuous Integration) stage**, quality checks help maintain code reliability, security, and performance standards before the code progresses to further stages or deployment. Here are essential quality checks typically performed in the CI stage:

---

### **1. Code Quality and Style Checks**

- **Static Code Analysis:** Tools like **SonarQube**, **ESLint** (for JavaScript), and **Pylint** (for Python) analyze code for maintainability, complexity, and adherence to coding standards.
- **Linting:** Ensures consistent code style and readability by catching syntax errors, enforcing style guidelines, and detecting common issues.
- **Benefit:** Increases code readability, reduces technical debt, and enforces team-wide coding standards.

### **2. Security Vulnerability Scans**

- **Dependency Scans:** Tools like **OWASP Dependency-Check**, **Snyk**, and **npm audit** scan third-party libraries for known vulnerabilities.
- **Static Application Security Testing (SAST):** Tools like **SonarQube** and **Fortify** analyze source code for security flaws (e.g., SQL injection, XSS).
- **Benefit:** Detects and addresses potential security issues early, minimizing risks in production environments.

### **3. Unit and Integration Testing**

- **Unit Tests:** Verifies that individual functions or components work as expected.
- **Integration Tests:** Ensures that different parts of the application interact correctly, especially critical for microservices.
- **Benefit:** Catches functional issues early, improving code reliability and preventing regressions.

### **4. Code Coverage Analysis**

- **Code Coverage Tools:** Tools like **JaCoCo** (for Java), **Coverage.py** (for Python), or **Codecov** measure the percentage of code covered by tests.
- **Coverage Thresholds:** Set minimum coverage thresholds in the CI pipeline to ensure adequate testing before merging.
- **Benefit:** Ensures sufficient testing of new code, reducing the likelihood of bugs and enhancing code quality.

### **5. Performance and Load Testing (Optional)**

- **Load Testing:** Tools like **JMeter** or **Gatling** can run short load tests in CI for critical functionality, simulating traffic to detect performance regressions.
- **Performance Benchmarks:** Measure response times, resource usage, and throughput for critical components to catch performance issues early.
- **Benefit:** Identifies performance bottlenecks early, preventing potential production slowdowns.

### **6. Code Review Automation**

- **Automated Code Reviews:** Tools like **GitHub Actions** and **GitLab Code Quality** provide automated feedback on pull requests to catch issues in CI.
- **Benefit:** Speeds up the code review process by catching obvious issues, allowing reviewers to focus on higher-level feedback.

### **7. Compliance Checks and Best Practices**

- **Compliance Enforcement:** Tools like **Terraform Validator** for infrastructure as code or **Chef Inspec** for server configurations ensure compliance with industry standards.
- **Best Practices:** CI tools can enforce naming conventions, logging practices, and security best practices specific to the organization.
- **Benefit:** Ensures that code and configurations adhere to internal policies and regulatory standards.

---

### **Summary**

By incorporating these quality checks—**code quality checks**, **security scans**, **testing** (unit, integration, load), **code coverage**, **review automation**, and **compliance checks**—you create a robust CI process that promotes reliable, secure, and performant code. These checks enhance code quality, reduce risk, and streamline development, enabling faster, safer releases.

## ** 11. how will you test the state machine,**
Testing a state machine comprehensively ensures that it behaves as expected under various scenarios, maintains consistency, and handles failures gracefully. Below are **four detailed points** outlining the strategies and methodologies to effectively test the state machine based on the discussed design:

---

### **1. Unit Testing of States and Transitions**

**Objective:** Validate that each state and transition behaves correctly in isolation, ensuring that actions are executed as intended and state changes occur accurately.

**Approach:**

- **Mocking Dependencies:**
  - **Services:** Use mocking frameworks (e.g., `unittest.mock` in Python) to simulate interactions with external services like **Inventory Service**, **Payment Service**, **Email Service**, **Redis Cache**, and **Apache Cassandra**.
  - **Database Operations:** Mock database interactions to focus solely on the state machine logic without relying on actual database instances.

- **Test Cases:**
  - **Valid Transitions:** Ensure that for each valid event in a given state, the state machine transitions to the correct next state and executes the associated action.
    - *Example:* Triggering `APPROVE_FRAUD` in the `FRAUD_CHECK` state should transition to `PREAUTH` and call the **Preauthorization Service**.
  - **Invalid Transitions:** Verify that triggering an invalid event in a current state raises appropriate exceptions or errors.
    - *Example:* Triggering `SEND_EMAIL` in the `FRAUD_CHECK` state should result in an `InvalidTransitionException`.
  - **Action Execution:** Confirm that the correct actions (e.g., service calls, print statements) are executed during transitions.
    - *Example:* Upon transitioning to `EMAIL`, the **Email Service** should be invoked to send a confirmation email.

- **Tools and Frameworks:**
  - **Python `unittest`:** Utilize the `unittest` framework for structuring and running unit tests.
  - **Mocking Libraries:** Use libraries like `unittest.mock` or `pytest-mock` to mock external dependencies and services.

**Example Unit Test Structure:**

```python
import unittest
from unittest.mock import MagicMock, patch
from state_machine import StateMachine, State, Event

class TestStateMachine(unittest.TestCase):
    def setUp(self):
        self.sm = StateMachine(State.FRAUD_CHECK)
        # Mock external services
        self.sm.action_preauth = MagicMock()
        self.sm.action_fraud_check = MagicMock()
        # ... mock other actions as needed

    def test_valid_transition_fraud_to_preauth(self):
        self.sm.trigger_event(Event.APPROVE_FRAUD)
        self.assertEqual(self.sm.get_state(), State.PREAUTH)
        self.sm.action_preauth.assert_called_once()

    def test_invalid_transition(self):
        with self.assertRaises(Exception):
            self.sm.trigger_event(Event.SEND_EMAIL)

    # Additional test cases for other transitions

if __name__ == '__main__':
    unittest.main()
```

---

### **2. Integration Testing with External Services and Databases**

**Objective:** Ensure that the state machine interacts correctly with external services, databases, caches, and message brokers, maintaining data integrity and consistent state transitions.

**Approach:**

- **Environment Setup:**
  - **Test Databases:** Utilize test instances of **Apache Cassandra** and **Redis** or use in-memory databases where applicable.
  - **Mock Services:** Deploy test versions of external services (**Inventory Service**, **Payment Service**, **Email Service**) that can simulate real-world behaviors, including success and failure scenarios.
  - **Message Brokers:** Set up a test instance of **Apache Kafka** to handle event streaming without affecting production data.

- **Test Scenarios:**
  - **End-to-End Transitions:** Simulate complete workflows, triggering events and verifying that the state machine correctly updates states and interacts with all necessary services.
    - *Example:* From `FRAUD_CHECK` → `PREAUTH` → `BOOKING_CONFIRMATION` → `EMAIL` → `FIN`.
  - **Database Interactions:** Verify that state updates, transition logs, and compensating transactions are correctly written to and read from **Apache Cassandra**.
  - **Cache Consistency:** Ensure that **Redis Cache** reflects the latest state and that cache misses correctly retrieve data from **Cassandra**.
  - **Event Streaming:** Test that events are correctly published to and consumed from **Apache Kafka**, triggering appropriate actions in the **Event Processor**.

- **Tools and Frameworks:**
  - **Docker Compose:** Use Docker Compose to orchestrate test environments with all necessary services.
  - **Test Frameworks:** Utilize frameworks like `pytest` with fixtures to manage integration test setups and teardowns.

---

### **3. Testing Rollback Mechanisms and Failure Handling**

**Objective:** Ensure that the state machine correctly identifies failures, triggers compensating transactions, and maintains system consistency through effective rollback mechanisms.

**Approach:**

- **Simulating Failures:**
  - **Service Failures:** Mock failures in external services (e.g., **Payment Service** throws an exception during preauthorization).
  - **Database Failures:** Simulate database unavailability or write failures in **Apache Cassandra**.
  - **Message Broker Failures:** Test scenarios where **Kafka** is unavailable or message delivery is delayed.

- **Compensating Transactions:**
  - **Verification:** Confirm that upon encountering a failure, the **Saga Orchestrator** initiates the correct compensating transactions (e.g., reverting a preauthorization).
  - **Idempotency Checks:** Ensure that compensating transactions are idempotent and can handle repeated execution without adverse effects.

- **State Consistency:**
  - **Post-Rollback State:** Verify that the state machine reverts to the appropriate previous state after a rollback.
  - **Audit Logs:** Check that all state transitions and compensations are accurately logged in the `transitions` and `compensations` tables in **Cassandra**.

- **Tools and Frameworks:**
  - **Chaos Monkey:** Introduce random failures in services to test the resilience and rollback capabilities.
  - **Mocking Libraries:** Use `unittest.mock` to simulate exceptions and failures in service calls.

---

### **4. Performance, Load, and Stress Testing**

**Objective:** Assess the state machine's performance under various load conditions, ensuring it meets the required throughput, latency, and scalability expectations.

**Approach:**

- **Load Testing:**
  - **Simulate High Traffic:** Use tools like **Apache JMeter**, **Locust**, or **k6** to generate a high volume of state transition events, mimicking real-world usage.
  - **Throughput Measurement:** Measure the number of state transitions the system can handle per second without degradation.
  - **Latency Tracking:** Monitor the response times for state transitions to ensure they remain within acceptable limits.

- **Stress Testing:**
  - **Beyond Capacity:** Push the system beyond its expected maximum load to identify breaking points and observe behavior under extreme conditions.
  - **Recovery Verification:** Ensure that the system recovers gracefully after being stressed, maintaining data integrity and consistent states.

- **Scalability Testing:**
  - **Horizontal Scaling:** Test the addition of more instances of the **State Machine Service**, **Kafka**, **Cassandra**, and **Redis** to verify that the system scales linearly.
  - **Distributed Components:** Ensure that partitioning and replication in **Cassandra** and **Kafka** function correctly under increased load.

- **Rate Limiting Verification:**
  - **API Gateway Limits:** Confirm that the rate limiting configured at the **API Gateway** effectively throttles excessive requests, preventing system overload.
  - **Graceful Degradation:** Ensure that clients receive appropriate error messages (e.g., `429 Too Many Requests`) when rate limits are exceeded.

- **Tools and Frameworks:**
  - **Load Testing Tools:** **Apache JMeter**, **Locust**, **k6** for generating and managing load.
  - **Monitoring Tools:** **Prometheus** and **Grafana** to visualize performance metrics during tests.
  - **Chaos Engineering Tools:** **Chaos Monkey** or **Gremlin** to introduce failures during load tests.

**Example Load Testing Steps:**

1. **Define Test Scenarios:**
   - **Normal Load:** Simulate the expected number of state transitions per second.
   - **Peak Load:** Simulate higher than expected traffic to test scalability.
   - **Spike Load:** Introduce sudden bursts of traffic to assess system resilience.

2. **Execute Tests:**
   - Use **Locust** scripts to define user behavior, triggering state transitions via the **API Gateway**.
   - Monitor system performance metrics in **Grafana** during the tests.

3. **Analyze Results:**
   - **Throughput:** Ensure the system handles the required QPS (Queries Per Second) without significant latency increases.
   - **Latency:** Verify that response times remain within acceptable thresholds.
   - **Error Rates:** Check for increased error rates or failed transitions under load.

4. **Optimize and Retest:**
   - Identify bottlenecks (e.g., database write speeds, service processing times) and optimize configurations or code.
   - Retest to validate improvements.


---

### **Summary**

By implementing a comprehensive testing strategy encompassing **unit tests**, **integration tests**, **rollback and failure handling tests**, and **performance/load tests**, you ensure that the state machine operates reliably, maintains consistency, and performs efficiently under various conditions. This multi-faceted approach addresses both functional and non-functional requirements, aligning with best practices and the technical details discussed in the system design.
