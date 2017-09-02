package com.oauth.services.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

/**
 * Created by yichen.wei on 6/24/17.
 */
public class Test {
    public static void main(String args[]) {
        BytesKeyGenerator saltGenerator = KeyGenerators.secureRandom();
//        StandardPasswordEncoder encode = new StandardPasswordEncoder("SHA-256", "");
//        StandardPasswordEncoder encode = new StandardPasswordEncoder("");
        StandardPasswordEncoder encode = new StandardPasswordEncoder();
        System.out.println("abcfwef...");
        //a8ba715d5a076c99b95995d357651df5c296bf308abaa154a54d2418885ec622e9fe8624f2e06524
        //be1e54adbd1c5c5d58a714fad7d529c73198c8c51e1f9d43edc79dac4784b5e93460605fe7082b0d
        //910a6df88a99d5d81f3376628f3fd6a91a2152a366f2d450ef9220ff32f0c74952f754da62cd5a13
        System.out.println(encode.encode("abcdef"));
//        System.out.println(encode.encode("mypass"));
        String salt = saltGenerator.generateKey().toString();
        System.out.println(salt);
        System.out.println(saltGenerator.getKeyLength());

        BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
        System.out.println(bc.encode("admin"));
    }
}
