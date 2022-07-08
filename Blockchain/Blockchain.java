import java.util.*;
import java.math.*;
import java.security.KeyPair;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;

public class Blockchain {
    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static ArrayList<String> History = new ArrayList<String>();
    private static final SimpleDateFormat ctime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static void main(String[] args) throws Exception {
        //ECDSA
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        ECGenParameterSpec random = new ECGenParameterSpec("secp256k1");
        
        keyGen.initialize(random, new SecureRandom());

        KeyPair pair = keyGen.generateKeyPair();
        PrivateKey priv = pair.getPrivate();
        PublicKey pub = pair.getPublic();
        
        blockchain.add(new Block("Genesis Block", "0"));
        while(true) {
            Scanner input = new Scanner(System.in); 
            System.out.print("Enter the data you want to put in the block: ");
            String data = input.nextLine();
            String[] codes = signature(priv, pub, data);
            System.out.println("The data has been digitally signed");
            History.add(codes[1]);
            boolean result = verify(codes[0], codes[1], data);
            System.out.println("..................................");
            if(result) {
               System.out.println("Transaction verified!");
               blockchain.add(new Block(data, blockchain.get(blockchain.size() - 1).hash));
            }
            for (int i =  0; i < blockchain.size(); i++)
	         { 		 
                  System.out.println();
                  if(i >= 1) {
                     System.out.println("Signature: " + History.get(i-1));
                  }
                  System.out.println("Block"+"(" + (i + 1) +"): ");
                  System.out.println("Time creation: " + ctime.format(blockchain.get(i).timeStamp));  
	               System.out.println("Blockchain data: " + blockchain.get(i).data);
                  System.out.println("Previous Hash: " + blockchain.get(i).previousHash);
                  System.out.println("Block Hash: " + blockchain.get(i).hash);
                  System.out.println();  		
	          }
               if(Validation() && result) {
                  System.out.println("The blockchain is Valid! \nTransaction Succesful! \n");
               } else {             
                  System.out.println("Previous\\Current Hashes are not equal, Block has been altered! \nTransaction Failed! \n");
                  break;
               }
            
            
            Scanner input3 = new Scanner(System.in);  
            System.out.print("Do you want to delete a block?(yes/no): ");
            String remove = input3.nextLine();
            if(remove.charAt(0) == 'y') {
               for (int i =  0; i < blockchain.size(); i++)
	            { 		 
                  System.out.println("Block " + (i + 1)); 
               }
               Scanner input4 = new Scanner(System.in);  
               System.out.print("Pick Block number you want to delete: ");
               int num = input4.nextInt();               
               blockchain.remove(num - 1);
               continue;            
            }
            
            Scanner input4 = new Scanner(System.in);  
            System.out.print("Do you want to end? ");
            String end = input4.nextLine();
            if(end.charAt(0) == 'y') {
                break;       
            }
         }
          
      }

    
    public static Boolean Validation() {
      Block currentBlock;
      Block previousBlock;
      for (int i = 1; i < blockchain.size(); i++) {
         currentBlock = blockchain.get(i);
         previousBlock = blockchain.get(i - 1);
         if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
            return false;
         }
        if (!previousBlock.hash.equals(currentBlock.previousHash)) {
            return false;
        }
      }
      return true;
    }
    
    public static boolean verify(String pubkey, String sig, String plaintext) 
    throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, UnsupportedEncodingException, SignatureException {
        Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA");
        KeyFactory kf = KeyFactory.getInstance("EC");

        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(pubkey));

        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        ecdsaVerify.initVerify(publicKey);
        ecdsaVerify.update(plaintext.getBytes("UTF-8"));
        boolean results = ecdsaVerify.verify(Base64.getDecoder().decode(sig));
        return results;
    }
    
    
    public static String[] signature(PrivateKey priv, PublicKey pub, String plaintext) 
    throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, UnsupportedEncodingException, SignatureException {
        String[] code = new String[2];
        Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");
        ecdsaSign.initSign(priv);
        ecdsaSign.update(plaintext.getBytes("UTF-8"));
        byte[] signature = ecdsaSign.sign();
        code[0] = Base64.getEncoder().encodeToString(pub.getEncoded());
        code[1] = Base64.getEncoder().encodeToString(signature);
        return code;    
    }

  }

