## for payment gateway project, how many months did it take
## are there any specific challenges in this project and how did you handle them, make sure to cover all the technical details

## any issues you have faced during the onboarding of Adyen?
## backward compatibility issues
## can you speak in terms of global unique identifier taken care, for the issues from adyen payment provider, explain 
## Did you build entire Kafka Q's for it?
it is asynchronous, so we built webhooks, talk in this perspective as well
and also more volume during holiday season

## How did you make sure the project rollout is robust and success without any failures
## Whaat alerts you have in place to track it
    -> I said we hjave p99, p95 API latency
    -> also monitor tests related Argus alerts
    -> explained a situation when the PR merged but the TF's are failing due to some Ftests not executed during PR merge
    -> Added sh files to make sure it runs all the team specific Ftests before merging

## He is interested to know my take on adding robut line code coverage, is it necessary to keep 95%
    -> will it be redundant to track these changes?
    -> mentioned it is part of dev, we will have test plan in the tasks itself
    -> the QA will try to do the test plan and then only mark QA is done
    -> also code coverage ensures that Early Bug Detection, Confidence in Refactoring, 
    -> In financial or payment systems, as seen in the Adyen project, high coverage is essential to ensure compliance, robustness, and user trust.

## should we need to run CI checks on each commit or not
    -> For mission-critical applications or large teams, running CI on every commit might be justified to ensure stability and early feedback.
    -> For smaller teams or less critical projects, consider optimizing CI triggers to balance quality assurance with resource usage.
    -> we can also give this control to developer. If he thinks it is necessary just before review to make sure everything in place,

### System design CI/CD pipeline
Imagine we are developing a CI/CD system for deploying microservices
The system should support multiple teams,
handle independent pipelines for services,
ensure zero downtime, and allow easy rollbacks

chat link: https://chatgpt.com/g/g-hdVziaooT-system-design-gpt/c/67450ee1-8aa8-8007-aac5-1a9a3f7fbcd2

## for each of these pipeline archestra, give the outside products for these, give brief answer, like what products for all these
Here are the popular products and tools for each component in the CI/CD pipeline orchestrator system:

---

### **1. Source Control System**
   - **GitHub**: Widely used for code hosting, version control, and webhooks.
   - **GitLab**: Offers built-in CI/CD with integrated pipeline triggers.
   - **Bitbucket**: Ideal for teams using Atlassian products.

---

### **2. Pipeline Orchestrator**
   - **Jenkins**: Highly customizable and extensible for CI/CD.
   - **GitLab CI/CD**: Integrated with GitLab for seamless pipelines.
   - **CircleCI**: Focused on speed and simplicity.
   - **Tekton**: Kubernetes-native CI/CD pipelines.

---

### **3. Build System**
   - **Bazel**: High-performance build system from Google.
   - **Gradle**: Common for Java-based projects.
   - **Maven**: Widely used for dependency management in Java ecosystems.

---

### **4. Artifact Repository**
   - **Nexus Repository**: Supports various artifact formats.
   - **Artifactory**: Comprehensive solution for dependency and artifact management.
   - **AWS S3**: Scalable and simple object storage.

---

### **5. Deployment Manager**
   - **ArgoCD**: Kubernetes-native declarative GitOps deployment tool.
   - **Spinnaker**: Multi-cloud deployment orchestration.
   - **Helm**: Kubernetes package manager for deploying applications.

---

### **6. Task Queue**
   - **Kafka**: Distributed streaming platform with support for high throughput.
   - **AWS SQS**: Managed message queuing for distributed systems.
   - **RabbitMQ**: Feature-rich, general-purpose messaging.

---

### **7. Logging and Monitoring**
   - **ELK Stack**: Elasticsearch, Logstash, and Kibana for centralized logging.
   - **Prometheus**: Open-source monitoring and alerting toolkit.
   - **Grafana**: Visualization tool for metrics and logs.

---

### **8. Caching System**
   - **Redis**: In-memory key-value store for caching and real-time data.
   - **Memcached**: Lightweight caching solution.
   - **AWS ElastiCache**: Managed Redis/Memcached service.

---

### **9. Rate Limiting**
   - **Envoy**: Proxy with built-in rate limiting capabilities.
   - **NGINX**: Web server and reverse proxy supporting rate limiting.
   - **Kong Gateway**: API gateway with plugins for rate limiting.

---

### **10. Database**
   - **PostgreSQL**: Relational database with strong consistency and sharding support.
   - **MySQL**: Widely used relational database, suitable for transactional data.
   - **Amazon Aurora**: Scalable, managed database compatible with MySQL and PostgreSQL.

---

These tools and products are widely used in the industry and can be selected based on the specific needs, scale, and constraints of the CI/CD system being implemented.

## what are the criteria for rollback, guive brif answer

