Plagiarism Detector
======

This simple program detects possible plagiarism in Java program files, using an unordered dictionary implemented with a hash table. If two files have a long common substring, this indicates possible cheating.

# Considerations 

Changing whitespace between programs changes common substrings but does not change program semantics. To be insensitive to the spacing issues, all the whitespace from the input files is removed.

Another issue is that, by changing variable names, two programs which have the same semantics can be made quite different. To make the program insensitive to variable name changes, all user-defined identifiers are replaced with the character '#'. 

The result of these operations is stored as a sequence of tokens. 

# Plagiarism Search

To detect cheating, this program will search for a common subsequence of length l tokens (l being the user specified parameter given as a command line argument) and output the matching substrings, with the original variable names and spacing reinstored.