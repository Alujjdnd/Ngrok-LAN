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
import net.minecraft.server.OperatorList;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.Whitelist;
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

    @Shadow
    private int port;

    protected OpenToLanScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void initWidgets(CallbackInfo info) {

        if (config.enabledCheckBox) { //if mod enabled in mod menu
            this.addDrawableChild(ButtonWidget.builder(Text.translatable("text.UI.ngroklan.LanButton"), (button) -> {
                //localport was removed since OpenToLanScreen.class contains a new port variable that is automatically updated, I am shadowing it
                this.client.setScreen(null); // Removed all elements from the screen (this closes all menu windows)
                switch (config.regionSelect) {
                    case EU -> ngrokInit(port, Region.EU);
                    case AP -> ngrokInit(port, Region.AP);
                    case AU -> ngrokInit(port, Region.AU);
                    case SA -> ngrokInit(port, Region.SA);
                    case JP -> ngrokInit(port, Region.JP);
                    case IN -> ngrokInit(port, Region.IN);
                    default -> ngrokInit(port, Region.US); //US bundled here
                }
            }).dimensions(this.width / 2 - 155, this.height - 52, 150, 20).build());
        }
    }


    private void ngrokInit(int port, Region region) {

        //Defines a new threaded function to oepn the Ngrok tunnel, so that the "Open to LAN" button does not hitch - this thread runs in a seperate process from the main game loop
        Thread thread = new Thread(() ->
        {
            if (config.authToken.equals("AuthToken")) {
                // Check if authToken field has actually been changed, if not, print this text in chat
                mc.inGameHud.getChatHud().addMessage(Text.translatable("text.error.ngroklan.AuthTokenError").formatted(Formatting.RED));
                //\u00a7c
                NgrokLan.LOGGER.error("Launched Lan UNSUCCESSFUL");
            } else {
                try {
                    NgrokLan.LOGGER.info("Launched Lan!");
                    mc.inGameHud.getChatHud().addMessage(Text.translatable("text.info.ngroklan.startMessage").formatted(Formatting.YELLOW));


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
                    mc.inGameHud.getChatHud().addMessage(Text.translatable("text.info.ngroklan.success").formatted(Formatting.GREEN));

                    Text copyText = Texts.bracketed((Text.translatable(ngrok_url)).styled((style) -> style.withColor(Formatting.YELLOW).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, ngrok_url)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("chat.copy.click"))).withInsertion(ngrok_url)));
                    mc.inGameHud.getChatHud().addMessage(Text.translatable("text.info.ngroklan.ip", copyText));

                    mc.keyboard.setClipboard(ngrok_url);



                    // This starts the LAN server and greys out the open to lan button
                    MutableText textStart;

                    if (this.client.getServer().openToLan(this.gameMode, this.allowCommands, port)) {
                        mc.getServer().setOnlineMode(config.onlineCheckBox);
                        textStart = Text.translatable("commands.publish.started", port);
                        NgrokLan.serverOpen = true;

                        //I made a new thread that reads the json files to update the oplist and whitelist in the playermanager
                        Thread thread2 = new Thread(() -> {


                            boolean loaded = loadJson();
                            if(loaded){
                                MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.translatable("text.info.ngroklan.reload.success").styled(style -> style.withColor(Formatting.GREEN) ));
                            }
                            else{
                                Text commandText = Texts.bracketed((Text.translatable("text.info.ngroklan.reload.prompt")).styled((style) -> style.withColor(Formatting.RED).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reloadngroklanlists")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("text.info.ngroklan.reload.prompt")))));
                                mc.inGameHud.getChatHud().addMessage(Text.translatable("text.info.ngroklan.reload.message"));
                                mc.inGameHud.getChatHud().addMessage(commandText);
                            }
                        });

                        thread2.start();

                    } else {
                        textStart = Text.translatable("commands.publish.failed");
                        NgrokLan.serverOpen = false;
                    }
                    this.client.inGameHud.getChatHud().addMessage(textStart);
                    this.client.updateWindowTitle();

                } catch (Exception error) {
                    error.printStackTrace();
                    mc.inGameHud.getChatHud().addMessage(Text.literal(error.getMessage()));
                    mc.inGameHud.getChatHud().addMessage(Text.translatable("text.error.ngroklan.fail").formatted(Formatting.RED));
                    //ngrokInitiated = false;
                    throw new RuntimeException("Ngrok Service Failed to Start" + error.getMessage());
                }
            }
        });

        // This starts the thread defined above
        thread.start();

    }

    private boolean loadJson(){
        PlayerManager playerManager = this.client.getServer().getPlayerManager();

        Whitelist whitelist = playerManager.getWhitelist();
        OperatorList opList = playerManager.getOpList();

        try {
            whitelist.load();
            opList.load();


        } catch (Exception e) {
            return false;
        }

        return true;
    }



}
