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
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
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
            this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, this.height - 58, 150, 20, new TranslatableText("text.autoconfig.ngroklan.LanButton"), (button) -> {
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
                mc.inGameHud.getChatHud().addMessage(new LiteralText("\u00a7cPlease set your Ngrok AuthToken! Do this in your menu > Mods > Ngrok LAN > Sliders Icon > Auth Token"));
            } else {
                try {
                    NgrokLan.LOGGER.info("Launched Lan!");

                    mc.inGameHud.getChatHud().addMessage(new LiteralText("\u00a7eStarting Ngrok Service..."));

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
                    mc.inGameHud.getChatHud().addMessage(new LiteralText("\u00a7aNgrok Service Initiated Successfully!"));
                    mc.inGameHud.getChatHud().addMessage(new LiteralText("Your server IP is - \u00a7e" + ngrok_url + "\u00a7f (Copied to Clipboard)"));
                    mc.keyboard.setClipboard(ngrok_url);

                    // This starts the LAN server and greys out the open to lan button
                    TranslatableText text;


                    if (this.client.getServer().openToLan(this.gameMode, this.allowCommands, port)) {
                        text = new TranslatableText("commands.publish.started", new Object[]{port});
                    } else {
                        text = new TranslatableText("commands.publish.failed");
                    }
                    this.client.inGameHud.getChatHud().addMessage(text);
                    this.client.updateWindowTitle();

                } catch (Exception error) {
                    error.printStackTrace();
                    mc.inGameHud.getChatHud().addMessage(new LiteralText(error.getMessage()));
                    mc.inGameHud.getChatHud().addMessage(new LiteralText("\u00a7cNgrok Service Initiation Failed!"));
                    //ngrokInitiated = false;
                    throw new RuntimeException("Ngrok Service Failed to Start" + error.getMessage());
                }
            }
        });

        // This starts the thread defined above
        thread.start();

    }

}