Here are the key **criteria for initiating a rollback** in a CI/CD system:

---

### **1. Deployment Health**
   - High error rates or increased latency in production after deployment.
   - Failure of critical application health checks or canary environment tests.

### **2. User Impact**
   - Significant user-facing issues, such as broken features or downtime.
   - Negative feedback detected from monitoring tools like Sentry, New Relic, or user analytics.

### **3. Metrics Threshold**
   - Breach of predefined thresholds for key metrics (e.g., CPU/memory usage, transaction rates).
   - Abnormal spikes in database queries or cache misses.

### **4. Logging and Alerts**
   - Frequent application errors or exceptions detected in logs.
   - Alerts triggered by monitoring systems due to degraded service quality.

### **5. Manual Override**
   - Engineers detect issues not caught by automated systems and decide to initiate rollback.
   - Business-critical needs, such as preventing financial or reputational losses.

### **6. Configuration Failures**
   - Deployment involves incompatible or missing configurations.
   - Database migrations fail or data is corrupted during deployment.

---

**Automating rollback decisions** based on these criteria ensures minimal downtime and user impact.


### canary vs blue green, tell which when why, guve brief
Here’s a concise comparison of **Canary** and **Blue-Green Deployments**, focusing on **when** and **why** to use each:

---

### **1. Canary Deployment**
- **When**:
  - Gradual rollout is needed to minimize risk.
  - Real-world production testing with a subset of users is required.
  - You need to observe live user behavior and metrics over time.

- **Why**:
  - Limits exposure of issues to a small percentage of users.
  - Allows testing in production without impacting all users.
  - Easier to monitor and rollback incrementally.

- **Best for**:
  - High-traffic services where issues need to be contained.
  - Systems requiring A/B testing or controlled feature rollout.

---

### **2. Blue-Green Deployment**
- **When**:
  - Zero downtime is critical.
  - Rollbacks need to be fast and simple.
  - Entire system can be tested in a production-like environment before switching.

- **Why**:
  - Ensures quick rollback by switching traffic back to the stable environment.
  - Reduces risk by keeping old and new versions completely separate.
  - Suitable for batch processes or non-live critical services.

- **Best for**:
  - Environments with strict uptime requirements (e.g., banking, healthcare).
  - Deployments where validation/testing in an isolated environment is sufficient.

---

### **Key Tradeoffs**
- **Canary**:
  - Pro: Fine-grained control and monitoring.
  - Con: Requires robust monitoring and incremental traffic shifting.

- **Blue-Green**:
  - Pro: Simple rollback and clear isolation.
  - Con: Expensive due to the need for duplicate environments. 

Use **Canary** for progressive and high-risk rollouts, and **Blue-Green** for simplicity and critical zero-downtime needs.

### when you use caanry in rollout contexts, is it rolling back fast by any way? is it rolling back to old feature? why would you use blue green over canary

### **Canary Rollouts and Rollbacks**
1. **Canary Rollback Speed**:
   - Rollbacks in canary deployments **can be fast** if issues are detected early because only a small percentage of traffic is routed to the new version.
   - However, the rollback might take longer as traffic percentages are incrementally adjusted back to the stable version.

2. **Does it Roll Back to the Old Feature?**
   - Yes, canary rollbacks redirect traffic entirely to the old, stable version.
   - Canary deployments gradually expose users to new features, so rollbacks restore the system to the previous stable state.

---

### **When to Use Blue-Green Over Canary**
1. **Zero Downtime**:
   - Blue-Green provides **instant failover** by redirecting all traffic to the stable environment. Canary, on the other hand, involves incremental rollouts, which might not be as immediate.

2. **Simplicity in Rollback**:
   - Blue-Green is **simpler to roll back** since the entire new version is isolated in a separate environment, making switching instantaneous.
   - Canary relies on dynamic traffic management, which can add complexity during rollback.

3. **No Need for Incremental Testing**:
   - Use Blue-Green if you don’t require real-world user testing or progressive exposure. This is ideal for systems with robust pre-deployment testing.

---

### **Why Choose Blue-Green Over Canary**
- **Critical Uptime Needs**: Blue-Green ensures uninterrupted service during both deployment and rollback.
- **Cost Tolerance**: If the cost of maintaining duplicate environments is acceptable.
- **Simplicity**: When rollback speed and straightforward operations are prioritized over gradual exposure. 

In summary, **Canary** is ideal for gradual exposure and real-world testing, while **Blue-Green** is preferred for critical systems requiring fast, zero-downtime rollback.

can you give a real world example to explain this - "when you use caanry in rollout contexts, is it rolling back fast by any way? is it rolling back to old feature? why would you use blue green over canary", keep it precise and brief

### **Real-World Example: Canary vs Blue-Green**

