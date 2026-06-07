package net.lee.fnafmod;

import net.lee.fnafmod.client.ClientEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(fnafmod.MOD_ID)
public class fnafmod {
    public static final String MOD_ID = "fnafmod";

    public fnafmod(IEventBus modBus) {
        modBus.addListener(this::commonSetup);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientEvents.init();
        }
        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }
}
