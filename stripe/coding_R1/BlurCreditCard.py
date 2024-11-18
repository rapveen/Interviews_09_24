"""
 * Q - PART 1
 * We can assume that the input string contains tokens separated by a single whitespace.


Credit card numbers are represented by strings that contain anywhere from 13-16 digits (inclusive).
The function will analyze the input string and look for any token that looks
like a credit card (ie it contains between 13-16 digits).
The function will then replace all of the digits with an "x" character
EXCEPT for the last 4 digits for that token.
It will then return the full string with the data redacted.
Examples
// 16 digit number gets redacted, other tokens will not be touched
redact_card_numbers("1234567890123456 is a number")
returns "xxxxxxxxxxxx3456 is a number"
// No credit card found, no transformation needed
redact_card_numbers("basic_string 12345 no redaction")
returns "basic_string 12345 no redaction"
// 16 digit number in the middle of the string is redacted, other tokens are left alone.
redact_card_numbers("an embedded number 1234567890123456 in the string")
returns "an embedded number xxxxxxxxxxxx3456 in the string"

"""

def redact_card_numbers(input_string: str) -> str:
    # Handle empty input
    if not input_string:
        return input_string
    
    # Split string into tokens
    tokens = input_string.split()
    
    # Process each token
    redacted_tokens = []
    for token in tokens:
        # Check if token is a credit card number (13-16 digits)
        if token.isdigit() and 13 <= len(token) <= 16:
            # Redact all but last 4 digits
            redacted = 'x' * (len(token) - 4) + token[-4:]
            redacted_tokens.append(redacted)
        else:
            redacted_tokens.append(token)
    
    # Join tokens back with spaces
    return ' '.join(redacted_tokens)

# Test cases
def test_redact_card_numbers():
    # Test case 1: 16 digit number
    assert redact_card_numbers("1234567890123456 is a number") == "xxxxxxxxxxxx3456 is a number"
    
    # Test case 2: No credit card
    assert redact_card_numbers("basic_string 12345 no redaction") == "basic_string 12345 no redaction"
    
    # Test case 3: Embedded credit card
    assert redact_card_numbers("an embedded number 1234567890123456 in the string") == "an embedded number xxxxxxxxxxxx3456 in the string"
    
    # Test case 4: 13 digit number
    assert redact_card_numbers("1234567890123") == "xxxxxxxxx0123"
    
    print("All test cases passed!")

# Run tests
if __name__ == "__main__":
    test_redact_card_numbers()


"""
Q - PART 2

Fortunately, credit card numbers have some additional structure to them.
For example:


Cards issued by Visa will start with a 4 and will only have 13 OR 16 digits in them.
Cards issued by American Express will ALWAYS have the first two digits of 34 or 37
and will always contain 15 digits.
Cards issued by Mastercard will ALWAYS be 16 digits and will ALWAYS have the
first two digits between 51-55 (inclusive) OR will have the first four digits
between 2221-2720 (inclusive)
Modify your redact_card_numbers function to only redact valid Mastercard, Visa, or
American Express credit card numbers. Like the previous part, the redaction will replace
all of the digits with an “x” character EXCEPT for the last 4 digits.
Examples
// No credit card found, no transformation needed
redact_card_numbers("basic_string 12345 no redaction")
returns "basic_string 12345 no redaction"
// 16 digit number does not get redacted as it does not match brand criteria
redact_card_numbers("1234567890123456 is not a card")
returns "1234567890123456 is not a card"
// 16 digit number Visa does get redacted as it matches brand criteria
redact_card_numbers("4234567890123456 is a valid visa")
returns "xxxxxxxxxxxx3456 is a valid visa"

"""

def is_valid_card_number(card_number: str) -> bool:
    """Check if the card number matches any valid card type pattern."""
    if not card_number.isdigit():
        return False
    
    length = len(card_number)
    
    # Visa: Starts with 4 and is 13 or 16 digits
    if card_number.startswith('4'):
        return length in {13, 16}
    
    # American Express: Starts with 34 or 37 and is 15 digits
    if card_number.startswith(('34', '37')):
        return length == 15
    
    # Mastercard: 16 digits and starts with 51-55 or 2221-2720
    if length == 16:
        # Check 51-55 range
        if 51 <= int(card_number[:2]) <= 55:
            return True
        # Check 2221-2720 range
        if len(card_number) >= 4 and 2221 <= int(card_number[:4]) <= 2720:
            return True
    
    return False

def redact_card_numbers(input_string: str) -> str:
    """Redact valid credit card numbers in the input string."""
    # Handle empty input
    if not input_string:
        return input_string
    
    # Split string into tokens
    tokens = input_string.split()
    
    # Process each token
    redacted_tokens = []
    for token in tokens:
        if is_valid_card_number(token):
            # Redact all but last 4 digits
            redacted = 'x' * (len(token) - 4) + token[-4:]
            redacted_tokens.append(redacted)
        else:
            redacted_tokens.append(token)
    
    # Join tokens back with spaces
    return ' '.join(redacted_tokens)

# Test cases
def test_redact_card_numbers():
    # Test Visa cards
    assert redact_card_numbers("4234567890123456 is a valid visa") == "xxxxxxxxxxxx3456 is a valid visa"  # 16 digits
    assert redact_card_numbers("4234567890123 is a valid visa") == "xxxxxxxxx0123 is a valid visa"  # 13 digits
    
    # Test American Express
    assert redact_card_numbers("341234567890123 is amex") == "xxxxxxxxxxx0123 is amex"  # starts with 34
    assert redact_card_numbers("371234567890123 is amex") == "xxxxxxxxxxx0123 is amex"  # starts with 37
    
    # Test Mastercard
    assert redact_card_numbers("5134567890123456 is mastercard") == "xxxxxxxxxxxx3456 is mastercard"  # 51 prefix
    assert redact_card_numbers("5534567890123456 is mastercard") == "xxxxxxxxxxxx3456 is mastercard"  # 55 prefix
    assert redact_card_numbers("2221567890123456 is mastercard") == "xxxxxxxxxxxx3456 is mastercard"  # 2221 prefix
    assert redact_card_numbers("2720567890123456 is mastercard") == "xxxxxxxxxxxx3456 is mastercard"  # 2720 prefix
    
    # Test invalid cases
    assert redact_card_numbers("basic_string 12345 no redaction") == "basic_string 12345 no redaction"
    assert redact_card_numbers("1234567890123456 is not a card") == "1234567890123456 is not a card"
    assert redact_card_numbers("3612345678901234 not amex") == "3612345678901234 not amex"  # wrong prefix
    assert redact_card_numbers("5034567890123456 not mastercard") == "5034567890123456 not mastercard"  # wrong prefix
    
    print("All test cases passed!")

# Run tests
if __name__ == "__main__":
    test_redact_card_numbers()