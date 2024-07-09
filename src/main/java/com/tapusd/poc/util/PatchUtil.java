package com.tapusd.poc.util;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class PatchUtil {
    private static final ObjectMapper OBJECT_MAPPER;

    static {
        // disabling the collection type merging strategy
        // object mapper will now only replace array type installed of merging during patch
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configOverride(ArrayNode.class)
                .setMergeable(false);

        // configuring fasterxml to replace empty string with null
        SimpleModule module = new SimpleModule();
        module.addDeserializer(String.class, new StdDeserializer<String>(String.class) {
            @Override
            public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
                String result = StringDeserializer.instance.deserialize(p, ctxt);
                if (!StringUtils.hasText(result)) {
                    return null;
                }
                return result;
            }
        });

        objectMapper.registerModule(module);

        OBJECT_MAPPER = objectMapper;
    }

    private PatchUtil() {}

    public static <E> E applyPatch(E entity, JsonNode patchRequest) {
        JsonNode idNode = patchRequest.get("id");
        Assert.isNull(idNode, "Patch request body should not contains id!");

        try {
            JsonNode entityNode = OBJECT_MAPPER.convertValue(entity, JsonNode.class);
            JsonNode updatedJsonNode = OBJECT_MAPPER.readerForUpdating(entityNode).readValue(patchRequest);
            return (E) OBJECT_MAPPER.treeToValue(updatedJsonNode, entity.getClass());
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to apply patch", ex);
        }
    }
}
