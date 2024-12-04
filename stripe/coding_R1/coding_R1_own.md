### Part 1 Q

 Account Balances, Part 1
Description
At Stripe, we move a lot of money around. It's often important to us to know how much money an account has at a given time.
Implement a function which computes the account balances given a list of transactions.
The input to the function is a list of balance transactions, which have an account ID, a timestamp, a currency (three-letter ISO currency code), and an amount (in minor units, e.g. cents for USD)
account_id, timestamp, currency,minor_units 
acct_123,1, usd, 800

acct_321,2, usd, 100

acct_123,4, usd, -300

acct_321,5, usd, -300

acct_321,9, usd, -800

acct_321,10, usd, 1000
You can assume that the transactions are in monotonically increasing order of timestamp.
Please return the final account balance of each account, unless the account has no balance (i.e. exclude zero-balance accounts).
Feel free to construct the list as in-memory structures in your preferred programming language - there is no need to parse text files if you don't want to., FYI: minor units are cents, like 800 are 800 cents


Transactions:

acct_123,1,usd,800 → Balance: acct_123 USD: 800
acct_321,2,usd,100 → Balance: acct_321 USD: 100
acct_123,4,usd,-300 → Balance: acct_123 USD: 800 - 300 = 500
acct_321,5,usd,-300 → Balance: acct_321 USD: 100 - 300 = -200
acct_321,9,usd,-800 → Balance: acct_321 USD: -200 - 800 = -1000
acct_321,10,usd,1000 → Balance: acct_321 USD: -1000 + 1000 = 0


Final Balances:

acct_123: USD 500
acct_321: USD 0 (excluded)


#!/bin/python3
import sys

def compute_account_balances(transactions):
    """
    Computes the final balance for each account.
    
    Args:
    transactions (list of tuples): Each tuple contains 
        (account_id, timestamp, minor_units)
    
    Returns:
    dict: Mapping of account_id to balance, excluding zero balances.
    """
    account_balances = {}

    for txn in transactions:
        account_id, timestamp, amount = txn
        if account_id not in account_balances:
            account_balances[account_id] = 0
        account_balances[account_id] += amount

    # Remove zero balances
    final_balances = {account: balance for account, balance in account_balances.items() if balance != 0}

    return final_balances

def main():
    # Example transactions
    transactions = [
        ("acct_123", 1, 800),
        ("acct_321", 2, 100),
        ("acct_123", 4, -300),
        ("acct_321", 5, -300),
        ("acct_321", 9, -800),
        ("acct_321", 10, 1000)
    ]

    # Edge Case 1: No Transactions
    transactions_empty = []

    # Edge Case 2: All Zero Balances
    transactions_zero = [
        ("acct_001", 1, 500),
        ("acct_001", 2, -500)
    ]

    # Edge Case 3: Single Account, Multiple Transactions
    transactions_single = [
        ("acct_single", 1, 100),
        ("acct_single", 2, 200),
        ("acct_single", 3, -150),
        ("acct_single", 4, -150)
    ]

    # Edge Case 4: Negative Balances
    transactions_negative = [
        ("acct_999", 1, -100),
        ("acct_999", 2, -200),
        ("acct_999", 3, 50)
    ]

    # Edge Case 5: Large Number of Transactions
    transactions_large = [("acct_large", i, 1) for i in range(1, 100001)]

    # Running test cases
    print("Example Transactions:")
    print(compute_account_balances(transactions))
    print("\nEdge Case 1 - No Transactions:")
    print(compute_account_balances(transactions_empty))
    print("\nEdge Case 2 - All Zero Balances:")
    print(compute_account_balances(transactions_zero))
    print("\nEdge Case 3 - Single Account, Multiple Transactions:")
    print(compute_account_balances(transactions_single))
    print("\nEdge Case 4 - Negative Balances:")
    print(compute_account_balances(transactions_negative))
    print("\nEdge Case 5 - Large Number of Transactions:")
    # Uncomment the next line to test large transactions
    # print(compute_account_balances(transactions_large))

if __name__ == "__main__":
    main()



### Par 2 Q
Next follow-up to this question is as below, extend the code for this follow-up. 
 
