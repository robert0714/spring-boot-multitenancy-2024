package com.thomasvitale.instrumentservice.aop.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
    }

    public static String bean2Json(Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    public static String bean2Json(Object obj, FilterProvider filters) throws JsonProcessingException {
        return mapper.writer(filters).writeValueAsString(obj);
    }

    public static <T> T json2Bean(String jsonStr, Class<T> objClass) throws JsonProcessingException {
        return mapper.readValue(jsonStr, objClass);
    }

    public static <T> T json2Bean(String jsonStr, TypeReference<T> typeReference) throws JsonProcessingException {
        return mapper.readValue(jsonStr, typeReference);
    }

    public static JsonNode json2jsonNode(String jsonStr) throws JsonProcessingException {
        return mapper.readTree(jsonStr);
    }
}
