package com.hw.utility;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServiceUtility {
    private static ObjectMapper mapper = new ObjectMapper();
    private static String USER_ID = "uid";
    private static String AUTHORITIES = "authorities";

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
        try {
            Map<String, Object> var0 = mapper.readValue(s, new TypeReference<Map<String, Object>>() {
            });
            return (String) var0.get(USER_ID);
        } catch (IOException e) {
            throw new IllegalArgumentException("unable to find uid in authorization header");
        }
    }

    public static List<String> getAuthority(String bearerHeader) {
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
            return (List<String>) var0.get(AUTHORITIES);
        } catch (IOException e) {
            throw new IllegalArgumentException("unable to find authorities in authorization header");
        }
    }

    public static Authentication getAuthentication(String bearerHeader) {
        try {
            Collection<? extends GrantedAuthority> au=getAuthority(bearerHeader).stream().map(e -> new GrantedAuthority() {
                @Override
                public String getAuthority() {
                    return e;
                }
            }).collect(Collectors.toList());
            String username = getUsername(bearerHeader);
            Authentication authentication = new Authentication() {
                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                   return au;
                }

                @Override
                public Object getCredentials() {
                    return null;
                }

                @Override
                public Object getDetails() {
                    return null;
                }

                @Override
                public Object getPrincipal() {
                    /**required for authorization code flow*/
                    return username;
                }

                @Override
                public boolean isAuthenticated() {
                    return false;
                }

                @Override
                public void setAuthenticated(boolean b) throws IllegalArgumentException {

                }

                @Override
                public String getName() {
                    return null;
                }
            };
            return authentication;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("unable to create authentication obj in authorization header");
        }
    }

    public static String getServerTimeStamp() {
        return OffsetDateTime.now(ZoneOffset.UTC).toString();
    }
}
