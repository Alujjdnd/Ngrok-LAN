package alujjdnd.ngrok.lan.mixin;


import alujjdnd.ngrok.lan.NgrokLan;
import alujjdnd.ngrok.lan.config.NLanConfig;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.terraformersmc.modmenu.mixin.AccessorGridWidget;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin extends Screen {

    NLanConfig config = AutoConfig.getConfigHolder(NLanConfig.class).getConfig();
    MinecraftClient mc = MinecraftClient.getInstance();

    protected GameMenuScreenMixin(Text title) {
        super(title);
    }


    @Inject(method = "initWidgets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/GridWidget;forEachChild(Ljava/util/function/Consumer;)V"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onInitWidgets(CallbackInfo ci, GridWidget gridWidget, GridWidget.Adder adder, Text text) {

        if (config.enabledCheckBox && NgrokLan.serverOpen) { //if ngrok server open
            if (gridWidget != null) {
                final List<Widget> buttons = ((AccessorGridWidget) gridWidget).getChildren();

                for (Widget widget : buttons) {
                    if (buttonHasText(widget, "menu.playerReporting")) { //find player report button
                        this.addDrawableChild(ButtonWidget.builder(Text.translatable("text.UI.ngroklan.closeServerButton"), (button) -> {
                            this.client.setScreen(null);
                            closeServer();
                        }).dimensions(widget.getX() + 100, widget.getY(), 80, widget.getHeight()).build());
                    }
                }
            }
        }
    }

    private boolean buttonHasText(Widget widget, String translationKey) {
        if (widget instanceof ButtonWidget button) {
            Text text = button.getMessage();
            TextContent textContent = text.getContent();
            return textContent instanceof TranslatableTextContent && ((TranslatableTextContent) textContent).getKey().equals(translationKey);
        }
        return false;
    }

    private void closeServer(){
        NgrokLan.LOGGER.info("Closing LAN server...");
        PlayerManager playerManager = mc.getServer().getPlayerManager();
        List<ServerPlayerEntity> list = Lists.newArrayList(playerManager.getPlayerList());

        for (ServerPlayerEntity serverPlayerEntity : list) {
            GameProfile profile = serverPlayerEntity.getGameProfile();
            if(!mc.getServer().isHost(profile)){
                serverPlayerEntity.networkHandler.disconnect(Text.translatable("multiplayer.disconnect.server_shutdown"));
            }
        }

        mc.getServer().getNetworkIo().stop(); //stop listening to port
        ((IntegratedServerAccessor) mc.getServer()).setLanPort(-1);
        ((IntegratedServerAccessor) mc.getServer()).getLanPinger().interrupt();

        //fix the window title
        MinecraftClient.getInstance().updateWindowTitle();

        if(NgrokLan.serverOpen){ //kill ngrok tunnel
            NgrokLan.LOGGER.info("Closing Ngrok LAN, but world is open");
            NgrokLan.ngrokClient.kill();
            NgrokLan.serverOpen = false;
        }
        mc.inGameHud.getChatHud().addMessage(Text.translatable("text.info.ngroklan.closeMessage").formatted(Formatting.YELLOW));
    }

}
