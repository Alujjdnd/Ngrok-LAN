package alujjdnd.ngrok.lan;


import alujjdnd.ngrok.lan.command.LanDeopCommand;
import alujjdnd.ngrok.lan.command.LanOpCommand;
import alujjdnd.ngrok.lan.command.LanWhitelistCommand;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.SERVER)
public class NgrokServerInitialiser implements DedicatedServerModInitializer
{

    public static final Logger LOGGER = LoggerFactory.getLogger(NgrokLan.MODID);

    @Override
    // Initialize the differents parts of the mod when lauched on server
    public void onInitializeServer()
    {
        LOGGER.info("World is Open to LAN with Ngrok LAN");

        //register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
        {
            LanOpCommand.register(dispatcher);
            LanDeopCommand.register(dispatcher);
            LanWhitelistCommand.register(dispatcher);
        });
    }

}
