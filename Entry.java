// package cheatdetect;

public class Entry  {
    
    // Attributes
    private String key;
    private Pair value;
    
    // Constructor
    public Entry(String key, Pair value) {
        this.key = key;
        this.value = value;
    }
    
    // Returns String object representing entry's key 
    public String Key() {
        return this.key;
    }
    
    // Returns Pair object representing entry's value 
    public Pair Value() {
        return this.value;
    }
}