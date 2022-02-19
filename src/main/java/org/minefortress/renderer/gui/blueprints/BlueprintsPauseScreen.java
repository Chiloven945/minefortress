package org.minefortress.renderer.gui.blueprints;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.minefortress.network.ServerboundFinishEditBlueprintPacket;
import org.minefortress.network.helpers.FortressChannelNames;
import org.minefortress.network.helpers.FortressClientNetworkHelper;

@Environment(value= EnvType.CLIENT)
public class BlueprintsPauseScreen extends Screen {

    private final boolean showMenu;

    public BlueprintsPauseScreen(boolean showMenu) {
        super(new LiteralText("Edit Blueprint"));
        this.showMenu = showMenu;
    }

    @Override
    protected void init() {
        if (this.showMenu) {
            this.initWidgets();
        }
    }

    private void initWidgets() {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 24 - 16, 204, 20, new LiteralText("Back to game"), button -> {
            closeMenu();
        }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 48 - 16, 204, 20, new LiteralText("Save blueprint"), button -> {
            sendSave(true);
            closeMenu();
        }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 72 - 16, 204, 20, new LiteralText("Discard Blueprint"), button -> {
            sendSave(false);
            closeMenu();
        }));

    }

    private void sendSave(boolean shouldSave) {
        final ServerboundFinishEditBlueprintPacket packet = new ServerboundFinishEditBlueprintPacket(shouldSave);
        FortressClientNetworkHelper.send(FortressChannelNames.FORTRESS_SAVE_EDIT_BLUEPRINT, packet);
    }

    private void closeMenu() {
        this.client.setScreen(null);
        this.client.mouse.lockCursor();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.showMenu) {
            this.renderBackground(matrices);
            GameMenuScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 40, 0xFFFFFF);
        } else {
            GameMenuScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

}
