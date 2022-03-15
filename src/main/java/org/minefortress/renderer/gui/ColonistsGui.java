package org.minefortress.renderer.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.minefortress.entity.Colonist;
import org.minefortress.fortress.FortressClientManager;
import org.minefortress.interfaces.FortressClientWorld;
import org.minefortress.interfaces.FortressMinecraftClient;

import java.util.Optional;

public class ColonistsGui extends FortressGuiScreen{

    private int colonistsCount = 0;
    private boolean hovered;
    protected ColonistsGui(MinecraftClient client, ItemRenderer itemRenderer) {
        super(client, itemRenderer);
    }

    @Override
    void tick() {
        final FortressClientManager fortressManager = getFortressClientManager();
        if(!fortressManager.isInitialized()) return;

        colonistsCount = fortressManager.getColonistsCount();
    }

    @Override
    void render(MatrixStack matrices, TextRenderer font, int screenWidth, int screenHeight, double mouseX, double mouseY, float delta) {
        final FortressClientManager fortressManager = getFortressClientManager();
        if(!fortressManager.isInitialized()) return;

        final boolean colonsitsCountHovered = renderColonistsCount(matrices, font, screenWidth, screenHeight, mouseX, mouseY);

        if(fortressManager.isSelectingColonist()){
            final Colonist selectedColonist = fortressManager.getSelectedColonist();

            final int colonistWinX = 0;
            final int colonistWinY = screenHeight - 85;
            final int width = 120;
            final int height = 85;
            DrawableHelper.fillGradient(matrices, colonistWinX, colonistWinY, colonistWinX + width, colonistWinY + height, 0xc0101010, 0xd0101010, 100);

            final String name = Optional.ofNullable(selectedColonist.getCustomName()).map(Text::asString).orElse("");
            final String healthString = String.format("%.0f/%.0f", selectedColonist.getHealth(), selectedColonist.getMaxHealth());
            final String hungerString = "10/10";
            final String professionString = "Miner - 1 LVL";
            final String tasksString = selectedColonist.getCurrentTaskDesc();

            Screen.drawCenteredText(matrices, font, name, colonistWinX + width / 2, colonistWinY + 5, 0xFFFFFF);

            int heartIconX = colonistWinX + 5;
            int heartIconY = colonistWinY + textRenderer.fontHeight + 10;
            renderIcon(matrices, heartIconX, heartIconY, 8, 8, 52, 0);
            textRenderer.draw(matrices, healthString, heartIconX + 10, heartIconY + 2, 0xFFFFFF);

            int hungerIconX = colonistWinX + width/2 + 5;
            int hungerIconY = heartIconY;
            renderIcon(matrices, hungerIconX, hungerIconY, 8, 8, 52, 28);
            textRenderer.draw(matrices, hungerString, hungerIconX + 10, hungerIconY + 2, 0xFFFFFF);

            textRenderer.draw(matrices, "Profession:", colonistWinX + 5, hungerIconY + textRenderer.fontHeight + 5, 0xFFFFFF);
            textRenderer.draw(matrices, professionString, colonistWinX + 5, hungerIconY + 2 * textRenderer.fontHeight + 5 , 0xFFFFFF);

            textRenderer.draw(matrices, "Task:", colonistWinX + 5, hungerIconY + 3 * textRenderer.fontHeight + 10, 0xFFFFFF);
            textRenderer.draw(matrices, tasksString, colonistWinX + 5, hungerIconY + 4 * textRenderer.fontHeight + 10, 0xFFFFFF);
        }

        this.hovered = colonsitsCountHovered;
    }

    private void renderIcon(MatrixStack matrices, int iconX, int iconY, int iconWidth, int iconHeight, int heartIconU, int heartIconV) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
        DrawableHelper.drawTexture(matrices, iconX, iconY, 110, heartIconU, heartIconV, iconWidth, iconHeight, 256, 256);
    }

    private boolean renderColonistsCount(MatrixStack p, TextRenderer font, int screenWidth, int screenHeight, double mouseX, double mouseY) {
        final String colonistsCountString = "x" + colonistsCount;

        final int iconX = screenWidth / 2 - 91;
        final int iconY = screenHeight - 40;
        final float textX = screenWidth / 2f - 91 + 15;
        final int textY = screenHeight - 35;

        final int boundRightX = (int)textX + font.getWidth(colonistsCountString);
        final int boundBottomY = iconY + 20;

        final boolean hovered = mouseX >= iconX && mouseX <= boundRightX && mouseY >= iconY && mouseY < boundBottomY;

        super.itemRenderer.renderGuiItemIcon(new ItemStack(Items.PLAYER_HEAD), iconX, iconY);

        font.draw(p, colonistsCountString, textX, textY, 0xFFFFFF);

        if(hovered) {
            super.renderTooltip(p, Text.of("Your Pawns count"), (int) mouseX, (int) mouseY);
        }

        return hovered;
    }

    private FortressClientManager getFortressClientManager() {
        final FortressMinecraftClient fortressClient = (FortressMinecraftClient) this.client;
        return fortressClient.getFortressClientManager();
    }

    @Override
    boolean isHovered() {
        return this.hovered;
    }
}
