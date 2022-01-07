package org.minefortress.mixins.renderer;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)
public abstract class FortressCreateWorldScreenMixin extends Screen {

    @Shadow
    private CyclingButtonWidget<Boolean> enableCheatsButton;

    @Shadow private CyclingButtonWidget<Difficulty> difficultyButton;

    protected FortressCreateWorldScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "tweakDefaultsTo", at = @At("TAIL"))
    private void tweakDefaultsTo(CreateWorldScreen.Mode mode, CallbackInfo ci) {
        if(mode == CreateWorldScreen.Mode.DEBUG) {
            enableCheatsButton.setValue(true);
            difficultyButton.setValue(Difficulty.NORMAL);
        }
    }

}