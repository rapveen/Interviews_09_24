### HLD questions

1. **Uber Eats restaurant feed backend.**
       - main discussion revolved around how I would store and retrieve restaurant data efficiently. I suggested using a quad tree and geo hashing 

       - We discussed the quad tree storage logic, rebuilding it when a restaurant shuts down, and how a new node would construct or borrow the tree.

       - Serialization and deserialization were also discussed in detail on the whiteboard.

       - how and where the data will be stored and how the user/customer executive will get the data.
       -  To send the data to customer executive, used WebSockets.

2. **Instagram, Whatsapp, Google Photos, Google Calendar, Online multiplayer chess game.**
3. **Design In memory file system and implement:**
        [leetcode](https://leetcode.com/problems/design-in-memory-file-system/description/)
        - Create
        - Delete
        - Move (MOVE changes parent of the existing object. More like "mv" in unix)
        interested in Move method and implementation.
        Btw, this system should support Drive,Folder,File types (Drive being on the top hierarchy)

4. **HLD Basic Uber feature**
       Did great. Overall, the interviewer was happy. I followed this approach


       Know the user/actors of the system
       Be clear with features/modules to be designed
       Understand and discuss the scale of the system
       Consistency/Availability to be supported
       Picturise the sequence of events
       Thourough API design
       Start architecting the system with microservices
       Discussing the client-server interaction
       Indicate and discuss data flow with databases in mind (scaling, caching, sharding)
       Challenge the resilience of the system
       Other topics - health checks, logging and monitoring, security, low level design of some features
5. **Design Webhook**
6. **LSM Trees**
7. **Design a system in which user is subscribing to some stocks from list of stocks.**
        User will set some rules on basis of which if the rules are broken we have to sent notificaiton to user. For eg: let say user have subscribed to uber stock and set rule that if stock price goes above 60$ send me notification.
8. `you have to explain the design of a project that you have designed from scratch. I dont know if this is a mandatory requirement.`
9. `Asked me to design a system which levied taxes/penalties on drivers&riders with some constraints like completing 10 rides a month or getting lesser than average rating of that area consistently`
       ` which mechanism of alerting will i prefer (pull or push) and why   `
       
10. 