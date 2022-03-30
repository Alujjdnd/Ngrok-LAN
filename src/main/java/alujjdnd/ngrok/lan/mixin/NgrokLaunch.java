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

    // Reads the NLangConfig (ClothConfig) fields in the ModMenu, stores it under "config.<something>"
    NLanConfig config = AutoConfig.getConfigHolder(NLanConfig.class).getConfig();

    //MC Client Configuration, for printing in chat
    MinecraftClient mc = MinecraftClient.getInstance();

    private void ngrokInit(int port, Region region){

        // Check if mod is enabled in the ModMenu
        if (config.enabledCheckBox == true) {
            if (config.authToken == "AuthToken") {
                // Check if authToken field has actually been changed, if not, print this text in chat
                mc.inGameHud.getChatHud().addMessage(new LiteralText("\u00a7cPlease set your Ngrok AuthToken! Do this in your menu > Mods > Ngrok LAN > Sliders Icon > Auth Token"));
            } else {
                try {
                    NgrokLan.LOGGER.info("Launched Lan!");

                    // Java-ngrok wrapper code, to initiate the tunnel, with the authoken, region
                    final JavaNgrokConfig javaNgrokConfig = new JavaNgrokConfig.Builder()
                            .withAuthToken(config.authToken)
                            .withRegion(region)
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

                    // Print in chat the status of the tunnel, and the details copied to the clipboard
                    mc.inGameHud.getChatHud().addMessage(new LiteralText("\u00a7aNgrok Service Initiated Successfully!"));
                    mc.inGameHud.getChatHud().addMessage(new LiteralText("Your server IP is - \u00a7e" + ngrok_url + "\u00a7f (Copied to Clipboard)"));
                    mc.keyboard.setClipboard(ngrok_url);
                } catch (Exception e) {
                    e.printStackTrace();

                    // Notify user of unsuccessful tunnel initiations
                    mc.inGameHud.getChatHud().addMessage(new LiteralText("\u00a7cNgrok Service Initiation Failed!"));
                }
            }
        } else {
            // This is printed when the boolean from config "enabledCheckBox" is false
            mc.inGameHud.getChatHud().addMessage(new LiteralText("\u00a76Ngrok LAN Disabled."));
        }
    }

    @Inject(method = "openToLan", at = @At("RETURN"))
    private void onOpenToLan(GameMode gameMode, boolean cheatsAllowed, int port, CallbackInfoReturnable<Boolean> cir)
    {

        // Switch statement for selecting the region, reading off of the config.regionselect enum
        switch (config.regionSelect) {
            case US:
                ngrokInit(port, Region.US);
                break;
            case EU:
                ngrokInit(port, Region.EU);
                break;
            case AP:
                ngrokInit(port, Region.AP);
                break;
            case AU:
                ngrokInit(port, Region.AU);
                break;
            case SA:
                ngrokInit(port, Region.SA);
                break;
            case JP:
                ngrokInit(port, Region.JP);
                break;
            case IN:
                ngrokInit(port, Region.IN);
                break;
        }



    }
}


