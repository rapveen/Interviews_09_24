1. He explained for 15mins about the team and what they are building
2. he asked How did you plan for integrating Adyen
    ==> What challenges you have faced during the integration.
    ==> As it involved with 3rd party GWs, what issues you have got as most of the documentation are outdated.
    ==> the questions main focus was how did you ensure that there was velocity given that some of these things cant move fast, there is some schedule planned, how do you manage the timelines
3. what is your experience supporting internal teams, to build success
like in salesforce, some other team at salesforce is maintaining these platforms which you developed, so how do you support those teams, like you mostly worked on the customer facing softwares. Did you ever worked on helping out internal teams?
    ==> I mentioned about CodeDeploy project how it is a painpoint and then became success.
    and later got accepted org-wide. Written beautifully 
    ==> How much you have contributed for this project?
    I said starting from planning phase I've done this. Aditionally I've taken a practitioner certificate inorder to solve this.
    
4. have you ever had any feedback on your design may be by your manager or someone which you did not expect etc, tell about email subscription system
    ==> initially I discussed about Kafka vs SQS  but he said this is not any learning you have learnt
    --> then explained that even though initially the MVP we built convinced manager but when went to Director level he asked how in long term it would be sufficient for us. So then I have redesigned my options like team specific standard Queues

5. tell about a project wherer my observations are initially wrong but when I have to correct it after discussions and careful thought
    ==> I mentioned about HVLC problem. Admitted that due to my code changes the issue is occurring.
        debugged the issue and resolved it. explained properly about the after actions I've taken.

In the end also explained about the AWS codedeploy main stages

# AWS CodeDeploy Implementation Guide

## 1. Prerequisites Setup
1. Create an IAM role for CodeDeploy service
   - Create role with `AWSCodeDeployRole` policy
   - Allow EC2 instances to use this role

2. Create an IAM role for EC2 instances
   - Attach `AWSCodeDeployFullAccess` policy
   - Attach `AmazonS3ReadOnlyAccess` policy if deploying from S3

## 2. Install CodeDeploy Agent
On each target EC2 instance (Amazon Linux 2):
```bash
# Install CodeDeploy agent
sudo yum update
sudo yum install ruby
sudo yum install wget
cd /home/ec2-user
wget https://aws-codedeploy-us-east-1.s3.amazonaws.com/latest/install
chmod +x ./install
sudo ./install auto
sudo service codedeploy-agent status
```

## 3. Application Setup
1. Create application in CodeDeploy console:
   ```bash
   aws deploy create-application --application-name MyApp
   ```

2. Create deployment group:
   ```bash
   aws deploy create-deployment-group \
     --application-name MyApp \
     --deployment-group-name MyDeploymentGroup \
     --deployment-config-name CodeDeployDefault.OneAtATime \
     --service-role-arn arn:aws:iam::ACCOUNT_ID:role/CodeDeployServiceRole
   ```

## 4. Application Files Structure
```
MyApplication/
├── appspec.yml
├── scripts/
│   ├── before_install.sh
│   ├── after_install.sh
│   ├── application_start.sh
│   └── application_stop.sh
└── source/
    └── application files
```

Sample appspec.yml:
```yaml
version: 0.0
os: linux
files:
  - source: /source
    destination: /var/www/html/
hooks:
  BeforeInstall:
    - location: scripts/before_install.sh
      timeout: 300
      runas: root
  AfterInstall:
    - location: scripts/after_install.sh
      timeout: 300
      runas: root
  ApplicationStart:
    - location: scripts/application_start.sh
      timeout: 300
      runas: root
  ApplicationStop:
    - location: scripts/application_stop.sh
      timeout: 300
      runas: root
```

## 5. Deployment Process
1. Package your application:
   ```bash
   zip -r application.zip .
   ```

2. Upload to S3:
   ```bash
   aws s3 cp application.zip s3://your-bucket/
   ```

3. Create deployment:
   ```bash
   aws deploy create-deployment \
     --application-name MyApp \
     --deployment-group-name MyDeploymentGroup \
     --s3-location bucket=your-bucket,key=application.zip,bundleType=zip
   ```

## 6. Deployment Lifecycle Events
CodeDeploy executes hooks in this order:
1. ApplicationStop
2. DownloadBundle
3. BeforeInstall
4. Install
5. AfterInstall
6. ApplicationStart
7. ValidateService

## 7. Monitoring and Troubleshooting
1. Check deployment status:
   ```bash
   aws deploy get-deployment --deployment-id <deployment-id>
   ```

2. View logs:
   - Application logs: `/var/log/aws/codedeploy-agent/codedeploy-agent.log`
   - Deployment logs: `/opt/codedeploy-agent/deployment-root/deployment-logs/codedeploy-agent-deployments.log`

3. Common troubleshooting steps:
   - Verify IAM roles and permissions
   - Check CodeDeploy agent status
   - Review deployment logs
   - Validate appspec.yml syntax
   - Ensure script permissions are correct