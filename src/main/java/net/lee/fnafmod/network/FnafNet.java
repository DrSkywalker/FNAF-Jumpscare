package net.lee.fnafmod.network;

import net.lee.fnafmod.fnafmod;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = fnafmod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class FnafNet {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                SpawnMobAfterScareC2S.TYPE,
                SpawnMobAfterScareC2S.STREAM_CODEC,
                SpawnMobAfterScareC2S::handle
        );
    }
}
