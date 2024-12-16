package com.hbm.handler;

import com.hbm.capability.HbmCapability;
import com.hbm.capability.HbmCapability.IHBMData;
import com.hbm.lib.RefStrings;
import com.hbm.main.MainRegistry;
import com.hbm.packet.KeybindPacket;
import com.hbm.packet.PacketDispatcher;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = RefStrings.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class HbmKeybinds {
    public static final String CATEGORY = "key.categories.hbm";

    public static KeyMapping jetpackKey = new KeyMapping("key.hbm.toggleBack", GLFW.GLFW_KEY_C, CATEGORY);
    public static KeyMapping hudKey = new KeyMapping("key.hbm.toggleHUD", GLFW.GLFW_KEY_V, CATEGORY);
    public static KeyMapping reloadKey = new KeyMapping("key.hbm.reload", GLFW.GLFW_KEY_R, CATEGORY);

    public static KeyMapping craneUpKey = new KeyMapping("key.hbm.craneMoveUp", GLFW.GLFW_KEY_UP, CATEGORY);
    public static KeyMapping craneDownKey = new KeyMapping("key.hbm.craneMoveDown", GLFW.GLFW_KEY_DOWN, CATEGORY);
    public static KeyMapping craneLeftKey = new KeyMapping("key.hbm.craneMoveLeft", GLFW.GLFW_KEY_LEFT, CATEGORY);
    public static KeyMapping craneRightKey = new KeyMapping("key.hbm.craneMoveRight", GLFW.GLFW_KEY_RIGHT, CATEGORY);
    public static KeyMapping craneLoadKey = new KeyMapping("key.hbm.craneLoad", GLFW.GLFW_KEY_ENTER, CATEGORY);

    // Register key bindings
    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(jetpackKey);
        event.register(hudKey);
        event.register(reloadKey);
        event.register(craneUpKey);
        event.register(craneDownKey);
        event.register(craneLeftKey);
        event.register(craneRightKey);
        event.register(craneLoadKey);
    }

    // Handle key input
    @Mod.EventBusSubscriber(modid = RefStrings.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class KeyInputHandler {
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;

            IHBMData props = HbmCapability.getData(MainRegistry.proxy.me());
            if (props == null) return;

            for (EnumKeybind key : EnumKeybind.values()) {
                KeyMapping keyMapping = getKeyMapping(key);
                if (keyMapping == null) continue;

                boolean current = keyMapping.isDown();
                boolean last = props.getKeyPressed(key);

                if (last != current) {
                    PacketDispatcher.wrapper.sendToServer(new KeybindPacket(key, current));
                    props.setKeyPressed(key, current);
                }
            }
        }

        private static KeyMapping getKeyMapping(EnumKeybind key) {
            return switch (key) {
                case JETPACK, TOGGLE_JETPACK -> jetpackKey;
                case TOGGLE_HEAD -> hudKey;
                case RELOAD -> reloadKey;
                case CRANE_UP -> craneUpKey;
                case CRANE_DOWN -> craneDownKey;
                case CRANE_LEFT -> craneLeftKey;
                case CRANE_RIGHT -> craneRightKey;
                case CRANE_LOAD -> craneLoadKey;
            };
        }
    }

    public enum EnumKeybind {
        JETPACK,
        TOGGLE_JETPACK,
        TOGGLE_HEAD,
        RELOAD,
        CRANE_UP,
        CRANE_DOWN,
        CRANE_LEFT,
        CRANE_RIGHT,
        CRANE_LOAD
    }
}
