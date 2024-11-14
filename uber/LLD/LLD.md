instructions:
step 1: clarify requirements
step 2: Identify Entities
step 3: class design
step 4: implementation
step 5: exception handling
    - now based on core functionalities of the feature, list out different design patterns being used for each functionality.
    - working solution
    - code extensibility
    - code readability
    - good naming conventions for variables   
    - Interface Design
    - Optimization and Performance Tuning
    - Reflects the ability to design highly extensible and flexible systems.
    - As a Meta E5-level role, demonstrating the ability to design highly scalable, extensible, and flexible systems is crucial. At this level, interviewers expect you to not only solve the problem at hand but also showcase your proficiency in architecting solutions that can gracefully handle future enhancements, increased complexity, and scalability demands
    - use design patterns like Observer, Factory, Abstract Factory, Strategy, Adapter, State, Composite, Decorator but not limited to these. Showcases a comprehensive understanding of various design patterns and their applications.
    - Keeping a 20-40 rule. First 20 min would be to scope down the vague problem to apis, objects, interfaces/abstract classes/super classes, strategies and specifications. Next 40 min for implementation and running test cases.
    - refined approach for core functionalities of the problem. Use optimized algorithms available in real world.
    - Better real-world applicability
    - Built-in thread management
    - Ready for distributed deployment
    - Better resource utilization
    - focus on multi-threading and concurrency in Java.
    - can you give code for all the classes involved.
    - follow SOLID, KISS, DRY principles.
    - give an extendible and compillable code so that we can run some examples in main. may be have a client facing class to run it. follow all best practices.



1. **Variation of Key Value storage. And some Transaction handling. Design LLD and code**
2. **Question was a variation of meeting scheduler.**
    N rooms are given and streams of times will be given and we have to schedule meeting. After storing the meeting we have to store audit logs for each room. We have to delete audit logs after x days. Follow up questions were that rooms will have capactiy constraints so that we have to check if we can organise meeting with given capacity. Also we have to minimise spillage of free time while sceduling meetings.
    Spillage eg: if room1 is free from 9-10 am and room2 is free from 9am - 12pm and if a meeting comes for 9-10 am then we should assign it to room1 as room1 will be booked for day . Concurrency handling was expected.


    After clearing above rounds HLD and HM were scheduled.
3. `Splitwise`
4. `Your knowledge of asynchronous programming, threads, concurrency are tested.`
5. `multi threaded publisher subscriber message queue`
6. `Composite, Decorator can earn you good points instead.`
    `Adapter, State are good too.`
    `Observer, Factory, Abstract Factory, Strategy are must known.`

7. `Top 1 frequent element in a stream. But the problem was framed in a different manner. Implement apis:`
        - postTweet(userId, tweetId, message)
        - like(userId, tweetId)
        - unlike(userId, tweetId)
        - getTopLikedTweet(userId)

        I told a 
        User {
        Map<Tweet, LikeFrequency>;
        TreeMap<LikeFrequency, Set<Tweet>>; 
        }
        solution.
        The interviewer asked to optimize. I went on explaining a doubly linked list approach which was an overkill and couldn't code anything in time. I just had to replace the TreeMap with HashMap and introduce an additional variable topLikeFrequency to track the highest key in the new HashMap. And update topLikeFrequency whenever the Set in it becomes empty or a better likeFrequency comes.

8. ``Keeping a 20-40 rule. First 20 min would be to scope down the vague problem to apis, objects, interfaces/abstract classes/super classes, strategies and specifications. Next 40 min for implementation and running test cases.``

9. 



### Updated prompt by ChatGPT

 Develop a comprehensive, scalable, and extensible Java-based system for << >>, adhering to best practices in software design and engineering. The solution should demonstrate proficiency in object-oriented design, design patterns, multi-threading, concurrency control, and system optimization.

1. Problem Scoping and Requirements (5-7 minutes)
Clarify Functional Requirements:
Understand and outline the core functionalities needed.
Define Constraints and Scale:
Identify any limitations, performance requirements, or scalability considerations.
Identify Edge Cases:
Determine unusual or extreme scenarios that the system must handle.
List Assumptions:
State any assumptions made to simplify or clarify the problem.
Sample Input/Output Scenarios:
Provide examples to illustrate expected behavior.
2. Core Design Planning (10-12 minutes)
A. API Design:

Define Public Interfaces and Contracts:
Outline the main APIs the system will expose.
Design Service Layer APIs:
Plan how different services or modules will interact.
Plan REST Endpoints (if applicable):
Design endpoints for web-based interactions.
B. Object Model:

Identify Entities and Their Relationships:
List main objects and how they relate to each other.
Define Immutable vs Mutable Objects:
Decide which objects should be immutable to enhance thread safety and predictability.
Plan Value Objects vs Entities:
Distinguish between objects that represent values and those that represent entities with identities.
Consider Builder Patterns for Complex Objects:
Use builder patterns to manage the creation of objects with many parameters.
C. Behavioral Patterns:

Identify Different "Types" of Behaviors:
Determine varying behaviors that need to be encapsulated.
Plan for Behavior Segregation Using Interfaces:
Use interfaces to separate different behaviors.
Consider Behavioral Composition vs Inheritance:
Decide between composing behaviors or using inheritance hierarchies.
Design Provider/Strategy Patterns for Varying Behaviors:
Implement strategies or providers to handle interchangeable behaviors.
3. Architecture Components (8-10 minutes)
A. Core Components:

