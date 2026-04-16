package com.ionhex975.vulkanpostfx.client.shader.uniform;

public final class VpfxBuiltinUniformSourceInjector {
    private VpfxBuiltinUniformSourceInjector() {
    }

    public static String inject(String shaderSource) {
        if (shaderSource == null || shaderSource.isBlank()) {
            return shaderSource;
        }

        String header = VpfxBuiltinUniformRegistry.buildShaderDeclarationHeader();

        String[] lines = shaderSource.split("\\R", -1);
        if (lines.length == 0) {
            return header + shaderSource;
        }

        StringBuilder out = new StringBuilder();
        boolean injected = false;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            out.append(line);

            if (i < lines.length - 1) {
                out.append('\n');
            }

            if (!injected && line.strip().startsWith("#version")) {
                out.append(header);
                injected = true;
            }
        }

        if (!injected) {
            return header + shaderSource;
        }

        return out.toString();
    }
}