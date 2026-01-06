package common;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Bezpieczenstwo {
    private static final int ITERACJE = 65536;
    private static final int DLUGOSC_KLUCZA = 256;

    // Metoda tworząca hash z hasła
    public static String hashujHaslo(String haslo) {
        try {
            byte[] sol = new byte[16];
            new SecureRandom().nextBytes(sol); // Generowanie losowej soli

            byte[] hash = obliczHash(haslo.toCharArray(), sol);

            // Zapisujemy jako: sol:hash w formacie Base64
            return Base64.getEncoder().encodeToString(sol) + ":" +
                    Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Błąd hashowania", e);
        }
    }

    // Metoda sprawdzająca, czy hasło pasuje do hasha
    public static boolean sprawdzHaslo(String hasloPodane, String hashZBiblioteki) {
        try {
            String[] czesci = hashZBiblioteki.split(":");
            byte[] sol = Base64.getDecoder().decode(czesci[0]);
            byte[] hashOczekiwany = Base64.getDecoder().decode(czesci[1]);

            byte[] hashPodany = obliczHash(hasloPodane.toCharArray(), sol);

            // Porównanie bit po bicie
            int diff = hashOczekiwany.length ^ hashPodany.length;
            for (int i = 0; i < hashOczekiwany.length && i < hashPodany.length; i++) {
                diff |= hashOczekiwany[i] ^ hashPodany[i];
            }
            return diff == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] obliczHash(char[] haslo, byte[] sol) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(haslo, sol, ITERACJE, DLUGOSC_KLUCZA);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return skf.generateSecret(spec).getEncoded();
    }
}