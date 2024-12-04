Its a 45 min round
so plan properly to discuss max points
1. I messed out on the plan.
2. Couldn't complete complete flow within the time I thought I could ahve done it but couldn't.
3. only explained write path but not the read path
4. got carried away with adding complexities, while he is interested in simplistic solving of the problem

###  Design 1: Ledger Service
### chat link: https://chatgpt.com/g/g-hdVziaooT-system-design-gpt/c/6745e403-0b78-8007-8eb5-c6eb6ae784b8
Description
Problem:
 

Stripe is a payments platform that provides the ability for online businesses to charge money from customers and then get paid out periodically.

 

For example, a Stripe merchant called ShirtyPuff runs a website that sells t-shirts. Every time one of ShirtyPuff’s customers buys a t-shirt we collect money on behalf of the merchant. Periodically we pay the merchant an amount which is calculated by aggregating all the transactions.

 

There are other teams that take care of building software for actually sending and receiving money.

 

Your aim is to build a bookkeeping service (called Ledger) that keeps track of money sent and money received on behalf of a merchant. The purpose of this service is to record all financial activity and allow getting the account balance for a given merchant.


                                            
                   |                           ------------------        |
                   |                           |                |        |
---------------    |     --------------        | Bank Service   | ---------->
|             |    |     |            | -----> |                |        |
| Shirty Puff | -------> |            |        ------------------        |
| (merchant)  |    |     | Stripe API |                                  |
|             |    |     |            |        -----------------------   |
---------------    |     |            | -----> |                     |   |
                   |     --------------        | Credit Card Service | ----->
                   |                           |                     |   |
                   |                           -----------------------   |
                   |                                                     |
                   |                                                     |
                                            ----------
                                            |        |
                                            | Ledger |
                                            |        |
                                            ----------
The Ledger should support the following operations:

- Record money sent or received on behalf of a merchant

- Get account balance for a given merchant

 

What APIs would you build for a system like this?
How would you go about building this system?

Questions:
1. Do you need a microservice architecture as you are planning on using many services
2. Why can't we use a simple DB for this purpose
what type of database will you be using
as it involved multiple services, shold we implement a distributed lock/2PC or Saga patterns?
when I mentioned the database schema, I couldn't consider all cases into effect
like a transaction holds info about both the accounts. acct1, acct2. 
he said why didn't I mention in the start ?
I also mentioned read/write estimations. memory, tput.
He is curious like why write size is 1KB? we are storing only 4-5 fields in the DB then why those occupy 1KB size?
He specifically asks do you perform any batch processing, without comeing from my side.
How the read path will be?
what DB we will be using?

How do you make sure we dont do double committing of transactions into DB?
I said we can use a GUID specific to the Xn which prevents this. But as in the design I mentioned Journal svc, Event store. I kind of didn't drew it properly. I confused him that the dual event will be stored in event store and poses a risk of processing to DB and getting stored.
But I corrected him that once the event arrives at Transaction Manager, it checks the Journal svc for previous inception of the GUID and then discards it without any further processing.

As you mentioned low latency, High availability in Non-FR, but introducing these many svcs can slow up the system. How do you take care of that?
The tradeoff between **low latency** and **high availability** in a system with multiple services lies in the complexity and consistency model:

1. **Latency**: 
   - Introducing multiple services (e.g., Transaction Manager, Validation, Event Store) increases inter-service communication overhead, which can add latency.
   - Mitigation: Use **caching**, **optimized APIs**, and **asynchronous event processing** where eventual consistency is acceptable.

2. **Availability**:
   - Achieving high availability with multiple services requires redundancy, replication, and fault-tolerant designs. This increases complexity in managing failures, retries, and failovers.
   - Mitigation: Use **event-driven architectures** and design services to degrade gracefully or fallback when a dependent service is unavailable.

**Tradeoff**: By adding services for modularity and scalability, you sacrifice some latency for the benefits of fault isolation and independent scaling, while balancing consistency guarantees.


how would you handle idempotency