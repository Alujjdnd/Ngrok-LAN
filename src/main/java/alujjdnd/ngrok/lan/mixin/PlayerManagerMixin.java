package alujjdnd.ngrok.lan.mixin;

import alujjdnd.ngrok.lan.NgrokLan;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.Whitelist;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.SocketAddress;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Final
    @Shadow
    private Whitelist whitelist;

    @Inject(at = @At("HEAD"), method = "checkCanJoin")
    public void isSpawnProtected(SocketAddress address, GameProfile profile, CallbackInfoReturnable<Text> cir) {
        NgrokLan.LOGGER.info("Whitelist? " + whitelist.getFile().getAbsolutePath());
    }
}
