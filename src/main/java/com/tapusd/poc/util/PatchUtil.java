package com.tapusd.poc.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.Assert;

import java.io.IOException;

public class PatchUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private PatchUtil() {}

    // Can't handle nested array or collection modification
    // It will merge and keep both old records and new records in the array
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
