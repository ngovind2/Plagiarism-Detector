// package cheatdetect;
// Uses Horner's polynomial accumulation method as the hash code function to produce an integer value for a specified key

public class StringHashCode implements HashCode {

    public int giveCode(Object key) {
        
        // Store key as a String object
        String str = (String) key;

        // Use polynomial accumulation method
        // First coefficient is represented by last character of string
        int hashCode = str.codePointAt(str.length() - 1); 

        // Using 33 as the polynomial variable
        for (int i = str.length() - 2; i >= 0; i--)
        	hashCode = hashCode * 33 + str.codePointAt(i);

		// Return hash code value 
        return hashCode; 
        
    }
}