package servidor;

import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/***
 * Librería Hash. Contiene 2 métodos fundamentales: Hashear y Verificar el Hash.
 */
public class Hash {

    /***
     * Método que hashea utilizando el algoritmo SHA-512 el string que recibe como parámetro.
     * @param s Texto a hashear
     * @return Texto hasheado
     */
    public static String hashear(String s) {
        //Se crea el objeto MessageDigest
        MessageDigest md = null;
        //SHA-512
        try {
            md = MessageDigest.getInstance("SHA-512");
            md.update(s.getBytes());
            //Se realiza el Hashing
            byte[] mb = md.digest();
            return new String(Hex.encodeHex(mb));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * Método que comprueba si el hasheo del texto s (segundo parámetro) es igual al hash (primer parámetro)
     * @param hash Supuesto hasheo de s
     * @param s Texto a hashear y verificar con hash
     * @return True (son iguales), False (no son iguales)
     */
    public static boolean verificarHash(String hash, String s) {
        String sHasheado = hashear(s);
        return sHasheado.equalsIgnoreCase(hash);
    }
}
