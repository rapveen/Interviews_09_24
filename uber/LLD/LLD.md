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
    - use design patterns like Observer, Factory, Abstract Factory, Strategy, Adapter, State, Composite, Decorator but not limited to these
    - Keeping a 20-40 rule. First 20 min would be to scope down the vague problem to apis, objects, interfaces/abstract classes/super classes, strategies and specifications. Next 40 min for implementation and running test cases.
    - SOLID design principles
    - refined approach for core functionalities of the problem. Use optimized algorithms available in real world.


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

8. `Keeping a 20-40 rule. First 20 min would be to scope down the vague problem to apis, objects, interfaces/abstract classes/super classes, strategies and specifications. Next 40 min for implementation and running test cases.`

9. 