Similar to above question, for this followup question as well - first give analysis, the optimal algorithm being used, data structure being used in the code etc and then rest of the approach with intuition also, edge cases, dry run and then give the optimal code with comments, with edge case test cases in main()  
 
Question: Part 2

Description
At Stripe, we move a lot of money around. It's often important to us to know how much money an account has at a given time.
Implement a function which computes the account balances given a list of transactions.
The input to the function is a list of balance transactions, which have an account ID, a timestamp, a currency (three-letter
ISO currency code), and an amount (in minor units, e.g. cents for USD)

account_id, timestamp, currency,minor_units

acct_123,1,usd,800

acct_321,2,usd, 100

acct_123,4, usd, -300
acct
321,5, usd, -300

acct_321,9, usd, -800
acct
_321, 10,usd, 1000
You can assume that the transactions are in monotonically increasing order of timestamp.
Part 2:
We'd like it to be the case that each individual merchant balance never goes below zero. Please modify your code to reject all transactions which would bring the merchant balance below zero.
Please return the list of rejected transactions, and the final account balances per account.
Feel free to construct the list as in-memory structures in your preferred programming language - there is no need to parse text files if you don't want to.

Output Requirements:

Final Account Balances: A dictionary mapping each account_id to its final balance, excluding accounts with a zero balance.
Rejected Transactions: A list of transactions that were rejected because they would cause the account balance to drop below zero.

Transactions:
acct_123,1,800 → Balance: acct_123 = 800
acct_321,2,100 → Balance: acct_321 = 100
acct_123,4,-300 → Balance: acct_123 = 800 - 300 = 500
acct_321,5,-300 → Attempted Balance: 100 - 300 = -200 → Rejected
acct_321,9,-800 → Attempted Balance: 100 - 800 = -700 → Rejected
acct_321,10,1000 → Balance: acct_321 = 100 + 1000 = 1100

#!/bin/python3
import sys

def compute_account_balances_with_rejections(transactions):
    """
    Computes the final balance for each account and identifies rejected transactions
    that would cause the balance to drop below zero.
    
    Args:
    transactions (list of tuples): Each tuple contains 
        (account_id, timestamp, minor_units)
    
    Returns:
    tuple:
        - dict: Mapping of account_id to balance, excluding zero balances.
        - list: List of rejected transactions.
    """
    account_balances = {}
    rejected_transactions = []

    for txn in transactions:
        account_id, timestamp, amount = txn
        current_balance = account_balances.get(account_id, 0)
        proposed_balance = current_balance + amount

        if proposed_balance < 0:
            # Reject the transaction
            rejected_transactions.append(txn)
        else:
            # Accept the transaction
            account_balances[account_id] = proposed_balance

    # Remove zero balances
    final_balances = {account: balance for account, balance in account_balances.items() if balance != 0}

    return final_balances, rejected_transactions

