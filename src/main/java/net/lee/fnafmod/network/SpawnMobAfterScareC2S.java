package net.lee.fnafmod.network;

import net.lee.fnafmod.util.ArmorRandomizer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;

import net.lee.fnafmod.fnafmod;

public record SpawnMobAfterScareC2S(ResourceLocation mobId, String spawnName, int offX, int offY, int offZ,
                                    String[] armor) implements CustomPacketPayload {

    public static final Type<SpawnMobAfterScareC2S> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(fnafmod.MOD_ID, "spawn_mob_after_scare_c2s"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SpawnMobAfterScareC2S> STREAM_CODEC = StreamCodec.ofMember(SpawnMobAfterScareC2S::encode, SpawnMobAfterScareC2S::decode);

    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeResourceLocation(mobId);
        buf.writeBoolean(spawnName() != null);
        if (spawnName() != null) buf.writeUtf(spawnName());
        buf.writeInt(offX);
        buf.writeInt(offY);
        buf.writeInt(offZ);
        for (int i = 0; i < 4; i++) {
            String s = (armor != null && i < armor.length) ? armor[i] : null;
            buf.writeBoolean(s != null);
            if (s != null) buf.writeUtf(s);
        }
    }

    public static SpawnMobAfterScareC2S decode(RegistryFriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        String spawnName = buf.readBoolean() ? buf.readUtf(32767) : null;
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        String[] armor = new String[4];
        for (int i = 0; i < 4; i++) {
            boolean present = buf.readBoolean();
            armor[i] = present ? buf.readUtf(32767) : null;
        }
        return new SpawnMobAfterScareC2S(id, spawnName, x, y, z, armor);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @SuppressWarnings("resource")
    public static void handle(SpawnMobAfterScareC2S msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer sender = (ServerPlayer) ctx.player();
            if (sender == null) return;
            Level level = sender.level();
            var reg = level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE);
            reg.getOptional(msg.mobId).ifPresent(type -> {
                Entity e = type.create(level);
                if (e instanceof Mob mob) {
                    mob.moveTo(sender.getX() + msg.offX, sender.getY() + msg.offY, sender.getZ() + msg.offZ, sender.getYRot(), sender.getXRot());

                    // set custom name if provided
                    if (msg.spawnName() != null) {
                        mob.setCustomName(Component.literal(msg.spawnName()));
                        mob.setCustomNameVisible(true);
                    }

                    EquipmentSlot[] slots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
                    RandomSource rnd = mob.getRandom();

                    if (msg.armor == null) {
                        ArmorRandomizer.equipRandomArmor(mob, rnd, false);
                    } else {
                        for (int i = 0; i < 4; i++) {
                            String id = msg.armor.length > i ? msg.armor[i] : null;
                            if (id == null) continue;
                            if ("random".equalsIgnoreCase(id)) {
                                Item item = ArmorRandomizer.pickArmorForSlot(rnd, slots[i]);
                                if (item != Items.AIR) mob.setItemSlot(slots[i], new ItemStack(item));
                                continue;
                            }
                            var itemRegistry = level.registryAccess().registryOrThrow(Registries.ITEM);
                            Item item = itemRegistry.getOptional(ResourceLocation.parse(id)).orElse(Items.AIR);
                            if (item != Items.AIR) {
                                mob.setItemSlot(slots[i], new ItemStack(item));
                            }
                        }
                    }

                    level.addFreshEntity(mob);
                }
            });
        });
    }
}
