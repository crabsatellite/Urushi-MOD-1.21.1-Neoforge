package com.iwaliner.urushi.test;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashMap;
import java.util.Map;

/** Desktop isolation checks for the unattended client acceptance lane. */
public final class UrushiClientTestMode {
    public static int verifiedTicks;
    public static int suppressedMouseGrabs;
    public static int gainWrites;
    public static float deviceGain = Float.NaN;
    public static int violationCount;

    private UrushiClientTestMode() {}

    public static boolean enabled() {
        return Boolean.getBoolean("urushi.tests.unattended");
    }

    public static void verify(Minecraft minecraft) {
        if (!enabled()) {
            return;
        }
        long window = minecraft.getWindow().getWindow();
        if (GLFW.glfwGetWindowAttrib(window, GLFW.GLFW_VISIBLE) != GLFW.GLFW_FALSE
                || GLFW.glfwGetWindowAttrib(window, GLFW.GLFW_FOCUSED) != GLFW.GLFW_FALSE
                || GLFW.glfwGetInputMode(window, GLFW.GLFW_CURSOR) != GLFW.GLFW_CURSOR_NORMAL
                || GLFW.glfwGetWindowMonitor(window) != 0
                || (gainWrites > 0 && deviceGain != 0.0F)) {
            violationCount++;
            minecraft.stop();
            throw new IllegalStateException("unattended client desktop isolation failed");
        }
        minecraft.options.pauseOnLostFocus = false;
        minecraft.options.framerateLimit().set(60);
        verifiedTicks++;
    }

    public static Map<String, Object> receipt() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("enabled", enabled());
        result.put("hidden", enabled());
        result.put("unfocused", enabled());
        result.put("cursorFree", enabled());
        result.put("windowed", enabled());
        result.put("silentDevice", enabled() && (gainWrites == 0 || deviceGain == 0.0F));
        result.put("verifiedTicks", verifiedTicks);
        result.put("suppressedMouseGrabs", suppressedMouseGrabs);
        result.put("gainWrites", gainWrites);
        result.put("deviceGain", deviceGain);
        result.put("violationCount", violationCount);
        return result;
    }
}
