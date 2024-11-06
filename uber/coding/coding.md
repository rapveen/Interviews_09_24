### **Instructions**
1. `segment-tree graph type question`
2. `discuss the follow-up in detail`
    - `test cases also`
3. [n-Ary Trees](https://leetcode.com/articles/introduction-to-n-ary-trees/)
4. [Quad tree](https://leetcode.com/problems/construct-quad-tree/)
5. [Bus Routes](https://leetcode.com/problems/bus-routes/)
    ==> `a follow-up with weights added on the routes.`
6. find route in a matrix with some obstacles
7. [text justification](https://leetcode.com/problems/text-justification/)
8.  `a variation of alien dictionary`
9.  `top K`
    Given a stream of infinite list of words starting from time 0, find the maximum frequency K words for a given list of range of hours.
    solved using min heaps, sets and dictionary where I store the occurance in dictionary and create heap of size K for every hour, and use set to store those K values for optimal solution.(max 24hr history to be maintained)
10. `valid BST`
11. `graphs currency exchnage problem`
12. `Given a dictionary of words, where each list represents the set of words which are synonyms and a list of Queries. Find out the distinct number of queries.


A query will be duplicate if it has
(x,y) ->


(x, syn(y))
(syn(x), y)
(syn(x), syn(y))
Example:


dict = [['a', 'b', 'c', 'd'],
['p', 'q', 'r', 's'],
['u', 'v', 'w']
['x', 'c', 'y', 'z']]


Queries: (a,b), (p,q), (a,p), (a,q), (u,a), (y,a)
Ans: 4`

13. Given a string and an positive array of length 26, find maximum possible substrings that are possible such that each character is can be arranged in the substring with maximum length present in the array.
Ex :
"aab" , [2,1,0,0,0,..0]
result = length[('a' 'a' 'b'), ('aa','b)]
"aaba", [3,1,0,0,0...0]
result = length[('aa','b','a'), ('a', 'ab','a'), ('a','a','b', 'a'), ('aa', 'ba')]


Note: Not sure if the example clarifies the question but point is that array contains the max value of substring that a character can have so if a[0] = 3 so 'a' can occur in substring with max length 3 and if a[1]=2 then 'b' can be included in the substrings with max length 2


I used DP and Queue where I will bsf the string and store the permutation value for each charcter possible, not able to get the answer as my que was exploading and not poping the value corretly. Let me know if there is a similar problem, not able to find the solution for this.


What are my chances here to get to the Design and HM Round?

14. LC Hard graph problems
15. https://stackoverflow.com/questions/1761626/weighted-random-numbers
16. [pacific flow](https://leetcode.com/problems/pacific-atlantic-water-flow/description/)
17.  You're given N cities connected with M bi-directional roads, you need to convert this into a unidirectional graph such that you have minimum number of saperated components (saperated component means, where there is no incoming edge).
18. (use fenwick tree)[https://leetcode.com/problems/count-of-smaller-numbers-after-self/description/]
19. (find median)[https://leetcode.com/problems/find-median-from-data-stream/]  
    ==>  followup, where definition of median can be any number in the range of two nearest powers of 2 from actual median.
        1 2 3 5 6 7 8 -> actual median - 5 , Loose median -> any number between 4 to 8
        Expected constant time and constant space solution.
        Solved with buckets having counts of number in a range of powers of 2.
        bucket 1 - 0 to 2 - count
        bucket 2 - 3 to 4 - count
        bucket 3 - 5 to 8 - count
20. 