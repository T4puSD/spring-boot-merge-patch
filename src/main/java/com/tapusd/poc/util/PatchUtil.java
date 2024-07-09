package com.tapusd.poc.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.util.Assert;

import java.io.IOException;

public class PatchUtil {
    private static final ObjectMapper OBJECT_MAPPER;

    static {
        // disabling the collection type merging strategy
        // object mapper will now only replace array type installed of merging during patch
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configOverride(ArrayNode.class)
                .setMergeable(false);

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
