1. ### Design Movie Ticket System ### 
    Don't store the data in DB, but store it in memory. You only have 1 machine.
    Code GET Search and POST Book Ticket.
    How do you handle race conditions?
2. ### Design Parking lot ### 
3. ### Design CircuitBreaker pattern ###

https://leetcode.com/problems/design-twitter/description/

medium max sum in contiguous array

Given a set of N people (numbered 1, 2, 3,.....N) We would like to split everyone into two groups of any size. Each Person may dislike some other people, and they should not go into the same group.
Formally if dislikes[i] = [a,b], it means its not allowed to put in the people numbered a & b into the same group.
Return true if and only if possible to split everyone into two groups.
Input: N = 4, dislikes = [[1,2], [1,3],[2,4]]
Output : true
Explanation: group1 [1,4], group2 [2,3]
Input: N = 3 dislikes = [[1,2],[1,3],[2,3]]
Output: false