Base Abstractions (Abstract Classes/Interfaces):
Define foundational abstractions to promote loose coupling.
Provider Interfaces for Extensible Behaviors:
Create interfaces that allow behaviors to be extended or modified.
Validators and Blockers for Rule Enforcement:
Implement components that enforce business rules and constraints.
Factories for Object Creation:
Use factory patterns to manage the creation of objects, promoting scalability.
Event Handlers and Observers:
Incorporate event-driven patterns to handle asynchronous events.
B. Cross-cutting Concerns:

Exception Hierarchy:
Design a structured exception hierarchy for consistent error handling.
Validation Framework:
Implement a framework to validate data and operations.
Event Management:
Manage events and listeners to handle state changes and notifications.
State Management:
Handle different states of the system or objects efficiently.
Thread Safety Considerations:
Ensure that shared resources are accessed in a thread-safe manner.
4. Implementation Guidelines (25-30 minutes)
A. Code Organization:

Package Structure by Feature/Module:
Organize code into packages based on functionality.
Clear Separation of Interfaces and Implementations:
Keep interfaces separate from their concrete implementations.
Proper Encapsulation:
Hide internal states and expose only necessary functionalities.
Immutable Where Possible:
Make objects immutable to enhance thread safety and reduce side effects.
B. Design Patterns Usage:

Provider Pattern for Varying Behaviors:
Implement providers to handle different behaviors dynamically.
Factory Pattern for Object Creation:
Use factories to manage the instantiation of objects.
Strategy Pattern for Interchangeable Algorithms:
Encapsulate algorithms within strategy classes for flexibility.
Observer Pattern for Event Handling:
Notify observers about state changes or events.
Decorator Pattern for Dynamic Behavior Addition:
Add responsibilities to objects dynamically without altering their structure.
State Pattern for State Management:
Manage object states and transitions effectively.
Composite Pattern for Tree Structures:
Treat individual objects and compositions uniformly.
Command Pattern for Operation Encapsulation:
Encapsulate requests as objects, allowing for parameterization and queuing.
C. SOLID Principles:

Single Responsibility: Each class has one purpose.
Open/Closed: Extend behavior through providers/strategies without modifying existing code.
Liskov Substitution: Ensure proper inheritance hierarchies.
Interface Segregation: Design focused and specific interfaces.
Dependency Inversion: Depend on abstractions rather than concrete implementations.
D. Best Practices:

Composition Over Inheritance:
Favor composing objects to achieve functionality over inheriting from classes.
Immutability Where Possible:
Create immutable objects to enhance predictability and thread safety.
Fail-fast Validations:
Validate inputs and states early to catch errors promptly.
Proper Exception Handling:
Handle exceptions gracefully and maintain system stability.
Thread Safety Considerations:
Use synchronization mechanisms to protect shared resources.
Clear Naming Conventions:
Use descriptive and consistent names for variables, methods, and classes.
Interface-based Programming:
Program to interfaces to promote flexibility and interchangeability.

5. Documentation:

Class/Interface Documentation:
Provide clear JavaDoc comments explaining the purpose and usage of classes and interfaces.
Usage Examples:
Include examples demonstrating how to use the system.
Important Assumptions:
Document any assumptions made during the design and implementation.
Threading Considerations:
Explain how thread safety is achieved and managed within the system.
6. Key Focus Areas:
A. Extensibility:

Provider Interfaces for Varying Behaviors:
Design interfaces that allow behaviors to be extended without modifying existing code.
Strategy Pattern for Algorithms:
Encapsulate interchangeable algorithms within strategy classes.
Factory Pattern for Object Creation:
Use factories to manage the instantiation process, facilitating scalability.
Clear Extension Points:
Define points in the system where new features or behaviors can be added seamlessly.
B. Maintainability:

Clear Separation of Concerns:
Divide the system into distinct sections, each handling a specific aspect.
Proper Encapsulation:
Hide internal states and expose only necessary functionalities.
Interface-based Design:
Use interfaces to define contracts, promoting loose coupling.
Immutable Objects Where Possible:
Create immutable objects to reduce complexity and enhance thread safety.
C. Scalability:

Thread Safety Considerations:
Implement synchronization and concurrent data structures to handle multiple operations.
Resource Management:
Efficiently manage system resources to support growth.
Performant Algorithms:
Use optimized algorithms to ensure high performance under load.
Caching Strategies:
Implement caching to reduce latency and improve responsiveness.
D. Real-world Considerations:

Error Handling:
Robustly manage errors to maintain system stability.
Logging:
Implement logging to monitor system behavior and diagnose issues.
Monitoring:
Set up monitoring to track system performance and health.
Configuration Management:
Use configuration files or services to manage system settings dynamically.
7. Code Structure Requirements:
Clear Package Organization:

Organize code into logical packages based on features or modules.
Interface-first Approach:

Design interfaces before concrete implementations to promote flexibility.
Proper Exception Hierarchy:

Create a structured hierarchy of custom exceptions for consistent error handling.
Clean and Descriptive Naming:

Use meaningful names that convey the purpose and functionality of variables, methods, and classes.
Comprehensive JavaDoc:

Document classes, interfaces, and methods to explain their roles and usage.
Working Example in Main Method:

Provide a Main class with examples demonstrating the system's functionality.
Basic Unit Tests:

Include unit tests for critical components to ensure correctness.
Thread Safety Considerations:

Implement synchronization and use thread-safe data structures where necessary.
8. Expected Deliverables:
Working Solution with All Required Classes:

A complete and functional codebase covering all aspects of the problem.
Main Class with Usage Examples:

A Main class or equivalent to demonstrate how to initialize and interact with the system.
Clear Documentation of Assumptions:

Document any assumptions made to clarify design decisions and problem constraints.
Basic Test Cases:

Provide unit tests that validate core functionalities and handle edge cases.
Explanation of Design Choices:

Include comments or documentation explaining why certain design patterns and principles were applied.