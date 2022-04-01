package alujjdnd.ngrok.lan.mixin;


import net.minecraft.client.gui.screen.OpenToLanScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OpenToLanScreen.class)
public class OpenToLanScreenMixin extends Screen {

    protected OpenToLanScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void initWidgets(CallbackInfo info) {
        if (true) { //TODO: check mod enabled
            this.addDrawableChild(new ButtonWidget(this.width / 2 + 104, this.height / 4 + 120 + -16, 20, 20, new TranslatableText("text.autoconfig.ngroklan.LanButton"), (button) -> {
                //current ngrok open to lan click
            }));
        }
    }
}