def main():
    # Example transactions
    transactions = [
        ("acct_123", 1, 800),
        ("acct_321", 2, 100),
        ("acct_123", 4, -300),
        ("acct_321", 5, -300),
        ("acct_321", 9, -800),
        ("acct_321", 10, 1000)
    ]

    # Edge Case 1: No Transactions
    transactions_empty = []

    # Edge Case 2: All Transactions Rejected
    transactions_all_rejected = [
        ("acct_001", 1, -100),
        ("acct_001", 2, -200)
    ]

    # Edge Case 3: All Transactions Accepted
    transactions_all_accepted = [
        ("acct_002", 1, 500),
        ("acct_002", 2, -200),
        ("acct_002", 3, 300)
    ]

    # Edge Case 4: Multiple Accounts with Mixed Outcomes
    transactions_mixed = [
        ("acct_100", 1, 1000),
        ("acct_200", 2, 500),
        ("acct_100", 3, -1500),  # Rejected
        ("acct_200", 4, -300),
        ("acct_300", 5, -100),    # Rejected
        ("acct_100", 6, 600)
    ]

    # Edge Case 5: Single Account with Multiple Rejections
    transactions_single_multiple_rejections = [
        ("acct_single", 1, 100),
        ("acct_single", 2, -50),
        ("acct_single", 3, -60),  # Rejected
        ("acct_single", 4, -10)   # Rejected
    ]

    # Edge Case 6: Large Number of Transactions
    transactions_large = [("acct_large", i, 1) for i in range(1, 100001)]

    # Running test cases
    print("Example Transactions:")
    balances, rejected = compute_account_balances_with_rejections(transactions)
    print("Final Balances:", balances)
    print("Rejected Transactions:", rejected)

    print("\nEdge Case 1 - No Transactions:")
    balances, rejected = compute_account_balances_with_rejections(transactions_empty)
    print("Final Balances:", balances)
    print("Rejected Transactions:", rejected)

    print("\nEdge Case 2 - All Transactions Rejected:")
    balances, rejected = compute_account_balances_with_rejections(transactions_all_rejected)
    print("Final Balances:", balances)
    print("Rejected Transactions:", rejected)

    print("\nEdge Case 3 - All Transactions Accepted:")
    balances, rejected = compute_account_balances_with_rejections(transactions_all_accepted)
    print("Final Balances:", balances)
    print("Rejected Transactions:", rejected)

    print("\nEdge Case 4 - Multiple Accounts with Mixed Outcomes:")
    balances, rejected = compute_account_balances_with_rejections(transactions_mixed)
    print("Final Balances:", balances)
    print("Rejected Transactions:", rejected)

    print("\nEdge Case 5 - Single Account with Multiple Rejections:")
    balances, rejected = compute_account_balances_with_rejections(transactions_single_multiple_rejections)
    print("Final Balances:", balances)
    print("Rejected Transactions:", rejected)

    print("\nEdge Case 6 - Large Number of Transactions:")
    # Uncomment the next lines to test large transactions
    # balances, rejected = compute_account_balances_with_rejections(transactions_large)
    # print("Final Balances:", balances)
    # print("Number of Rejected Transactions:", len(rejected))

if __name__ == "__main__":
    main()



Next follow-up to this question is as below, extend the code for this follow-up. 
 
Similar to above question, for this followup question as well - first give analysis, the optimal algorithm being used, data structure being used in the code etc and then rest of the approach with intuition also, edge cases, dry run and then give the optimal code with comments, with edge case test cases in main()  

### Part 3a Q 
Question: Part 3a

You can assume that the transactions are in monotonically increasing order of timestamp.
Part 3a
Stripe offers a functionality where "platform" accounts have sub-accounts that we call "connected accounts". For example, a broker for selling widgets might be a platform, and the connected accounts could be individual widget producers.
Some of our platforms are willing to guarantee their connected accounts' negative balances. For simplicity, we'll examine the case with a single platform. The balance of the platform account is used as a "reserve": that is, rather than rejecting a transaction on a connected account that drives its balance negative, we would make up the difference using the platform's balance.
3a: Update your code to support debiting the platform balance for the difference when the connected account's balance is insufficient for a debit. If the platform has insufficient balance, reject the transaction. Your code should take the platform's account ID as a parameter. All other accounts are connected to that platform.

For both parts, please return the maximum amount of the platform's funds that were used as reserves, in addition to the account balances and rejected transactions.
Feel free to construct the list as in-memory structures in your preferred programming language - there is no need to parTe text files if you don't want to.
A few example test cases (1 and 2 for part 3a, 3 and 4 for part 3b)
In the examples, acct_123 is the platform.
Test case 1 (for part 3a)

Test case 1 (for part 3a)
account_id, timestamp, currency,minor_units
acct_123,1, usd, 1000
acct_321,2, usd, 100
acct_321,4, usd, -300
Expected final balances:
acct_123: 800 usd
acct_321: 0 usd (not returned)
Max reserved amount: 200 usd
Test case 2 (for part 3a)
account_id, timestamp, currency, minor_units
acct_123,1, usd, 1000
acct_321,2, usd, 100
acct_321,4, usd, -300
acct_456,6, usd, -100
Expected final balances: acct_123: 700 usd
acct_321: 0 usd (not returned) acct_456: 0 usd (not returned)



