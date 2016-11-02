package com.linkx.wallpaper.data.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkx.wallpaper.data.SerializerProvider;
import java.io.IOException;
import java.util.List;

public class Model {
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return SerializerProvider.getInstance().readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> listFromJson(String json, Class<T> clazz) {
        try {
            ObjectMapper mapper = SerializerProvider.getInstance();
            JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
            return SerializerProvider.getInstance().readValue(json, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String toJson() {
        try {
            return SerializerProvider.getInstance().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String identity() throws MethodNotOverrideException {
        throw new MethodNotOverrideException("child:" + this.getClass().getName() + " must override this");
    }

    public static class MethodNotOverrideException extends Exception {
        public MethodNotOverrideException(String detailMessage) {
            super(detailMessage);
        }
    }
}
