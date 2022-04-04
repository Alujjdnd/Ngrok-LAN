package alujjdnd.ngrok.lan.mixin;


import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.github.alexdlaird.ngrok.NgrokClient;

@Mixin(MinecraftServer.class)
public abstract class CloseTunnelMixin {

    // Not yet working, this is constructing a new object instead of referencing the one in OpenToLanScreenMixin
    @Inject(at = @At("TAIL"), method = "shutdown")
    private void afterShutdownServer(CallbackInfo info) {
        final NgrokClient ngrokClient = new NgrokClient.Builder()
                .build();


        ngrokClient.kill();
    }

}
