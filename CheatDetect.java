// package cheatdetect;

import java.io.*;
import java.util.LinkedList;
import java.util.Iterator;
import java.lang.String;

public class CheatDetect {
               
	// Input: file name, start and end positions in the file
	// Output: file contents between start and end positions, inclusively, stored as a String
    private static String getStringfromFile(String name,int start,int end) throws java.io.IOException {
        
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(name));
        
        int ch;
        int  position = 0;
    
        while (position != start) {
            ch = in.read();
            position++;
            if (ch == -1) throw new IOException("Invalid position range");
         }
        
        StringBuffer buf = new StringBuffer();
        
        while (position <= end) {
            ch = in.read();
            position++;
            
            if (ch == -1) throw new IOException("Invalid position range");
            buf.append( (char) ch);  
        }
        
        in.close();
        return buf.toString();
    }



    // Outputs subsequences of a specified length that are common to two files
    // If no matches are found, nothing is printed
	public static void main(String[] args) throws java.io.IOException, DictionaryException {
		
		// Check that correct number of command-line arguments were entered; if not, exit program
		if (args.length != 4) {
			System.out.println("Error in invoking program. Please try again.");
			System.exit(1); 
		}
		
		// Store file names
		String keywords = args[0]; 
		String file1 = args[1]; 
		String file2 = args[2];
		
		// Store user-specified subsequence length
		int length = (new Integer(args[3])).intValue();

        // Ensure subsequence length is greater than 1
        if (length <= 1) {
            System.out.println("Error in invoking program. Please enter a length greater than 1.");
            System.exit(1); 
        }

        // Initialize hash dictionaries with suggested maximum load factor of 0.5
        HashDictionary file1Dict = new HashDictionary(new StringHashCode(), (float) 0.5);
        HashDictionary keywordDict = new HashDictionary(new StringHashCode(), (float) 0.5);

        // Create linked lists to hold matching substrings and corresponding positions
        LinkedList<Entry> matchInFile1 = new LinkedList<Entry>();
        LinkedList<Entry> matchInFile2 = new LinkedList<Entry>();

        // Try to catch possible IOException
        try {
            FileTokenRead file1Tokens = new FileTokenRead(file1);
            FileTokenRead file2Tokens = new FileTokenRead(file2);
            FileTokenRead keywordTokens = new FileTokenRead(keywords);
        } catch (IOException e){ 
            System.out.println("Error in invoking program. Please try again.");
            System.exit(1); 
        }

        // If no exception was thrown, file tokens can be read successfully 
        FileTokenRead file1Tokens = new FileTokenRead(file1);
        FileTokenRead file2Tokens = new FileTokenRead(file2);
        FileTokenRead keywordTokens = new FileTokenRead(keywords);
        
        // Create iterators for each token sequence 
        Iterator<Token> keywordIter = keywordTokens.getIterator(); 
        Iterator<Token> file1Iter = file1Tokens.getIterator();
        Iterator<Token> file2Iter = file2Tokens.getIterator(); 
        
        // Traverse token sequence & enter keywords into keywordDict before files are processed (for later comparison)
        while(keywordIter.hasNext()) {
            
            // Retrive a token from the sequence 
            Token entry = (Token) keywordIter.next();
                
            // Insert the token's value into keywordDict as the key
            // Value is irrelevant; enter a null pair
            String k = entry.Value();
            Pair v = new Pair(0,0);

            keywordDict.insert(k, v);
        }

        // Declaring string and pair objects to track subsequences and their start/end positions
        String substring;
        Pair substringValue;
            
        // Retrieve a token from file1 sequence
        Token token = (Token) file1Iter.next();

        while (file1Iter.hasNext()) {
            
            // Reset substring & substringValue 
            substring = null;
            substringValue = new Pair(0,0);
                
            // Create a substring of user-specified length 
            for (int i = 0; i < length; i++) {
                
                // For the substring's first token, record start position of token (ie. start position of substring)
                if (i == 0)
                    substringValue.start = token.startPosition();

                // For the substring's last token, record end position of token (ie. end position of substring)
                else if (i == length-1)
                    substringValue.end = token.endPosition();

                // Store as token string value as key
                String key = token.Value();

                // Change non-keywords that begin wih a letter to '#'
                if(keywordDict.find(key) == null && (int)key.charAt(0) >= 65 && (int)key.charAt(0) <= 122)
                        key = "#";

                // If key is first in substring sequence:
                if (i == 0)
                    substring = key;
                
                // If not first in sequence, concatenate key to sequence
                else
                    substring += key;
                
                // Retrieve next token in file1 token sequence
                if (file1Iter.hasNext())
                    token = (Token) file1Iter.next();
                
                // If no subsequences of user-specified length exist in file1, exit program since no match possible
                else {
                    if (file1Dict.size() == 0) {
                        System.out.println("No subsequence of specified length in " + file1);
                        System.exit(1);
                    }
                }
                    
            }
            
            // If it exists, insert subsequence into file1Dict
            file1Dict.insert(substring, substringValue);
        }



         // Retrieve a token from file2 sequence
        token = (Token) file2Iter.next();

        while (file2Iter.hasNext()) {
                
            // Reset substring & substringValue 
            substring = null;
            substringValue = new Pair(0,0);
                
            // Create a substring of user-specified length 
            for (int i = 0; i < length; i++) {
                
                // For the substring's first token, record start position of token (ie. start position of substring)
                if (i == 0)
                    substringValue.start = token.startPosition();

                // For the substring's last token, record end position of token (ie. end position of substring)
                else if (i == length-1)
                    substringValue.end = token.endPosition();

                // Store as key
                String key = token.Value();                

                // Change non-keywords that begin wih a letter to '#'
                if(keywordDict.find(key) == null && (int)key.charAt(0) >= 65 && (int)key.charAt(0) <= 122)
                        key = "#";

                // If key is first in substring sequence:
                if (i == 0)
                    substring = key;
                
                // If not first in sequence, concatenate key to sequence
                else
                    substring += key;


                // Retrieve next token in file2 token sequence
                if (file2Iter.hasNext())
                    token = (Token) file2Iter.next();
                
                // If no subsequences of user-specified length exist, exit program since no match possible
                else {
                    if (matchInFile2.isEmpty()) {
                        System.out.println("No subsequence of specified length in " + file1);
                        System.exit(1);
                    }
                }

            }

                
            // If it exists, compare subsequence in file2 with entries in file1Dict (ie. with file1 subsequences)
            // Matching strings are added to corresponding linked lists to be printed out later
            
            if (file1Dict.find(substring) != null && substringValue.End() != 0) {
            
                // Move entry from file1's dictionary to file1's linked list
                matchInFile1.addLast(file1Dict.remove(substring));
                    
                // Add current subsequence to file2's linked list
                matchInFile2.addLast(new Entry(substring, substringValue));
            }

        }
                
        // Convert '#' to original representation and output results of matched subsequences
            
        while(matchInFile1.isEmpty() == false && matchInFile2.isEmpty() == false) {
            Entry firstMatch = matchInFile1.removeFirst();
            Entry secondMatch = matchInFile2.removeFirst();
            
            System.out.println("Found in " + file1 + ":");
            System.out.println(getStringfromFile(file1,firstMatch.Value().Start(),firstMatch.Value().End()));
            
            System.out.println("Found in " + file2 + ":");
            System.out.println(getStringfromFile(file2,secondMatch.Value().Start(),secondMatch.Value().End()));

            System.out.println();
        }
    }     
}