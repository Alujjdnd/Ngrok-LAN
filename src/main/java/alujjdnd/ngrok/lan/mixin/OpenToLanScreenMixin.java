package alujjdnd.ngrok.lan.mixin;


import alujjdnd.ngrok.lan.NgrokLan;
import alujjdnd.ngrok.lan.config.NLanConfig;
import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.conf.JavaNgrokConfig;
import com.github.alexdlaird.ngrok.protocol.CreateTunnel;
import com.github.alexdlaird.ngrok.protocol.Proto;
import com.github.alexdlaird.ngrok.protocol.Region;
import com.github.alexdlaird.ngrok.protocol.Tunnel;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.OpenToLanScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OpenToLanScreen.class)
public class OpenToLanScreenMixin extends Screen {

    NLanConfig config = AutoConfig.getConfigHolder(NLanConfig.class).getConfig();
    MinecraftClient mc = MinecraftClient.getInstance();

    @Shadow
    private GameMode gameMode;

    @Shadow
    private boolean allowCommands;

    protected OpenToLanScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void initWidgets(CallbackInfo info) {

        if (config.enabledCheckBox) { //if mod enabled in mod menu
            this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, this.height - 58, 150, 20, new TranslatableText("text.UI.ngroklan.LanButton"), (button) -> {
                int localPort = NetworkUtils.findLocalPort(); // part of the minecraft Networkutils class, finds an available local port (this was from the openToLan class)
                this.client.setScreen(null); // Removed all elements from the screen (this closes all menu windows)
                switch (config.regionSelect) {
                    case EU -> ngrokInit(localPort, Region.EU);
                    case AP -> ngrokInit(localPort, Region.AP);
                    case AU -> ngrokInit(localPort, Region.AU);
                    case SA -> ngrokInit(localPort, Region.SA);
                    case JP -> ngrokInit(localPort, Region.JP);
                    case IN -> ngrokInit(localPort, Region.IN);
                    default -> ngrokInit(localPort, Region.US); //US bundled here
                }
            }));

        }
    }


    private void ngrokInit(int port, Region region) {

        //Defines a new threaded function to oepn the Ngrok tunnel, so that the "Open to LAN" button does not hitch - this thread runs in a seperate process from the main game loop
        Thread thread = new Thread(() ->
        {
            if (config.authToken.equals("AuthToken")) {
                // Check if authToken field has actually been changed, if not, print this text in chat
                mc.inGameHud.getChatHud().addMessage(new TranslatableText("text.error.ngroklan.AuthTokenError"));
                mc.inGameHud.getChatHud().addMessage(new TranslatableText("text.error.ngroklan.AuthTokenError").formatted(Formatting.RED));
                //\u00a7c
            } else {
                try {
                    NgrokLan.LOGGER.info("Launched Lan!");

                    mc.inGameHud.getChatHud().addMessage(new TranslatableText("text.info.ngroklan.startMessage").formatted(Formatting.YELLOW));


                    // Java-ngrok wrapper code, to initiate the tunnel, with the authoken, region
                    final JavaNgrokConfig javaNgrokConfig = new JavaNgrokConfig.Builder()
                            .withAuthToken(config.authToken)
                            .withRegion(region)
                            .build();

                    NgrokLan.ngrokClient = new NgrokClient.Builder()
                            .withJavaNgrokConfig(javaNgrokConfig)
                            .build();

                    final CreateTunnel createTunnel = new CreateTunnel.Builder()
                            .withProto(Proto.TCP)
                            .withAddr(port)
                            .build();

                    final Tunnel tunnel = NgrokLan.ngrokClient.connect(createTunnel);

                    NgrokLan.LOGGER.info(tunnel.getPublicUrl());

                    var ngrok_url = tunnel.getPublicUrl().substring(6);

                    // Print in chat the status of the tunnel, and the details copied to the clipboard
                    mc.inGameHud.getChatHud().addMessage(new TranslatableText("text.info.ngroklan.success").formatted(Formatting.GREEN));

                    Text copyText = Texts.bracketed((new LiteralText(ngrok_url)).styled((style) -> style.withColor(Formatting.YELLOW).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, ngrok_url)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("chat.copy.click"))).withInsertion(ngrok_url)));
                    mc.inGameHud.getChatHud().addMessage( new TranslatableText("text.info.ngroklan.ip", copyText));

                    mc.keyboard.setClipboard(ngrok_url);



                    // This starts the LAN server and greys out the open to lan button
                    TranslatableText text;


                    if (this.client.getServer().openToLan(this.gameMode, this.allowCommands, port)) {
                        mc.getServer().setOnlineMode(config.onlineCheckBox);
                        text = new TranslatableText("commands.publish.started", port);
                        NgrokLan.serverOpen = true;

                        //TODO: make sure this works, I make a new thread that reads the json files to update the oplist and whitelist in the playermanager
                        Thread thread2 = new Thread(() -> {
                            boolean result = loadJson();
                            if(!result){
                                Text commandText = Texts.bracketed((new TranslatableText("text.info.ngroklan.reload.prompt")).styled((style) -> style.withColor(Formatting.YELLOW).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reloadngroklanlists")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("text.info.ngroklan.reload.prompt")))));
                                mc.inGameHud.getChatHud().addMessage( new TranslatableText("text.info.ngroklan.reload.message"));
                                mc.inGameHud.getChatHud().addMessage(commandText);
                            }
                        });

                        thread2.start();

                    } else {
                        text = new TranslatableText("commands.publish.failed");
                        NgrokLan.serverOpen = false;
                    }
                    this.client.inGameHud.getChatHud().addMessage(text);
                    this.client.updateWindowTitle();

                } catch (Exception error) {
                    error.printStackTrace();
                    mc.inGameHud.getChatHud().addMessage(new LiteralText(error.getMessage()));
                    mc.inGameHud.getChatHud().addMessage(new TranslatableText("text.error.ngroklan.fail").formatted(Formatting.RED));
                    //ngrokInitiated = false;
                    throw new RuntimeException("Ngrok Service Failed to Start" + error.getMessage());
                }
            }
        });

        // This starts the thread defined above
        thread.start();

    }

    private boolean loadJson(){
        int i;
        boolean bl3 = false;

        for(i = 0; !bl3 && i <= 2; ++i) {
            if (i > 0) {
                NgrokLan.LOGGER.warn("Encountered a problem while converting the op list, retrying in a few seconds");
                this.sleepFiveSeconds();
            }

            bl3 = ServerConfigHandler.convertOperators(this.client.getServer());
            //fail is false
        }

        boolean bl4 = false;

        for(i = 0; !bl4 && i <= 2; ++i) {
            if (i > 0) {
                NgrokLan.LOGGER.warn("Encountered a problem while converting the whitelist, retrying in a few seconds");
                this.sleepFiveSeconds();
            }

            bl4 = ServerConfigHandler.convertWhitelist(this.client.getServer());
            //fail is false
        }

        return bl3 && bl4;
    }

    private void sleepFiveSeconds() {
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException var2) {
        }
    }

}
