package org.rmj.g3appdriver;

import static com.huawei.hms.feature.dynamic.b.s;

import org.junit.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TestHash {

    @Test
    public void Test(){

        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update("35606007228172220240827142536".getBytes());

            byte[] bytes = digest.digest();

            char[] hexArray = "0123456789ABCDEF".toCharArray();
            char[] hexChars = new char[bytes.length * 2];

            for ( int j = 0; j < bytes.length; j++ ) {

                int v = bytes[j] & 0xFF;

                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];

                System.out.println(v);
            }

        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

}
