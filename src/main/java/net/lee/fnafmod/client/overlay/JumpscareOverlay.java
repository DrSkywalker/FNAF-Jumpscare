package net.lee.fnafmod.client.overlay;

import net.lee.fnafmod.client.JumpscareManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.LayeredDraw;

public class JumpscareOverlay implements LayeredDraw.Layer {
    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        JumpscareManager.get().render(graphics);
    }
}
