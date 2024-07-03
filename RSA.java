import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;

public class RSA {
    public static void main(String[] args) {

        // Input message
        Scanner input = new Scanner(System.in);
        System.out.print("Enter Message: ");
        String messageString = input.nextLine();
        input.close();

        // Convert message to BigInteger
        BigInteger message = new BigInteger(messageString.getBytes(StandardCharsets.UTF_8));

        // Generates the public and private keys
        BigInteger[] keys = generateKeys();
        System.out.println("Original Message: " + messageString);

        // Encrypts the message using public key
        BigInteger ciphertext = encryption(message, keys[0], keys[2]);
        System.out.println("Encrypted Message: " + ciphertext);

        // Decrypts the message using private key
        BigInteger decryptedMessage = decryption(ciphertext, keys[1], keys[2]);
        System.out.println("Decrypted Message: " + new String(decryptedMessage.toByteArray(), StandardCharsets.UTF_8));
    }

    // Encrypts the message, C = P^e (mod n)
    public static BigInteger encryption(BigInteger plaintext, BigInteger e, BigInteger n) {
        return fastExponentiation(plaintext, e, n); 
    }

    // Decrypts the message, P = C^d (mod n)
    public static BigInteger decryption(BigInteger ciphertext, BigInteger d, BigInteger n) {
        return fastExponentiation(ciphertext, d, n);
    }

    // Finds the result of base^e (mod n) using the fast exponentiation algorithm
    public static BigInteger fastExponentiation(BigInteger base, BigInteger exponent, BigInteger modulo) {
        
        // Anything mod 1 will be 0
        if (modulo.equals(BigInteger.ONE))
            return BigInteger.ZERO;

        // Converts the exponent to binary string  
        String binaryString = exponent.toString(2);
        int length = binaryString.length();

        // Converts the binary string into binary int, and put it into an array
        int[] binaryArray = new int[length];
        for (int i = 0; i < length; i++) {
            
            // Convert character '0' or '1' to integer 0 or 1
            binaryArray[i] = Character.getNumericValue(binaryString.charAt(i));
        }

        // Stores the base
        BigInteger temp = base;

        // Loops through every binary digit
        for(int j=1; j<binaryArray.length; j++) {

            // If the binary number is 1
            if (binaryArray[j] == 1) {

                // The base is squared, then it is multiplied with itself
                base = base.multiply(base);
                base = base.multiply(temp);
                base = base.mod(modulo);
            }

            // If the binary number is 0
            else if (binaryArray[j] == 0) {

                // The base is only squared
                base = base.multiply(base);
                base = base.mod(modulo);
            }
        }

        // The final results of the modular exponentiation is returned
        return base;
    }

    // Generates the public and private key 
    public static BigInteger[] generateKeys() {
        BigInteger[] keys = new BigInteger[3];

        // Generate two large prime numbers
        SecureRandom secureRandom = new SecureRandom();
        BigInteger p = BigInteger.probablePrime(1024, secureRandom);
        BigInteger q = BigInteger.probablePrime(1024, secureRandom);

        System.out.println("p: " + p);
        System.out.println("q: " + q);

        // Calculate n = p * q
        BigInteger n = p.multiply(q);
        keys[2] = n; 

        System.out.println("n: " + n);

        // Calculate φ(n) = (p-1) * (q-1)
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        System.out.println("φ(n): " + phi);

        // Find public exponent e
        BigInteger e;
        do {

            // Finds a random number for e where, 1 < e < φ(n)
            e = new BigInteger(phi.bitLength(), new SecureRandom());

        // Ensures the e is 1 < e < φ(n) and gcd(e,φ(n)) = 1
        } while (e.compareTo(BigInteger.ONE) <= 0 || e.compareTo(phi) >= 0 || !findGcd(phi,e).equals(BigInteger.ONE));

        System.out.println("e: " + e);
    
        // Calculate private exponent d using extended Euclidean algorithm
        BigInteger d = findMultiplicativeInverse(phi,e);

        System.out.println("d: " + d);
        
        keys[0] = e; // public exponent
        keys[1] = d; // private exponent
        keys[2] = n; // modulo

        return keys;
    }

    // Finds the gcd between 2 numbers
    public static BigInteger findGcd(BigInteger a, BigInteger b) {

        // Performs euclidean algorithm
        while (!a.mod(b).equals(BigInteger.ZERO)) {
            BigInteger temp = b;
            b = a.mod(b);
            a = temp;
        }

        // returns the gcd of the 2 numbers
        return b;
    }

    // Finds the multiplicative inverse of a number
    public static BigInteger findMultiplicativeInverse(BigInteger a, BigInteger b) {
        
        // Stores the biggest number out of a and b
        BigInteger initial;
        if (a.compareTo(b) > 0) {
            initial = a;
        }

        else {
            initial = b;
        }
        
        // Initializes the t1, t2, t 
        BigInteger t1 = BigInteger.ZERO;
        BigInteger t2 = BigInteger.ONE;
        BigInteger t = BigInteger.ONE;

        // Performs the extended Euclidean algorithm
        while (!(a.mod(b).equals(BigInteger.ZERO))) {
            
            // finds the quotient
            BigInteger q = a.divide(b);

            // Same process as normal Euclidean algorithm
            BigInteger temp = b;
            b = a.mod(b);
            a = temp;

            // Finds t by t1 - t2(a/b)
            t = t1.subtract(q.multiply(t2));
            t1 = t2;
            t2 = t; 
        }

        // Make t positive
        if (t.compareTo(BigInteger.ZERO) < 0)
            t = t.add(initial);

        // Multiplicative Inverse is returned through t
        return t;
    }
}
