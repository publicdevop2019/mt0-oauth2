package com.hw.utility;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Map;

public class ServiceUtility {
    private static ObjectMapper mapper = new ObjectMapper();
    private static String USER_NAME = "uid";

    public static String getUsername(String bearerHeader) {
        String replace = bearerHeader.replace("Bearer ", "");
        String jwtBody;
        try {
            jwtBody = replace.split("\\.")[1];
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new IllegalArgumentException("malformed jwt token");
        }
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decode = decoder.decode(jwtBody);
        String s = new String(decode);
        System.out.println(s);
        try {
            Map<String, Object> var0 = mapper.readValue(s, new TypeReference<Map<String, Object>>() {
            });
            return (String) var0.get(USER_NAME);
        } catch (IOException e) {
            throw new IllegalArgumentException("unable to find user_name in authorization header");
        }
    }

    public static String getServerTimeStamp() {
        return OffsetDateTime.now(ZoneOffset.UTC).toString();
    }
}
