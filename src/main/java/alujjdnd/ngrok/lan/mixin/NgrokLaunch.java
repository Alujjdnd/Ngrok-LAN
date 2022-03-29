package alujjdnd.ngrok.lan.mixin;

import alujjdnd.ngrok.lan.NgrokLan;

// import net.minecraft.client.gui.hud.ChatHud;
// import net.minecraft.network.MessageType;

import alujjdnd.ngrok.lan.config.NLanConfig;
import me.shedaniel.autoconfig.AutoConfig;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.protocol.CreateTunnel;
import com.github.alexdlaird.ngrok.protocol.Proto;
import com.github.alexdlaird.ngrok.protocol.Tunnel;
import com.github.alexdlaird.ngrok.conf.JavaNgrokConfig;
import com.github.alexdlaird.ngrok.protocol.Region;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.GameMode;

import net.minecraft.text.LiteralText;

@Mixin(IntegratedServer.class)
public class NgrokLaunch {
	@Inject(method = "openToLan", at = @At("RETURN"))
    private void onOpenToLan(GameMode gameMode, boolean cheatsAllowed, int port, CallbackInfoReturnable<Boolean> cir)
    {

    try{    
        NgrokLan.LOGGER.info("Launched Lan!");

        NLanConfig config = AutoConfig.getConfigHolder(NLanConfig.class).getConfig();
        final JavaNgrokConfig javaNgrokConfig = new JavaNgrokConfig.Builder()
                    .withAuthToken(config.authToken)
                    .withRegion(Region.AP)
                    .build();
        
        final NgrokClient ngrokClient = new NgrokClient.Builder()
                    .withJavaNgrokConfig(javaNgrokConfig)
                    .build();

        final CreateTunnel createTunnel = new CreateTunnel.Builder()
                    .withProto(Proto.TCP)
                    .withAddr(port)
                    .build();

        final Tunnel tunnel = ngrokClient.connect(createTunnel);


        NgrokLan.LOGGER.info(tunnel.getPublicUrl());

        var ngrok_url = tunnel.getPublicUrl().substring(6);

        MinecraftClient mc = MinecraftClient.getInstance();
        mc.inGameHud.getChatHud().addMessage(new LiteralText("\u00a7aNgrok Service Initiated Successfully!"));
        mc.inGameHud.getChatHud().addMessage(new LiteralText("Your server IP is - \u00a7e" + ngrok_url + "\u00a7f (Copied to Clipboard)"));
        mc.keyboard.setClipboard(ngrok_url);
    }
    catch (Exception e)
    {
        e.printStackTrace();
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.inGameHud.getChatHud().addMessage(new LiteralText("\u00a7cNgrok Service Initiated Failed!"));
    }
    }
}


