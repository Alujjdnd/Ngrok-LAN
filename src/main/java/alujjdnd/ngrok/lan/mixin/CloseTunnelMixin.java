package alujjdnd.ngrok.lan.mixin;


import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.conf.JavaNgrokConfig;
import com.github.alexdlaird.ngrok.installer.NgrokInstaller;
import com.github.alexdlaird.ngrok.protocol.CreateTunnel;
import com.github.alexdlaird.ngrok.protocol.Proto;
import com.github.alexdlaird.ngrok.protocol.Region;
import com.github.alexdlaird.ngrok.protocol.Tunnel;

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
