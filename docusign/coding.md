### Q 1
 /*    
    
     Implement a proximity search method.
    
     Given text as input, two keywords and a numeric range, return the number of times
     both keyword 1 and keyword 2 are found within the given range throughout the text, or 0
     if your search is not successful. The keywords themselves are considered to be part of the range.
     This makes 2 the minimum valid range for keyword proximity.
    
     For simplicity, assume all words are separated with a whitespace.
    
     input
    
         text(text)    : The early bird gets the worm
         keyword1(text): bird
         keyword2(text): worm
         range(number) : 4
		 
		      k1= bird k2 = worm
        text: the early bird bird worm worm gets the worm
        
        the early bird bird - 0
        early bird bird worm -  2  bird = 2, worm = 1 -> 2 
        bird bird worm worm  - 2  bird = 2, worm = 1 (as new word is same as old - neglect one word) -> 2
        bird worm worm  gets -0    bird = 0, worm = 0 as all are already processed
        worm worm gets the  - 0
        worm gets the worm - 0
    
    */
    public static void main(String[] args) {
        test("the early bird gets the worm", "bird", "worm", 4, 1);
        test("the early bird gets the worm", "bird", "worm", 1, 0);
        test("the early bird gets the worm a happy bird indeed", "bird", "worm", 4, 2);
        test("the early bird bird worm worm gets the worm.", "bird", "worm", 4, 4);
        test("the early bird gets the worm", "worm", "bird", 4, 1);
        test("the early worm gets the bird", "bird", "worm", 4, 1);
        test("the early bird gets the worm", "bird", "worm", 3, 0);
        test("the early bird gets the worm", "bird", "missing", 4, 0);
        test("The early bird gets the worm", "The", "bird", 3, 2);
    }


### Q 2 
Playlist shuffle question
A: https://claude.ai/chat/16b4db2c-b2f4-49a6-8c41-c8cf6477ef97

### Q 3
efficient string matching for a keyword in a huge file from disk
A: https://claude.ai/chat/a68a7a93-937c-4410-be3f-d8a9d1d1594d