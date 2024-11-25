Design an algorithm to find the shortest path between two people in a social network. Coding on white board
Follow up question:- Scaling if we have a billion users

questions related to Master Slave architecture, Load Balancer, Nginx Proxy 

 Overview:  
	* We have a service like Docusign. 
	* The service records the id of each fully signed document in a database and then sends out a confirmation notification to the signing user.
	* For each successfully sent notificaiton, the service also writes a log in a flat log file on the server.
	Issue:
	* Due to a bug, 50% of the notifications are missing being sent out.
    * The missing notifications are not logged in this log file
    Task:
    * Given the database that records fully signed documents id and the flat log file that records all the successful notifications, how would you scalably find the missing notifications.
Ans: https://chatgpt.com/c/6741dacc-314c-8007-8c01-b982b2c90364


