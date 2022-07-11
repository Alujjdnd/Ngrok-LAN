package alujjdnd.ngrok.lan.mixin;

import alujjdnd.ngrok.lan.NgrokLan;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class WindowTitleMixin {
    @Inject(method = "getWindowTitle", at = @At("HEAD"), cancellable = true)
    private void injected(CallbackInfoReturnable<String> cir) {
        StringBuilder stringBuilder2 = new StringBuilder(I18n.translate("text.title.ngroklan.window"));

        if(NgrokLan.serverOpen)
            cir.setReturnValue(stringBuilder2.toString());
    }
}
