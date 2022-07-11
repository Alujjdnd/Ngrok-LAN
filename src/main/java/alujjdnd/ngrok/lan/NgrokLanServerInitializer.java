package alujjdnd.ngrok.lan;

import alujjdnd.ngrok.lan.command.LanDeopCommand;
import alujjdnd.ngrok.lan.command.LanOpCommand;
import alujjdnd.ngrok.lan.command.LanWhitelistCommand;
import alujjdnd.ngrok.lan.command.ReloadJsonListsCommand;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.server.DedicatedServerModInitializer;
import org.quiltmc.qsl.command.api.CommandRegistrationCallback;

@Environment(EnvType.SERVER)
public class NgrokLanServerInitializer implements DedicatedServerModInitializer {
    @Override
    // Initialize the different parts of the mod when launched on server
    public void onInitializeServer(ModContainer mod) {
        NgrokLan.LOGGER.info("World is Open to LAN with Ngrok LAN");

		// Initialize the different parts of the mod when launched on server
		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> LanOpCommand.register(dispatcher)));
		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> LanDeopCommand.register(dispatcher)));
		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> LanWhitelistCommand.register(dispatcher)));
		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> ReloadJsonListsCommand.register(dispatcher)));
    }
}
