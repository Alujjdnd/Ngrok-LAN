package alujjdnd.ngrok.lan;


import alujjdnd.ngrok.lan.command.LanDeopCommand;
import alujjdnd.ngrok.lan.command.LanOpCommand;
import alujjdnd.ngrok.lan.command.LanWhitelistCommand;
import alujjdnd.ngrok.lan.command.ReloadJsonListsCommand;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.SERVER)
public class NgrokServerInitialiser implements DedicatedServerModInitializer
{

    public static final Logger LOGGER = LoggerFactory.getLogger(NgrokLan.MODID);

    @Override
    public void onInitializeServer() {
        
        LOGGER.info("World is Open to LAN with Ngrok LAN");

        // Initialize the differents parts of the mod when lauched on server
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> LanOpCommand.register(dispatcher)));
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> LanDeopCommand.register(dispatcher)));
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> LanWhitelistCommand.register(dispatcher)));
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> ReloadJsonListsCommand.register(dispatcher)));
    }

}