## Analysis
Platform Account: A designated platform account (e.g., acct_123) that can cover negative balances of connected accounts using its own balance.
Reserve Mechanism:
When a connected account's debit would cause its balance to go negative, the platform account covers the deficit.
If the platform's balance is insufficient to cover the deficit, the transaction is rejected.
Track the maximum amount the platform's funds are used as reserves.

Transactions:

acct_123,1,1000 → Platform balance: 1000
acct_321,2,100 → acct_321 balance: 100
acct_321,4,-300 →
Attempted acct_321 balance: 100 - 300 = -200
Platform covers 200 from acct_123:
acct_321 balance: 0
acct_123 balance: 1000 - 200 = 800
Reserved Amount: 200
Final Balances:

acct_123: 800
acct_321: 0 (excluded)


#!/bin/python3
import sys

def compute_account_balances_with_platform(transactions, platform_account_id):
    """
    Computes the final balance for each account, utilizes platform's balance to cover deficits,
    identifies rejected transactions, and tracks the maximum reserved amount from the platform.
    
    Args:
    transactions (list of tuples): Each tuple contains 
        (account_id, timestamp, minor_units)
    platform_account_id (str): The account ID of the platform.
    
    Returns:
    tuple:
        - dict: Mapping of account_id to balance, excluding zero balances.
        - list: List of rejected transactions.
        - int: Maximum amount reserved from the platform's funds.
    """
    account_balances = {}
    rejected_transactions = []
    platform_reserved = 0
    max_reserved = 0

    for txn in transactions:
        account_id, timestamp, amount = txn

        # Initialize account balance if not present
        if account_id not in account_balances:
            account_balances[account_id] = 0

        if amount >= 0:
            # Credit transaction: simply add to the account balance
            account_balances[account_id] += amount
        else:
            # Debit transaction: attempt to deduct from the account
            current_balance = account_balances[account_id]
            proposed_balance = current_balance + amount

            if proposed_balance >= 0:
                # Sufficient balance: apply the debit
                account_balances[account_id] = proposed_balance
            else:
                # Insufficient balance: calculate deficit
                deficit = -proposed_balance  # Positive value
                # Initialize platform balance if not present
                if platform_account_id not in account_balances:
                    account_balances[platform_account_id] = 0
                platform_balance = account_balances[platform_account_id]

                if platform_balance >= deficit:
                    # Platform can cover the deficit
                    account_balances[account_id] = 0  # Set connected account balance to zero
                    account_balances[platform_account_id] -= deficit  # Deduct from platform
                    platform_reserved += deficit
                    # Update max_reserved if needed
                    if platform_reserved > max_reserved:
                        max_reserved = platform_reserved
                else:
                    # Platform cannot cover the deficit: reject transaction
                    rejected_transactions.append(txn)

    # Remove zero balances
    final_balances = {account: balance for account, balance in account_balances.items() if balance != 0}

    return final_balances, rejected_transactions, max_reserved

