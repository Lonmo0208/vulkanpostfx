package com.ionhex975.vulkanpostfx.client.runtime.texture;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ionhex975.vulkanpostfx.client.pack.vpfx.VpfxTextureFilter;
import com.ionhex975.vulkanpostfx.client.pack.vpfx.VpfxTextureWrap;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class VpfxRuntimeTextureManifestReader {
    private VpfxRuntimeTextureManifestReader() {
    }

    public static VpfxRuntimeTextureManifest read(Path manifestPath) throws IOException {
        try (Reader reader = Files.newBufferedReader(manifestPath, StandardCharsets.UTF_8)) {
            JsonElement element = JsonParser.parseReader(reader);
            if (!element.isJsonObject()) {
                throw new IOException("Runtime texture manifest root must be an object: " + manifestPath);
            }

            JsonObject root = element.getAsJsonObject();
            String runtimeNamespace = getRequiredString(root, "runtime_namespace", manifestPath);

            JsonObject texturesObject = getRequiredObject(root, "textures", manifestPath);
            Map<String, VpfxRuntimeTextureDescriptor> textures = new LinkedHashMap<>();

            for (Map.Entry<String, JsonElement> entry : texturesObject.entrySet()) {
                String logicalName = entry.getKey();
                if (!entry.getValue().isJsonObject()) {
                    throw new IOException("Runtime texture entry must be an object: " + logicalName);
                }

                JsonObject obj = entry.getValue().getAsJsonObject();
                String relativePath = getRequiredString(obj, "relative_path", manifestPath);
                String resourceId = getRequiredString(obj, "resource_id", manifestPath);
                String filterRaw = getRequiredString(obj, "filter", manifestPath);
                String wrapRaw = getRequiredString(obj, "wrap", manifestPath);

                textures.put(logicalName, new VpfxRuntimeTextureDescriptor(
                        logicalName,
                        relativePath,
                        resourceId,
                        VpfxTextureFilter.fromJson(filterRaw),
                        VpfxTextureWrap.fromJson(wrapRaw)
                ));
            }

            return new VpfxRuntimeTextureManifest(runtimeNamespace, textures);
        }
    }

    private static JsonObject getRequiredObject(JsonObject parent, String key, Path path) throws IOException {
        if (!parent.has(key) || !parent.get(key).isJsonObject()) {
            throw new IOException("Missing required object field '" + key + "' in " + path);
        }
        return parent.getAsJsonObject(key);
    }

    private static String getRequiredString(JsonObject parent, String key, Path path) throws IOException {
        if (!parent.has(key) || !parent.get(key).isJsonPrimitive()
                || !parent.get(key).getAsJsonPrimitive().isString()) {
            throw new IOException("Missing required string field '" + key + "' in " + path);
        }
        return parent.get(key).getAsString();
    }
}