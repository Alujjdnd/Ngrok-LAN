package alujjdnd.ngrok.lan.mixin;

import alujjdnd.ngrok.lan.NgrokLan;
import alujjdnd.ngrok.lan.config.NLanConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public class SpawnProtectionMixin {

    NLanConfig config = AutoConfig.getConfigHolder(NLanConfig.class).getConfig();

    @Shadow
    private PlayerManager playerManager;

    @Inject(at = @At("HEAD"), method = "isSpawnProtected", cancellable = true)
    public void isSpawnProtected(ServerWorld world, BlockPos pos, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if(!NgrokLan.serverOpen){
            cir.setReturnValue(false);
        }
        else if (world.getRegistryKey() != World.OVERWORLD) {
            cir.setReturnValue(false);
        } else if (playerManager.isOperator(player.getGameProfile())) {
            cir.setReturnValue(false);
        } else if (config.spawnProtectionRadius <= 0) {
            cir.setReturnValue(false);
        } else {
            BlockPos blockPos = world.getSpawnPos();
            int i = MathHelper.abs(pos.getX() - blockPos.getX());
            int j = MathHelper.abs(pos.getZ() - blockPos.getZ());
            int k = Math.max(i, j);
            cir.setReturnValue(k <= config.spawnProtectionRadius);
        }
        //there's a small diff from vanilla check that checks if Oplist is empty. Since the host
        //is an "admin" (more control than op idk the technical term) by default they are not in
        //the op list, so this check does not work.
    }


}