def main():
    # Define the platform account ID
    platform_account_id = "acct_123"

    # Test Case 1 (Part 3a)
    transactions_test1 = [
        ("acct_123", 1, 1000),
        ("acct_321", 2, 100),
        ("acct_321", 4, -300)
    ]
    # Expected final balances:
    # acct_123: 800
    # acct_321: 0 (excluded)
    # Max reserved amount: 200

    # Test Case 2 (Part 3a)
    transactions_test2 = [
        ("acct_123", 1, 1000),
        ("acct_321", 2, 100),
        ("acct_321", 4, -300),
        ("acct_456", 6, -100)
    ]
    # Expected final balances:
    # acct_123: 700
    # acct_321: 0 (excluded)
    # acct_456: 0 (excluded)
    # Max reserved amount: 300

    # Edge Case 1: No Transactions
    transactions_empty = []

    # Edge Case 2: All Transactions on Platform
    transactions_platform_only = [
        ("acct_123", 1, 500),
        ("acct_123", 2, -200),
        ("acct_123", 3, -300)
    ]
    # Expected final balances:
    # acct_123: 0 (excluded)
    # Max reserved amount: 0

    # Edge Case 3: All Transactions Rejected
    transactions_all_rejected = [
        ("acct_123", 1, 100),
        ("acct_321", 2, -200),  # Deficit: 200, Platform has 100 (insufficient)
        ("acct_456", 3, -50)    # Deficit: 50, Platform has 100 (after initial)
    ]
    # Expected final balances:
    # acct_123: 100
    # acct_321: 0 (excluded)
    # acct_456: 0 (excluded)
    # Rejected Transactions: [("acct_321", 2, -200)]
    # Max reserved amount: 0

    # Edge Case 4: Platform Insufficient to Cover Deficits
    transactions_insufficient_platform = [
        ("acct_123", 1, 100),
        ("acct_321", 2, 50),
        ("acct_321", 3, -100),  # Deficit: 50, Platform has 100 → covers
        ("acct_456", 4, -200)   # Deficit: 200, Platform has 50 → reject
    ]
    # Expected final balances:
    # acct_123: 50
    # acct_321: 0 (excluded)
    # acct_456: 0 (excluded)
    # Rejected Transactions: [("acct_456", 4, -200)]
    # Max reserved amount: 50

    # Edge Case 5: Multiple Connected Accounts with Interleaved Transactions
    transactions_mixed = [
        ("acct_123", 1, 1000),
        ("acct_321", 2, 500),
        ("acct_456", 3, 300),
        ("acct_321", 4, -600),  # Deficit: 100
        ("acct_456", 5, -400),  # Deficit: 100
        ("acct_789", 6, -50),   # Deficit: 50
        ("acct_789", 7, 100),
        ("acct_789", 8, -150),  # Deficit: 50
        ("acct_000", 9, -1000)  # Deficit: 1000, Platform has 700 → reject
    ]
    # Expected final balances:
    # acct_123: 100
    # acct_321: 0 (excluded)
    # acct_456: 0 (excluded)
    # acct_789: 0 (excluded)
    # Rejected Transactions: [("acct_000", 9, -1000)]
    # Max reserved amount: 300

    # Edge Case 6: Platform Balance Zero Initially
    transactions_zero_platform = [
        ("acct_321", 1, -100),  # Deficit: 100, Platform has 0 → reject
        ("acct_123", 2, 50),
        ("acct_321", 3, -50),   # Deficit: 50, Platform has 50 → covers
        ("acct_321", 4, -10)    # Deficit: 10, Platform has 0 → reject
    ]
    # Expected final balances:
    # acct_123: 0 (excluded)
    # acct_321: 0 (excluded)
    # Rejected Transactions: [("acct_321", 1, -100), ("acct_321", 4, -10)]
    # Max reserved amount: 50

    # Edge Case 7: Transactions That Exactly Exhaust Platform's Balance
    transactions_exact_exhaust = [
        ("acct_123", 1, 300),
        ("acct_321", 2, 100),
        ("acct_321", 3, -150),  # Deficit: 50 → covers
        ("acct_456", 4, -250)   # Deficit: 250 → covers (platform has 250)
    ]
    # Expected final balances:
    # acct_123: 0 (excluded)
    # acct_321: 0 (excluded)
    # acct_456: 0 (excluded)
    # Max reserved amount: 300

    # Running Test Cases
    test_cases = [
        ("Test Case 1 (Part 3a)", transactions_test1),
        ("Test Case 2 (Part 3a)", transactions_test2),
        ("Edge Case 1 - No Transactions", transactions_empty),
        ("Edge Case 2 - All Transactions on Platform", transactions_platform_only),
        ("Edge Case 3 - All Transactions Rejected", transactions_all_rejected),
        ("Edge Case 4 - Platform Insufficient to Cover Deficits", transactions_insufficient_platform),
        ("Edge Case 5 - Multiple Connected Accounts with Interleaved Transactions", transactions_mixed),
        ("Edge Case 6 - Platform Balance Zero Initially", transactions_zero_platform),
        ("Edge Case 7 - Transactions That Exactly Exhaust Platform's Balance", transactions_exact_exhaust)
    ]

    for test_name, txn_list in test_cases:
        print(f"\n{test_name}:")
        balances, rejected, max_reserved = compute_account_balances_with_platform(txn_list, platform_account_id)
        print("Final Balances:", balances)
        print("Rejected Transactions:", rejected)
        print("Max Reserved Amount:", max_reserved)

if __name__ == "__main__":
    main()


