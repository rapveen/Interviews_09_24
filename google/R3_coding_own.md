Given n points with (x, Y) integer coordinates. Find a straight line crossing throught the maximum amount of those given points

I proposed the solution in early time only
dry run an example as well within 15mins
started implementing code 
-> but in middle I kind of read xj as x. somehow I couldn't see it after the interview as well. May be due to low energy.
-> wasted 5-10mins on that logic finding. But interviewer is patient in hearning me out.
-> in the end he asked me the TC, explained it.
-> tell the TC by considering the GCD complexity as well, said it is o(log(min(a,b)))
-> he asked what would be the TC case when we are handling 64 bit integers. took some time and said 
    -> if we take extreme numbers like 2^64 then log(2^64) is 64 so constant time
-> he asked why did you take the key as string but not a double value.
    -> to which I said, Ease of Calculation:
        Handling Non-Integer Slopes:

        Slopes like 1/2 or -3/4 cannot be represented accurately as integers or fractions without additional effort. Using double allows for a straightforward representation of these non-integer slopes.
        Ease of Calculation:

        Calculating the y-intercept (intercept) directly requires floating-point arithmetic because it involves the slope (dy/dx) multiplied by xi, which can produce non-integer results.
        double ensures these calculations are performed accurately and without needing additional code to handle fractional arithmetic.
        Simplicity:

        Floating-point numbers are widely supported and easy to use for mathematical operations like division and subtraction.
        For example, computing the slope (dy/dx) and intercept (y = mx + b) using double avoids the need for explicit fraction management.

overall I feared alot 
as I missed some coding lines

lets see what is the review.