#### **Canary Example**
- **Scenario**: A streaming platform rolls out a new recommendation algorithm.
- **Action**: Only 5% of users are routed to the new algorithm while monitoring engagement metrics.
- **Issue Detected**: Users under the canary group experience poor recommendations.
- **Rollback**:
  - Traffic is redirected entirely to the old algorithm (stable feature).
  - Rollback is fast if detected early but can take longer if traffic was already ramped up significantly.

#### **Blue-Green Example**
- **Scenario**: An online banking system deploys a critical backend update.
- **Action**: The new version is tested in a completely isolated "blue" environment.
- **Issue Detected**: Integration with payment gateways fails in the blue environment.
- **Rollback**:
  - Traffic instantly switches back to the old "green" environment.
  - Rollback is immediate since the old version was untouched and isolated.

---

### **Why Use Blue-Green Over Canary**
- **Criticality**: For the banking system, even 5% user impact (as in canary) is unacceptable.
- **Rollback Speed**: Blue-Green ensures immediate rollback without incremental traffic adjustments.

**Summary**: Use **canary** for progressive exposure and real-time validation (e.g., a streaming platform). Use **blue-green** for critical systems where zero user impact and instant rollback are essential (e.g., banking).

### are the artefacts in mermaid diagram are the docker images? or what, answer in 2 lines
The artifacts in the **Mermaid diagram** can include **Docker images**, as they are commonly used for containerized deployments. Additionally, they can also include other build outputs like JAR files, static binaries, or configuration bundles, depending on the application.

### Is the deployment has to be auto approval or manual approval for the deployment automatically

answer briefly in 1 para with pros and cons

The choice between **auto-approval** and **manual approval** for deployments depends on the system's criticality and risk tolerance. 

- **Auto-Approval**: Ideal for non-critical systems with well-tested pipelines, enabling faster, hands-free deployments. However, it risks propagating undetected issues to production if testing is insufficient.
- **Manual Approval**: Suitable for high-risk or production-critical deployments, allowing human oversight to catch potential issues. The downside is slower deployments, which can delay time-sensitive updates.

A hybrid approach is often best, using auto-approval for staging or low-risk changes and manual approval for critical production deployments.

The decision between **auto-approval** and **manual approval** for deployments depends on the context of the system, its risk profile, and the maturity of your CI/CD pipeline. As an L5 engineer, here’s how I’d evaluate the trade-offs and recommend a hybrid approach.

### **Auto-Approval Deployments**
- **When to Use**:
  - Non-critical environments like staging, QA, or pre-production.
  - Highly mature pipelines with robust automated testing, integration checks, and rollback mechanisms.
- **Pros**:
  - Reduces deployment delays, enabling faster iteration cycles and more frequent releases.
  - Ideal for high-velocity engineering teams working on systems with low user impact.
  - Fully automated rollback mechanisms can mitigate risks of undetected issues.
- **Cons**:
  - Automated checks might miss edge cases, leading to potential downtime or degraded user experience in production.
  - Lack of human oversight increases risks for business-critical or customer-facing systems.

---

### **Manual Approval Deployments**
- **When to Use**:
  - Production environments, particularly for critical or high-risk features.
  - Systems that involve compliance or regulatory requirements where auditing and human validation are mandatory.
- **Pros**:
  - Ensures human oversight to validate non-functional aspects like business alignment, compliance, or unexpected anomalies not caught in automation.
  - Reduces the risk of deploying potentially catastrophic changes to production environments.
- **Cons**:
  - Slows down deployment processes, especially for organizations emphasizing continuous delivery.
  - Prone to delays or bottlenecks if manual approvers are unavailable.

---

### **Hybrid Approach**
The best practice for most organizations is a **hybrid model**:
1. **Auto-approval** for deployments to lower environments (e.g., staging, QA) to enable rapid testing and feedback.
2. **Manual approval** gates for high-risk production deployments, but only for changes that significantly impact customers or systems. 
3. Implement **conditional approvals** for production based on automated metrics and risk scoring. For example:
   - Low-risk changes with passing metrics (e.g., no errors in canary or staging) proceed automatically.
   - High-risk changes trigger manual approval based on predefined thresholds (e.g., error rates, latency).

---

### **Key Considerations for Decision-Making**
- **Maturity of CI/CD Pipeline**: Auto-approval is more viable if your pipeline has robust unit, integration, and end-to-end tests, along with automated rollbacks.
- **System Criticality**: Mission-critical systems, such as payment processing or healthcare, demand stricter controls like manual approval.
- **Deployment Velocity**: Teams with high deployment frequency should automate wherever feasible but retain manual gates for specific scenarios to avoid bottlenecks.
- **Monitoring and Rollbacks**: For auto-approvals, ensure observability tools (e.g., Prometheus, Datadog) and rollback mechanisms are in place to quickly mitigate failures.