### Part 3b - Q
3b: The platform would like to be paid back funds that were used for the connected account's reserves. If a connected account has consumed the platform's reserve, and it later receives funds, we should preferentially repay the platform's reserve before crediting the connected account's balance.

Additional Feature:

Reserve Repayment Mechanism:
If the platform uses its balance to cover a connected account's deficit, the connected account is considered indebted to the platform.
When the indebted connected account later receives funds (positive transactions), those funds are first used to repay the platform's reserve until the debt is cleared.
Remaining funds, if any, are credited to the connected account's balance.


Transactions:

("acct_123", 1, "usd", 1000) → Platform balance: 1000.
("acct_321", 2, "usd", -300) →
acct_321 balance: -300 (deficit covered by platform).
acct_123 balance: 1000 - 300 = 700.
Platform reserve: 300.
("acct_321", 4, "usd", 200) →
acct_321 owes the platform 300.
Repays 200 to the platform.
Platform balance: 700 + 200 = 900.
acct_321 balance: -300 + 200 = -100.
("acct_321", 6, "usd", 150) →
Repays remaining 100 to the platform.
Platform balance: 900 + 100 = 1000.
Remaining 50 credited to acct_321.
acct_321 balance: 0.
Final Balances:

acct_123: 1000.
acct_321: 0 (excluded).



def compute_account_balances_with_repayment(transactions, platform_account_id):
    """
    Extends the platform reserve mechanism to handle repayment of debts
    by connected accounts before crediting their balance.

    Args:
    transactions (list of tuples): Each tuple contains 
        (account_id, timestamp, minor_units).
    platform_account_id (str): The account ID of the platform.

    Returns:
    tuple:
        - dict: Final account balances, excluding zero balances.
        - list: List of rejected transactions.
        - int: Maximum amount reserved from the platform's funds.
    """
    account_balances = {}
    account_debts = {}
    rejected_transactions = []
    platform_reserved = 0
    max_reserved = 0

    for txn in transactions:
        account_id, timestamp, amount = txn if len(txn) == 3 else txn[:3]

        # Initialize account balance and debt if not present
        if account_id not in account_balances:
            account_balances[account_id] = 0
        if account_id not in account_debts:
            account_debts[account_id] = 0

        if amount >= 0:
            # Positive transaction: First repay debt, then credit remaining amount
            debt = account_debts[account_id]
            if debt > 0:
                repayment = min(amount, debt)
                account_debts[account_id] -= repayment
                account_balances[platform_account_id] += repayment
                amount -= repayment
            # Credit remaining amount (if any) to the account balance
            account_balances[account_id] += amount
        else:
            # Negative transaction: Handle deficit using platform reserve
            current_balance = account_balances[account_id]
            proposed_balance = current_balance + amount

            if proposed_balance >= 0:
                # Sufficient balance: apply the debit
                account_balances[account_id] = proposed_balance
            else:
                # Insufficient balance: Calculate deficit
                deficit = -proposed_balance
                platform_balance = account_balances.get(platform_account_id, 0)

                if platform_balance >= deficit:
                    # Platform covers the deficit
                    account_balances[account_id] = 0  # Set connected account balance to zero
                    account_balances[platform_account_id] -= deficit
                    account_debts[account_id] += deficit
                    platform_reserved += deficit
                    max_reserved = max(max_reserved, platform_reserved)
                else:
                    # Platform cannot cover the deficit: reject transaction
                    rejected_transactions.append(txn)

    # Remove zero balances
    final_balances = {account: balance for account, balance in account_balances.items() if balance != 0}

    return final_balances, rejected_transactions, max_reserved


def main():
    platform_account_id = "acct_123"

    transactions = [
        ("acct_123", 1, 1000),
        ("acct_321", 2, -300),
        ("acct_321", 4, 200),
        ("acct_321", 6, 150),
    ]

    balances, rejected, max_reserved = compute_account_balances_with_repayment(transactions, platform_account_id)

    print("Final Balances:", balances)
    print("Rejected Transactions:", rejected)
    print("Max Reserved Amount:", max_reserved)


if __name__ == "__main__":
    main()



### Note:
I've completed till 3a Part only.

