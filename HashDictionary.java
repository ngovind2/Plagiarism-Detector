// package cheatdetect;
// A dictionary based on a hash table that implements the Dictionary interface

public class HashDictionary implements Dictionary {

	private Entry[] hashTable; 		// Hash table
	private HashCode code;         	// Hash code
	private int N = 7; 		        // Size of hash table
	private int count = 0;			// Number of entries in dictionary
	private float maxLoadFactor; 	// User-specified maximum allotment for load factor
	private int opsCount;			// Number of find, insert & remove operations have been performed
	private int probeCount;			// Number of probes that have occurred 
    
    // Empty entry for to replace deleted entries
    private Entry AVAILABLE = new Entry(null, new Pair(-1,-1));     

    // The following variables are used between methods & changed during rehashing 
    private int a = 0;          // MAD compression variables
    private int b = -1;
    private int q = 5;          // Arbitrary prime value < N used for double hashing technique
	

    /*     CONSTRUCTORS     */

	// Default constructor
	public HashDictionary() throws DictionaryException {

		// Throws DictionaryException with a specified detail message
		throw new DictionaryException("Please specify a hash code and a maximum load factor."); 
	} 

	// Constructor
	public HashDictionary(HashCode inputCode, float inputLoadFactor) {
 	
        // Assign values to attributes
 		hashTable = new Entry[N]; 
 		this.code = inputCode;
 		this.maxLoadFactor = inputLoadFactor;

		// Set random coefficients a and b for MAD compression
        // Coefficient a cannot be 0 or a factor of N
        while ( a == 0 || a % N == 0)
           a = (int)(Math.random()*Integer.MAX_VALUE); 

        // Coefficient b must be positive
        while (b < 0)
           b = (int)(Math.random()*Integer.MAX_VALUE); 

	} 

    /*     PRINCIPAL METHODS     */

	// Inserts the entry with the specified key and value to the dictionary. 
    public void insert(String k, Pair v){

    	// Increment number of operations performed thus far
    	opsCount++;
        
        // Resize & rehash if load factor exceeds maximum load factor
        if (getLoadFactor() >= maxLoadFactor)
            rehash();
        
        // Initially, stepSize = 0 so only MAD compression is applied
        int stepSize = 0;
        
        while (stepSize < N) {
            
            // Increment number of probes performed thus far
            probeCount++;
            
            // Get hashed index
            int i = getHashedIndex(code.giveCode(k),stepSize);
            
            // If index contents are empty or available, new entry can be inserted
            if ((hashTable[i] == null) || (hashTable[i] == AVAILABLE)) {
                
                // Add new entry 
                hashTable[i] = new Entry(k, v);
                
                // Increment number of entries in hash table
                count++;
                break;
            }
            // If index is unavailable (collision), increment step size and re-calculate new index position (via double hashing)
            else
                stepSize++;
        }
    }  

    // Removes the entry with the specified key from the dictionary. Throws 
    // DictionaryException if no entry with key in the dictionary          
    public Entry remove(String k) throws DictionaryException {

    	// Increment number of operations performed thus far
    	opsCount++;

        // Initially, stepSize = 0 & index is produced by MAD compression only (no double hashing)
    	int stepSize = 0;
        
        while (stepSize < N) {
            
            // Increment number of probes performed thus far
            probeCount++;
            
            // Get hashed index
            int i = getHashedIndex(code.giveCode(k),stepSize);

            // If index is empty, entry does not exist and cannot be removed
            if (hashTable[i] == null)
                throw new DictionaryException("This entry does not exist."); 

            // If index is AVAILABLE, skip key comparison and move to next possible entry index
            // If index is empty and unavailable, check for key match
            else if (hashTable[i] != AVAILABLE) {
                
                if (hashTable[i].Key().equals(k)) {
                    
                    // Store entry
                    Entry temp = hashTable[i];

                    // Replace contents with 'AVAILABLE'
                    hashTable[i] = AVAILABLE;

                    // Decrement number of entries in hash table
                    count--;

                    // Return entry that was once there
                    return temp;
                }
            }
            
            //If entry has not yet been found, increment step size and re-calculate new index position (via double hashing)
            stepSize++;
        }
        
        // If entry was not found, throw an exception
        throw new DictionaryException("This entry does not exist.");
    }

