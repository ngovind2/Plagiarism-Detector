// package cheatdetect;

public class DictionaryException extends Exception{
    
    public DictionaryException() {
        super();
    }
    
    public DictionaryException(String errMsg) { 
      super(errMsg); 
    }

    public DictionaryException(Throwable cause) {
        super(cause);
    }

    public DictionaryException(String errMsg, Throwable cause) {
        super(errMsg, cause);
    }
    
} 