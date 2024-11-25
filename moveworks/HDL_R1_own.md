AWS is in the very early phase, where new cloud services such as EC2, DynamoDB, and S3 are under development. Before the launch of these cloud services, we need to build the billing infrastructure, so AWS can charge customers for their use of different services. The task is to design this billing infrastructure.
Different AWS cloud services should be able to send usage data to the billing service at any granularity.
Billing service/infrastructure needs to generate bills for different accounts.
Account users should be able to view their service usage through AWS portal.

Func Req:
Data Ingestion: billing infra
Data aggregation: data usage metrics
bill generation
Analytics UI portal -> something like Grafana, visualizing the usage patterns, cost analysis based on that.
pricing models support[tier pricing, on-demand, spot etc categories]


non-func:
scalability
High availability
low latency
Data consistency
Exteensibility
Focus on External users purpose only
Estimations:
write: 50k QPS
memory estimates
1M users
per account per month: 100MB


EC2 : S/M/L price per min * time used
S3 : Read /  Write per GB * GBs and read 
Dynamo : Number of reads and writes  * price per operation

event that will be published by EC2
How does it process the event
How will usage querying will work on EC2


EC2 
runinstances
Instance launch(InstanceLaunch)
InstaceID
instaceType(S, M, L)
regionID
startTime
accountID
metadataTags
Instance termination(InstanceTermintae)
InstaceID
instaceType(S, M, L)
regionID
stopTime
accountID
metadataTags
StartInstances
Event processing pipeline
EC2 Servcie—> Generates events in the logs 
logs in S3-> Athena to do the querying -> AWS Glue svc to batching jobs and process them.
EC2 emits an InstanceLaunch Event
EventBridge routes to Kinesis stream
Lamda func van read the events from Kinesis, extract the fields necessary in a NoSQL DB
Dynamo DB : 
{start : t} {end : t+7000}
Get the real-time usage data from DB or S3(by region or event_type, 



User I want to see EC2 cost in last 1 month grouped by region and AWS role i want one row for each week

Partition Key: Region#AWS_Role (us-east-1#Admin)
Sort Key: week -> 2024-W33
Fields:
region
role
week
usageMins
cost
{
regionRole: 
}


### Q - How do you calculate the pricing for EC2 if u only have LaunchEvent but not the TerminateEvent?
    you are getting 2 eveents, start event at t and end event coming at some time t+7000
    what is the correlation between them
    how will you combine them together
### Q - Do you get events ? what type of events
### Q - How do you get these events -> in any logs?
How will you capture these events, like start at time and end at time

### Q - where do you store these logs
### Q - is it S3 or DynamoDB or RedShift, what is the store?
### Q - what is the Schema of the store? is it SQL or NoSQL
### Q - If it is NoSQL, can you write what is the JSON format of the document
### Q - What should be the partition key and a sort key
### Q - DO you know DynamoDB at all?
### Q - Do you know how pricing is calculated by EC2 usage
### Q - what is the user/system flow of pricing calculation 
### Q - Do you track price per instance or collective instances in AWS billign calculation
### Q - User I want to see EC2 cost in last 1 month grouped by region and AWS role i want one row for each week