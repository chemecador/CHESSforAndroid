package servidor;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Librería Cifrado. Contiene 2 métodos fundamentales: Cifrar y Descifrar.
 */
public class Cifrado {
    // atributos de la clase Cifrado, nos sirven para cifrar y descifrar con la
    // misma clave
    private KeyGenerator keyGenerator;
    private Key key;
    private Cipher aes;

    public Cifrado() {
        // Generamos una clave de 128 bits adecuada para AES
        keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            key = keyGenerator.generateKey();

            // Alternativamente, una clave que queramos que tenga al menos 16 bytes
            // y nos quedamos con los bytes 0 a 15
            key = new SecretKeySpec("una clave de 16 bytes".getBytes(), 0, 16, "AES");

            // Se obtiene un cifrador AES
            aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    /***
     * Recibe un mensaje por parámetro y lo cifra con el algoritmo AES,
     * codificándolo posteriormente en Base64
     * para su optimización y hacer legibles sus caracteres.
     * Esto permitirá añadirlo a una base de datos o trabajar con él más
     * cómodamente.
     * 
     * @param s Mensaje de tipo String que queremos cifrar
     * @return String con el mensaje cifrado
     */
    public String cifrar(String s) {

        try {
            // Se inicializa para encriptacion y se encripta el texto,
            // que debemos pasar como bytes.
            aes.init(Cipher.ENCRYPT_MODE, key);
            // Guardamos en un array de bytes lo que nos devuelve el método doFinal, que
            // también es un array de bytes.
            // Como parámetro le enviamos el mensaje que queremos cifrar, parseado a un
            // array de bytes.
            byte[] encriptado = aes.doFinal(s.getBytes());

            // Devolvemos el mensaje codificado en Base64 y lo convertido a string
            return Base64.getEncoder().encodeToString(encriptado);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * Recibe un mensaje cifrado por parámetro y lo descifra con el algoritmo AES.
     * Pre: El mensaje recibido debe estar codificado en Base64.
     * 
     * @param s Mensaje de tipo String que queremos descifrar
     * @return String con el mensaje descifrado
     */
    public String descifrar(String s) {

        try {
            // Se iniciliza el cifrador para desencriptar, con la
            // misma clave y se desencripta
            aes.init(Cipher.DECRYPT_MODE, key);

            // Como el mensaje recibido está en Base64, el primer paso es descodificarlo
            // Una vez hecho, se lo enviamos como parámetro al método doFinal que nos
            // devuelve
            // el mensaje descifrado en un array de bytes
            byte[] desencriptado = aes.doFinal(Base64.getDecoder().decode(s.getBytes()));
            // Devolvemos el mensaje descifrado en un string
            return new String(desencriptado);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }
}