import java.util.*;
   
public class Block {
    public String hash;
    public String previousHash;
    public String data;
    public long timeStamp;
   
    public Block(String data, String previousHash)
    {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }
   
    public String calculateHash()
    {
        String calculatedhash = Hashing.sha256(previousHash + Long.toString(timeStamp)+ data);
        return calculatedhash;
    }
}