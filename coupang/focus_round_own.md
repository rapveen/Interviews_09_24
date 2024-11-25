1. Introduction
2. Tell me aabout a project where you have done extensive research on decisions
    and then went ahead in that
        => I've said about Email scheduler feature
        => but the intro time I quite stumbled as I had the project for the conflicts one.
    When I'm explaining projects, he aske dme why SQS why not Kafka
    => if in future u want to work for high scale like 50M then is this extensible?
        --> I said we can use SQS upscaling for the needs, but could improve my ans
    => Then said Apache Airflow implemented.
    => how to ensure that the mail has reached the end user, tell the technical aspects, the dlquqe etc
        are there any mechanisms for the same purpose?
        --> I said we used DLQ, but I could improve
    => how did we scale, what is the bottleneck in this project, like workers trying to fetch the mail, 
        consumer stage scaling issues happened.

        how did we solve that? using airflow logs? give details

    => how reusable was this, some other team also want to use it for notification, 
        how is it included in this project
    => can the same sqs be used for other teams as well, because my msgs can get choked right, how is that handled
3. Tell me any situation where you have breakdown the problem step-wise and understood and worked on that issue
    => I said Lua script issue in MoEngage.
    => He asked did you find the issue out or someone else
        -> I said taken help from DevOps team and due to their recent change the issue occurred
    => He asked what is the RCA written on your team
        -> I said it is basically on the DevOps team to notify each team involved during these changes.
        -> But I could've added from this time we ensured we are running our UTs, FTs in an Argus instance and added alerts just like in SF to notify is any expected cases are failing.
    => which API gateway were you using there.
4. Say any issue that yourself fixed but not by anyone in your firm
    => I said about HVLC project
    => He asked why is SQL query slow in first place
        -> I couldn't say that it is the expected exec time. And also the queries are legacy based. ALso this came into picture only when we run these queries on High Data clients like Flipkart
    => 