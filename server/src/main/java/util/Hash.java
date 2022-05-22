package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

/**
 * Libreria Hash. Contiene 2 metodos fundamentales: uno hashear y otro para verificar el hash.
 */
public class Hash {

    /**
     * Genera un hash utilizando el algoritmo SHA-512 el string que recibe como
     * parametro.
     * 
     * @param s Texto a hashear
     * 
     * @return Texto hasheado
     * @throws NoSuchAlgorithmException Excepcion que se lanza si no es posible utilizar SHA-512
     */
    public static String hashear(String s) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(s.getBytes());
        byte[] mb = md.digest();
        return new String(Hex.encodeHex(mb));
    }

    /**
     * Metodo que comprueba si el hash del texto s (segundo parametro) es igual al
     * hash (primer parametro)
     * 
     * @param hash Supuesto hash de s
     * @param s    Texto a hashear y verificar con hash
     * 
     * @return True (son iguales), False (no son iguales)
     * @throws NoSuchAlgorithmException Excepcion que se lanza si no es posible utilizar SHA-512
     */
    public static boolean verificarHash(String hash, String s) throws NoSuchAlgorithmException {
        String sHasheado = hashear(s);
        return sHasheado.equalsIgnoreCase(hash);
    }
}