    // If entry with this key exists in the dictionary, returns this entry
    // Otherwise returns null
    public Entry find(String k) {
        
        // Increment number of operations performed thus far
        opsCount++;
        
        // Initially, stepSize = 0 so only MAD compression is applied (no double hashing)
        int stepSize = 0;
        while (stepSize < N) {
            
            // Increment number of probes performed thus far
            probeCount++;
            
            // Get hashed index; first return value will be index after MAD compression only (since stepSize = 0)
            int i = getHashedIndex(code.giveCode(k), stepSize);
            
            // If index's contents are null, assume entry was never inserted in table
            if(hashTable[i] == null)
                return null;

            // If index's contents represent a non-empty entry, check for a key match
            if (hashTable[i] != AVAILABLE) {
                if ((hashTable[i].Key()).equals(k))
                    return hashTable[i];
            }
            
            // If key was not found, increase step size and move to next possible insertion point
            stepSize++;
        }
        
        // If no key was found, return null
        return null;
        
    }  
    
    // Returns number of entries in hash table
    public int size() {
    	return count; 
    }

    // Returns average number of probes performed per operation
    public float averNumProbes() {
    	return (float) probeCount/opsCount; 
    }


    /*     ADDITIONAL METHODS    */

    // Applies MAD compression, as well as double hashing when collisions occur (open addressing)
    private int getHashedIndex(int hashCode, int i) { 
        
        // Applies MAD compression (ie. h(k) function)
        int h1 = (Math.abs(a * hashCode + b)) % N;

        // Suggested h'(k) function
        int h2 = q - Math.abs(h1) % q;

        // When stepsize (i) = 0, MAD compression alone is applied
        // Returns hashed index
        return (h1 + i * h2) % N;
    } 


	// Invoked when load factor exceeds maximum load factor intially specified by user through constructor
	private void rehash() {

        // Reset hash table count
        count = 0;
        
        // Replace variable used in 'getHashedIndex' with old capacity
        q = N;

        // Resize hash table to be greater than double the current capacity; new size must be prime 
        
        int x = 1; 
        N *= 2; 

        while (isPrime(N + x)  == false)
            x += 2;  // To avoid even numbers

        // Store new hash table size
        N += x; 

        // Recalculate coefficients a and b for MAD compression function (ensuring a % new size == 0)
        // Coefficient a cannot be 0 or a factor of the table's size
        while ( a == 0 || a % N == 0)
           a = (int)(Math.random()*Integer.MAX_VALUE); 

        // Coefficient b must be positive
        while (b < 0)
           b = (int)(Math.random()*Integer.MAX_VALUE); 

        
        // Store current entries in a temporary table
        Entry [] temp = new Entry[q];
        
        for (int i = 0; i < q; i++) {
            
            // Copy indicies with nonempty contents
            if ((hashTable[i] != null) && (hashTable[i] != AVAILABLE))
                temp[i] = hashTable[i];  
        }
        
        // Resize hash table to new capacity
        hashTable = new Entry[N];
        
        // Re-insert keys and pair values from temporary table into new hash table
        for (int i = 0; i < q; i++) {
            if(temp[i] != null)
                insert(temp[i].Key(), temp[i].Value());
        }

	}

	 // Calculates current load factor
    private float getLoadFactor() {
        return (float) count/N;
    }

    // Calculates if integer is prime 
    private boolean isPrime(int n) {
        
        // Test for even integers
        if (n % 2 == 0)
            return false;

        // Test other possible divisors/factors up to sqrt(n)
        for (int i = 3; i*i <= n; i += 2)
            if (n % i == 0)
                return false;

        // If n is not divisible by any integers <= sqrt(n), then it must be prime
        return true;
    }

}