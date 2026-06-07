package net.lee.fnafmod.client;

import net.lee.fnafmod.client.overlay.JumpscareOverlay;
import net.lee.fnafmod.fnafmod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = fnafmod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    public static void init() {
        NeoForge.EVENT_BUS.addListener(ClientEvents::onClientTickEnd);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, ClientEvents::onScreenRenderPost);
        NeoForge.EVENT_BUS.addListener(ClientEvents::onKeyInput);
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent e) {
        Keybinds.register(e);
    }

    @SubscribeEvent
    public static void onRegisterGuiOverlays(RegisterGuiLayersEvent e) {
        // Works in-world when no Screen is open
        e.registerAboveAll(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(fnafmod.MOD_ID, "jumpscare"), new JumpscareOverlay());
    }

    public static void onClientTickEnd(ClientTickEvent.Post e) {
        if (Keybinds.TEST_SCARE.consumeClick()) {
            JumpscareManager.get().triggerRandom();
        }
        JumpscareManager.get().tick();
    }

    public static void onKeyInput(InputEvent.Key e) {
        if (e.getAction() == GLFW.GLFW_PRESS
                && Keybinds.TEST_SCARE.matches(e.getKey(), e.getScanCode())
                && !JumpscareManager.get().isActive()) {
            JumpscareManager.get().triggerRandom();
        }
    }

    public static void onScreenRenderPost(ScreenEvent.Render.Post e) {
        JumpscareManager.get().render(e.getGuiGraphics());
    }
}