"""
You are given the customer visit log of a shop represented by a 0-indexed string customers consisting only of characters 'N' and 'Y':

if the ith character is 'Y', it means that customers come at the ith hour
whereas 'N' indicates that no customers come at the ith hour.
If the shop closes at the jth hour (0 <= j <= n), the penalty is calculated as follows:

For every hour when the shop is open and no customers come, the penalty increases by 1.
For every hour when the shop is closed and customers come, the penalty increases by 1.
Return the earliest hour at which the shop must be closed to incur a minimum penalty.

Note that if a shop closes at the jth hour, it means the shop is closed at the hour j.

 

Example 1:

Input: customers = "YYNY"
Output: 2
Explanation: 
- Closing the shop at the 0th hour incurs in 1+1+0+1 = 3 penalty.
- Closing the shop at the 1st hour incurs in 0+1+0+1 = 2 penalty.
- Closing the shop at the 2nd hour incurs in 0+0+0+1 = 1 penalty.
- Closing the shop at the 3rd hour incurs in 0+0+1+1 = 2 penalty.
- Closing the shop at the 4th hour incurs in 0+0+1+0 = 1 penalty.
Closing the shop at 2nd or 4th hour gives a minimum penalty. Since 2 is earlier, the optimal closing time is 2.
Example 2:

Input: customers = "NNNNN"
Output: 0
Explanation: It is best to close the shop at the 0th hour as no customers arrive.
Example 3:

Input: customers = "YYYY"
Output: 4
Explanation: It is best to close the shop at the 4th hour as customers arrive at each hour.
"""

class Solution:
    def bestClosingTime(self, customers: str) -> int:
        max_score = score = 0
        best_hour = -1

        for i, c in enumerate(customers):
            score += 1 if c == 'Y' else -1
            if score > max_score:
                max_score, best_hour = score, i
                
        return best_hour + 1
    

"""
Q- PART 1
You are given a string S of 'Y' and 'N' seperated by spaces like: Y N N Y N Y Y N. Where S[i] represents if there are any customers waiting on our shop on ith day.
We are also given index ind on which we will open close the shop. We need to find what will be the total loss if we close the shop on ind day.
There will be a loss of 1 if we closed the shop on ith day and there exist any customer waiting on day after i.
Similary loss of 1 if we are open on ith day and there are no customer before i.


Eg: S= "Y N N Y N Y Y N" and ind = 2
Ans: 1 (1 day when no customer is there before ind 2) + 3 (3 days when customers are waiting after ind 2) = 4
"""
def calculate_total_loss(S, ind):
    # Remove spaces from the input string
    S = S.replace(' ', '')
    
    # Initialize counters
    loss_before = 0
    loss_after = 0
    
    # Iterate through the string
    for i, c in enumerate(S):
        if i < ind and c == 'N':
            loss_before += 1
        elif i > ind and c == 'Y':
            loss_after += 1
    
    # Total loss is the sum of losses before and after the closing day
    total_loss = loss_before + loss_after
    return total_loss

def main():
    # Number of test cases
    T = int(input("Enter the number of test cases: "))
    
    for _ in range(T):
        # Input string S
        S = input("Enter the customer string (e.g., 'Y N N Y N Y Y N'): ")
        
        # Input index ind
        ind = int(input("Enter the closing day index: "))
        
        # Calculate and print the total loss
        result = calculate_total_loss(S, ind)
        print(f"Total loss for closing on day {ind}: {result}")

if __name__ == "__main__":
    main()


"""
Q - PART 2
Find the day when loss is minimum

"""

def find_minimum_loss_day(S):
    # Remove spaces from the input string
    S = S.replace(' ', '')
    
    # Initialize counters
    loss_before = 0
    loss_after = S.count('Y')  # Total number of 'Y's in the string
    min_loss = float('inf')
    best_day = -1
    
    # Iterate through each day
    for i in range(len(S)):
        # Calculate total loss if closing on day i
        total_loss = loss_before + loss_after
        
        # Update minimum loss and best day
        if total_loss < min_loss:
            min_loss = total_loss
            best_day = i
        
        # Update loss_before and loss_after for the next iteration
        if S[i] == 'N':
            loss_before += 1
        elif S[i] == 'Y':
            loss_after -= 1
    
    return best_day

def main():
    # Number of test cases
    T = int(input("Enter the number of test cases: "))
    
    for _ in range(T):
        # Input string S
        S = input("Enter the customer string (e.g., 'Y N N Y N Y Y N'): ")
        
        # Find and print the best day to close the shop
        best_day = find_minimum_loss_day(S)
        print(f"The best day to close the shop is: {best_day}")

if __name__ == "__main__":
    main()


"""
Q - PART 3
now another followup, 
there are some logs and which contains this kind of pattern. For each valid pattern you have to print the pattern and find minimum loss for that.
Logs would be like: \t\t L R L R \n\nmtswioehdvdfoj R L R /t/n it could be very random.
"""
import re

def find_minimum_loss_day(S):
    # Remove spaces from the input string
    S = S.replace(' ', '')
    
    # Initialize counters
    loss_before = 0
    loss_after = S.count('Y')  # Total number of 'Y's in the string
    min_loss = float('inf')
    best_day = -1
    
    # Iterate through each day
    for i in range(len(S)):
        # Calculate total loss if closing on day i
        total_loss = loss_before + loss_after
        
        # Update minimum loss and best day
        if total_loss < min_loss:
            min_loss = total_loss
            best_day = i
        
        # Update loss_before and loss_after for the next iteration
        if S[i] == 'N':
            loss_before += 1
        elif S[i] == 'Y':
            loss_after -= 1
    
    return min_loss

def process_logs(logs):
    # Pattern to match valid sequences of 'Y' and 'N'
    pattern_regex = re.compile(r'\b[YN]+\b')
    
    # Iterate through each log entry
    for log in logs:
        # Find all valid patterns of 'Y' and 'N' in the log
        patterns = pattern_regex.findall(log)
        
        # Process each pattern to find the minimum loss
        for pattern in patterns:
            min_loss = find_minimum_loss_day(pattern)
            print(f"Pattern: {pattern}, Minimum Loss: {min_loss}")

def main():
    # Input the logs (assuming multi-line string)
    logs = [
        "\t\t L R L R \n\nmtswioehdvdfoj Y N Y N Y /t/n",
        "random text Y N N Y",
        "invalid pattern YYY/NNN but Y N Y Y"
    ]
    
    # Process the logs and output the results
    process_logs(logs)

if __name__ == "__main__":
    main()
