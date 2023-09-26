package alujjdnd.ngrok.lan.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.network.LanServerPinger;
import net.minecraft.server.integrated.IntegratedServer;

@Mixin(IntegratedServer.class)
public interface IntegratedServerAccessor {
    @Accessor
    public void setLanPort(int lanPort);

    @Accessor
    public LanServerPinger getLanPinger();
}