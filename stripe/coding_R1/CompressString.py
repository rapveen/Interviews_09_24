"""
Q - PART 1 :
Given a String, split it into major parts separated by special char / (slash).
For each major part that’s split by /, we can further split it into minor parts separated by . (dot).


We assume the given Strings:


Only have lower case letters and two separators (., /).
Have no empty minor parts (no leading / trailing separators or consecutive separators like "/a", "a/", "./..").
Have >= 3 letters in each minor part.
Produce a final output String such that for each minor part, we only keep the first and last letter of it, interpolated with the count of letters in between.


Function to implement :


String compress_part_one(String s) {
return compressed_s;
}


## Examples
Example 1
Given input =>
stripe.com/payments/checkout/customer.maria
output =>s4e.c1m/p6s/c6t/c6r.m3a

"""

def compress_part_one(s: str) -> str:
    # Split into major parts using '/'
    major_parts = s.split('/')
    
    # Process each major part
    compressed_major_parts = []
    for major_part in major_parts:
        # Split into minor parts using '.'
        minor_parts = major_part.split('.')
        
        # Process each minor part
        compressed_minor_parts = []
        for minor_part in minor_parts:
            # Get first and last letter
            first = minor_part[0]
            last = minor_part[-1]
            # Count middle letters
            middle_count = len(minor_part) - 2
            # Create compressed version
            compressed = f"{first}{middle_count}{last}"
            compressed_minor_parts.append(compressed)
        
        # Join minor parts with dots
        compressed_major = '.'.join(compressed_minor_parts)
        compressed_major_parts.append(compressed_major)
    
    # Join major parts with slashes
    return '/'.join(compressed_major_parts)

def test_compress_part_one():
    # Test case 1
    input1 = "stripe.com/payments/checkout/customer.maria"
    expected1 = "s4e.c1m/p6s/c6t/c6r.m3a"
    assert compress_part_one(input1) == expected1, f"Test case 1 failed. Expected {expected1}, got {compress_part_one(input1)}"
    
    # Test case 2 - simple case
    input2 = "abc.def"
    expected2 = "a1c.d1f"
    assert compress_part_one(input2) == expected2, f"Test case 2 failed"
    
    # Test case 3 - single path
    input3 = "hello/world"
    expected3 = "h3o/w3d"
    assert compress_part_one(input3) == expected3, f"Test case 3 failed"
    
    print("All test cases passed!")

# Run tests
if __name__ == "__main__":
    test_compress_part_one()


"""
Q - PART 2

For example, imagine compressing a URL such as "section/how.to.write.a.java.program.in.one.day".
After compressing it by following the rules in Part 1, the second major part still has 9 minor parts after compression.


Task: Therefore, to further compress the String, we want to only keep at most m (m>0) compressed minor parts from Part 1 within each major part.


If a major part has more than m minor parts, we keep the first (m-1) minor parts as is,
but concatenate the first letter of the m-th minor part and the last letter of the last minor part with the count of letters in the original string.
This means that if you build on top of results that have numeronyms, you should expand
on the numbers to count the number or letters in the original String, e.g, "w1w.s4e.c1m"
should be compressed into "w10m" instead of "w7m" (go through the examples below). You can either reuse Part 1 output or not.
If a major part has less than or equal to m minor parts, keep all the individual minor parts as they are from Part 1.


Example :
Given:
str = stripe.com/payments/checkout/customer.maria.doe
minor_parts = 2


(after Part 1 compression)
=>
s4e.c1m/p6s/c6t/c6r.m3a.d1e


(then after Part 2 compression)
=>
s4e.c1m/p6s/c6t/c6r.m6e


Step-by-step guide for part 2 compression:


For the last major part "c6r.m3a.d1e", keep the first 2-1=1 minor part "c6r"
For the rest of the minor parts "m3a.d1e", compress into "m6e"
Combine "c6r.m6e" (from step 1 and 2) as the last compressed major part
Do the same for the rest of the major parts (if applicable)


"""

def count_letters_in_compressed(compressed_part: str) -> int:
    """Count original letters in a compressed part (e.g., 'w1d' represents 3 letters)"""
    # Extract number from middle of string (between first and last char)
    middle = compressed_part[1:-1]
    return int(middle) + 2  # Add 2 for first and last letters

def compress_further(compressed_parts: list[str], m: int) -> str:
    """Further compress a list of compressed minor parts if count > m"""
    if len(compressed_parts) <= m:
        return '.'.join(compressed_parts)
    
    # Keep first m-1 parts
    result_parts = compressed_parts[:m-1]
    
    # Calculate total letters in remaining parts
    total_letters = 0
    remaining_parts = compressed_parts[m-1:]
    for part in remaining_parts:
        total_letters += count_letters_in_compressed(part)
    
    # Create final compressed part (first letter of m-th part + count + last letter of last part)
    first_letter = remaining_parts[0][0]
    last_letter = remaining_parts[-1][-1]
    final_part = f"{first_letter}{total_letters-2}{last_letter}"
    
    result_parts.append(final_part)
    return '.'.join(result_parts)

def compress_part_two(s: str, m: int) -> str:
    # First do Part 1 compression
    major_parts = s.split('/')
    
    # Process each major part
    final_parts = []
    for major_part in major_parts:
        # Split into minor parts
        minor_parts = major_part.split('.')
        
        # First compress each minor part (Part 1)
        compressed_minors = []
        for minor in minor_parts:
            first = minor[0]
            last = minor[-1]
            middle_count = len(minor) - 2
            compressed_minors.append(f"{first}{middle_count}{last}")
        
        # Then do Part 2 compression if needed
        compressed_major = compress_further(compressed_minors, m)
        final_parts.append(compressed_major)
    
    return '/'.join(final_parts)

# Test cases
def test_compress_part_two():
    # Test case 1
    input1 = "stripe.com/payments/checkout/customer.maria.doe"
    expected1 = "s4e.c1m/p6s/c6t/c6r.m6e"
    result1 = compress_part_two(input1, 2)
    assert result1 == expected1, f"Test 1 failed. Expected {expected1}, got {result1}"
    
    # Test case 2 - long sequence of minor parts
    input2 = "section/how.to.write.a.java.program.in.one.day"
    result2 = compress_part_two(input2, 2)
    expected2 = "s5n/h1w.t25y"  # First part unchanged, second part compressed
    assert result2 == expected2
    
    # Test case 3 - exactly m minor parts
    input3 = "abc.def.ghi/xyz.uvw"
    result3 = compress_part_two(input3, 3)
    expected3 = "a1c.d1f.g1i/x1z.u1w"  # No further compression needed
    assert result3 == expected3
    
    print("All test cases passed!")

if __name__ == "__main__":
    test_compress_part_two()