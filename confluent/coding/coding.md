All rounds were 60 mins long. First 5 mins intro, 30-35 mins coding 
(approach discussion, implementation, optimisation, space time complexity), 5-10 mins modified original problem 
(add additional constraints and discuss the approach for that), 5 mins you can ask any questions to interviewer

✅find pattern in string, similar to https://leetcode.com/problems/find-and-replace-in-string/description/

Coding was an OOP problem that was basic hash map implementation and basic arithmetic.

✅Second coding question was to implement Linux Tail Command. Tail -N Command
At first, I proposed reading the file line-by-line from the beginning and using a queue to keep track of the last 'n' lines. The interviewer asked me to implement it.
I then suggested reading the file from the end instead, realizing it might be more efficient. However, I wasn't familiar with the specific file reader APIs to achieve this and requested the interviewer's assistance in finding them.
The interviewer didn't express any particular expectations and wasn't a Java expert.

standard system design question

behavior was also very normal.

Type: Practical file operations.
Focus: File reading and manipulation.
Expectations:
Solve practical problems using file APIs.
Discuss optimal implementation.
Evaluation: File handling, practical problem-solving.

✅Key-value store with expiration. 
Functions: put(key, value), get(key), and ❓get_average() (average of non-expired values). 
❓Data streamed in order of increasing timestamps.

✅Concurrency Focused - Design a cache similar to LRU but focus was how to handle concurrent reads and writes, locks, etc...

 Word and phrase search in list of documents

 ✅Validate and solve Sudoku.
 write code for a sudoku validator
- needs to mem efficient so use map to store counts
- print row/col/box as well where sudoku is not valid
- have a check to see if number is between 1-9
follow-up: write code for a sudoku solver
- normal as leetcode use backtracking
in other interviewer: Implemented a working solution and clarified how duplicate code could be removed if I had more time. However, the interviewer gave a no-hire recommendation because they felt I shouldn't have written duplicate code in the first place.
Follow up : solve given soduko


question on time complexity and Concurrency(Locking especially).

asked a DSA question in 2 parts first being easy-medium and second being medium-hard.
Again working code was required with good coding practice. Wrote it nicely and interviewer was impressed.

Was Asked a LLD question particularly on optimizing memory while reading a huge file as part of the problem.
Awareness of low level language memory constucts was key. Was able to solve it and run.
Working code was important again. Discussed further optimization on memory access when asked but
could not code as we were out of time. Struggled a little with API knowledge but was able to get through. Interviewer seemed fine in the end.


✅writing an autocomplete feature for an IDE. Few APIs and methods were provided.(similar to https://leetcode.com/discuss/interview-question/algorithms/132310/ide-autocomplete-feature)
Implement search by function signature for the purpose of IDE autocompletion. It had to take care of variadic functions. The best answer was using a trie
https://leetcode.com/discuss/interview-question/algorithms/132310/ide-autocomplete-feature
https://leetcode.com/playground/NfjSYG5W

 Multithreading question on Java. Related to the priority queue

focus on Multithreading and concurrency.
we could solve this using mutexes however they insisted using semaphores

Implement a scheduler in your language of choice. Theyll dive deep into concurrency.
I choose java and they basically had me reinvent timer task using primitives like locks, wait, notify
(Concurrency focused) - Design a scheduler which will execute tasks based on their time, fully focused on Concurrency and MultiThreading.
not distributed they wanted to check multi-threading concepts based on design they will ask questions how you avoid race-condition how you make sure each task run at their time and many more.

✅you're given k v pairs
funA: ['int','bool']
funB: ['int','int']
and queries like ['int','int']
return all functions that match the description
follow-up
you're also given a flag is variadic

funA: ['int','bool'] , isVariadic: true
funC: ['int','int'] , isVariadic: false
funB: ['int','int','int'] isVariadic: true
and queries like ['int','int']
return all functions that match the description
e.g
['int','int'] = funC
['int','int','int', 'int'] = funB

✅You're given a time based kv store,
window = 5 sec
time
01 put("foo", 1)
23 put("bar", 2)
34 get("foo") -> 1
45 getAverage() -> 1.5
67 get("foo") -> -1 (since key expired)
78 getAverage() -> 2 (since foo expired)
expectation was to optimize get() and getAverage()

In all rounds at the end i was asked to briefly explain how to do the given question using concurrency, but code was no required.

Medium/Hard question find a related to prefix matching find common sentences in multiple files.


✅ Modified LRU Cache
Implemented a working solution using map + pair data structure.
https://gist.github.com/hemantsonu20/24d0597a62787312b5e0dae05e01b71f ==> but may not proper

✅Word Search in a List of Documents
Implemented a working solution using an inverted index.
https://leetcode.com/discuss/interview-question/1878821/Confluent-or-Onsite-or-Search-Phrase-in-Document/1326396
LeetCode - Search Phrase in Document. A friend of mine taught me that whenever a question involves searching for a phrase or word in a list of documents, an inverted index is used.
They learned this in a database course at university. The LeetCode link includes a solution in the comments. 
✅Implement a Word Search Engine, given a list of documents with text, return the document ids that the given word belongs in. Followup: Search a phrase
Expectation was fully running code for both the questions


When was the last time a production incident happen, and how did you handle it
Implement pattern matching with at most one wildcard * (star)
Follow up: what if there were multiple stars

✅given a list of function names and the arguments they accept, implement a fast search for a function with certain args
used trie and provided full implementation. someone said (it is equivalent to IDE feature,
which gives you suggestions when you type the name of the function, it will give all possible suggestions)