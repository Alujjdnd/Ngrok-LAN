package alujjdnd.ngrok.lan.mixin;

import alujjdnd.ngrok.lan.NgrokLan;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class CloseTunnelMixin {
    @Inject(at = @At("TAIL"), method = "shutdown")
    private void afterShutdownServer(CallbackInfo info) {
        if(NgrokLan.serverOpen) {
            NgrokLan.LOGGER.info("Closing Ngrok LAN!");
            NgrokLan.ngrokClient.kill();
            NgrokLan.serverOpen = false;
        }
    }
}
