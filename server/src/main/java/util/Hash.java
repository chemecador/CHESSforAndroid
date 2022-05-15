package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

/***
 * Librería Hash. Contiene 2 métodos fundamentales: Hashear y Verificar el Hash.
 */
public class Hash {

    /***
     * Genera un hash utilizando el algoritmo SHA-512 el string que recibe como
     * parámetro.
     * 
     * @param s Texto a hashear
     * 
     * @return Texto hasheado
     * @throws NoSuchAlgorithmException Si no es posible utilizar SHA-512
     */
    public static String hashear(String s) throws NoSuchAlgorithmException {
        MessageDigest md = null;

        md = MessageDigest.getInstance("SHA-512");
        md.update(s.getBytes());
        byte[] mb = md.digest();
        return new String(Hex.encodeHex(mb));
    }

    /***
     * Método que comprueba si el hash del texto s (segundo parámetro) es igual al
     * hash (primer parámetro)
     * 
     * @param hash Supuesto hash de s
     * @param s    Texto a hashear y verificar con hash
     * 
     * @return True (son iguales), False (no son iguales)
     * @throws NoSuchAlgorithmException Si no es posible utilizar SHA-512
     */
    public static boolean verificarHash(String hash, String s) throws NoSuchAlgorithmException {
        String sHasheado = hashear(s);
        return sHasheado.equalsIgnoreCase(hash);
    }
}
