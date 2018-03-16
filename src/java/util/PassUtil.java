/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.security.SecureRandom;
import org.commonutils.util.SHA1Hash;

/**
 *
 * @author ushiho
 */
public class PassUtil {

    private SHA1Hash sHA1Hash;
    private static final SecureRandom random = new SecureRandom();

    public String passToSh1(String pass) {
        return sHA1Hash.getStringHash(pass);
    }

    public boolean testTwoPasswords(String pass1, String pass2) {
        return passToSh1(pass1).equals(passToSh1(pass2));
    }

    public String generatePassAndHash(int len, String dic) {
        String result = generate(len, dic);
        return passToSh1(result);
    }

    public String generate(int len, String dic) {
        String result = "";
        for (int i = 0; i < len; i++) {
            int index = random.nextInt(dic.length());
            result += dic.charAt(index);
        }
        return result;
    }
}
