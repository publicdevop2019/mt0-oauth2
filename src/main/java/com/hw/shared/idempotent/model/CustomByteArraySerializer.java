package com.hw.shared.idempotent.model;

import com.hw.shared.idempotent.exception.CustomByteArraySerializationException;

import java.io.*;

public class CustomByteArraySerializer {
    public static byte[] convertToDatabaseColumn(Object o) {
        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos)
        ) {
            oos.writeObject(o);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomByteArraySerializationException();
        }
    }

    public static Object convertToEntityAttribute(byte[] bytes) {
        try (ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(bytes))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new CustomByteArraySerializationException();
        }
    }

}
