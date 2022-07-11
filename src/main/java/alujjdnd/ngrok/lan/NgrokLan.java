package alujjdnd.ngrok.lan;

import alujjdnd.ngrok.lan.command.LanDeopCommand;
import alujjdnd.ngrok.lan.command.LanOpCommand;
import alujjdnd.ngrok.lan.command.LanWhitelistCommand;
import alujjdnd.ngrok.lan.command.ReloadJsonListsCommand;
import com.github.alexdlaird.ngrok.NgrokClient;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.command.api.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import alujjdnd.ngrok.lan.config.NLanConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

public class NgrokLan implements ModInitializer {
	public static final String MODID = "ngroklan";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static NgrokClient ngrokClient;
	public static boolean serverOpen = false;

	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("Minecraft started with Ngrok LAN");
		AutoConfig.register(NLanConfig.class, JanksonConfigSerializer::new);

		//Register commands
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, dedicated) -> {
			LanOpCommand.register(dispatcher);
			LanDeopCommand.register(dispatcher);
			LanWhitelistCommand.register(dispatcher);
			ReloadJsonListsCommand.register(dispatcher);
		});
	}
}
