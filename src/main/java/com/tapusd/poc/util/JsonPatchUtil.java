package com.tapusd.poc.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 * This util utilize <b>com.github.java-json-tools</b> dependency to perform merge patch
 * which uses reflection and not thread safe as discussed in this github <a href="https://github.com/FasterXML/jackson-databind/issues/2969#issuecomment-1272273682">issue</a>
 */
public class JsonPatchUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private JsonPatchUtil() {}

    // Able to handle nested array or collection modification
    // It will replace old array or collection with new collection
    public static <E> E applyPatch(E entity, JsonNode patchRequest) {
        JsonNode idNode = patchRequest.get("id");
        Assert.isNull(idNode, "Patch request body should not contains id!");

        try {
            JsonNode entityNode = OBJECT_MAPPER.convertValue(entity, JsonNode.class);
            JsonMergePatch jsonMergePatch = OBJECT_MAPPER.readValue(patchRequest.toString(), JsonMergePatch.class);
            JsonNode updatedJsonNode = jsonMergePatch.apply(entityNode);
            return (E) OBJECT_MAPPER.treeToValue(updatedJsonNode, entity.getClass());
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to apply patch", ex);
        } catch (JsonPatchException e) {
            throw new RuntimeException(e);
        }
    }
}