**Conclusion**: A **data-driven, hybrid strategy** strikes the right balance. Use automation to streamline routine changes, but introduce manual checkpoints for high-risk deployments to maximize reliability and minimize downtime.


### whats the relation between deployment manager and orchestrater instance
The **Deployment Manager** and **Orchestrator Instances** have a collaborative relationship, where the **Orchestrator** coordinates pipeline execution and delegates the deployment phase to the **Deployment Manager**.

---

### **Relationship**
1. **Role of the Orchestrator**:
   - Manages the overall pipeline, including build, test, and deploy stages.
   - Delegates deployment-specific tasks (e.g., initiating a canary or blue-green rollout) to the Deployment Manager.
   - Tracks deployment status to determine pipeline success or failure.

2. **Role of the Deployment Manager**:
   - Executes the deployment strategy (e.g., canary, blue-green, or rolling updates).
   - Interfaces with infrastructure systems (e.g., Kubernetes, VMs) to apply changes.
   - Reports deployment progress and outcomes back to the Orchestrator.

3. **Communication**:
   - Orchestrator Instances invoke the Deployment Manager via APIs (e.g., REST, gRPC) or messaging systems (e.g., Kafka) to initiate deployments.
   - Deployment Manager sends back status updates, including success, failure, or rollback triggers.

---

### **Example Workflow**
1. **Orchestrator Instance**: Receives a trigger from a source control system, runs the pipeline, and invokes the Deployment Manager during the deploy phase.
2. **Deployment Manager**: Executes the deployment strategy, monitors the rollout, and reports status (success/failure) back to the Orchestrator.
3. **Orchestrator Instance**: Logs the outcome, and if necessary, triggers a rollback or alerts based on the Deployment Manager’s feedback.

---

### **Key Dependency**
The **Orchestrator Instance** relies on the **Deployment Manager** to abstract the complexities of infrastructure-specific deployment strategies, enabling the pipeline to remain agnostic of deployment details.


### what do we store in the database
The **database** in this CI/CD system is used to store **pipeline metadata**, such as pipeline configurations, stages, and execution history, ensuring strong consistency. It also tracks **deployment states** (e.g., progress, status, rollback triggers) for coordination between orchestrator instances. Additionally, it stores **team-specific partitioned data**, like user roles, permissions, and logs, for secure and scalable operations.

### for logs related, like how monitoring taken care. what are the key metrics, like what logs you would look at in particular in your system in case of failures

For monitoring and log analysis in the CI/CD system, key aspects include **log collection**, **metrics monitoring**, and **alerting** to ensure system health and facilitate debugging in case of failures.

---

### **Key Metrics to Monitor**
1. **Pipeline Metrics**:
   - Success/failure rates for pipelines.
   - Average and peak execution times for stages (build, test, deploy).
   - Queue depth in task queues to detect bottlenecks.

2. **Deployment Metrics**:
   - Deployment duration for different environments (staging, production).
   - Error rates and latency in canary/blue-green rollouts.
   - Health check failures during deployment.

3. **System Health Metrics**:
   - Resource utilization (CPU, memory, disk I/O) for orchestrators, build runners, and deployment systems.
   - API response times for critical components (orchestrator, deployment manager).
   - Cache hit/miss ratios for Redis or dependency caches.

---

### **Logs to Analyze During Failures**
1. **Pipeline Orchestrator Logs**:
   - Task assignment errors, retries, or timeouts in task queues.
   - Stage failures with error messages (e.g., build script issues).

2. **Build System Logs**:
   - Compilation errors, test failures, or dependency download failures.
   - Disk or resource exhaustion on build agents.

3. **Deployment Manager Logs**:
   - Deployment failures due to incorrect configurations or infrastructure issues.
   - Rollback triggers and associated error logs.

4. **System-Wide Logs**:
   - Authentication/authorization errors for accessing secrets or resources.
   - Network failures in connecting to external dependencies (e.g., artifact repository, monitoring tools).

---

### **How Monitoring is Taken Care Of**
1. **Centralized Logging**:
   - Use tools like ELK (Elasticsearch, Logstash, Kibana) or Splunk to aggregate logs from all components.
   - Enable structured logging (JSON format) for easier querying and analysis.

2. **Real-Time Metrics and Alerts**:
   - Use Prometheus for metrics collection and Grafana for visualization.
   - Set up alerts for threshold breaches (e.g., queue depth, deployment failures).

3. **Correlated Tracing**:
   - Employ distributed tracing (e.g., OpenTelemetry) to trace requests end-to-end across orchestrators, build agents, and deployment systems.

---

**Summary**: Logs and metrics ensure observability by highlighting pipeline, deployment, and system health issues. Focus on real-time monitoring, centralized logging, and tracing to identify root causes and mitigate failures efficiently.

### Asked how is the GUID is calculated.
   -> couldn't answer it propely.