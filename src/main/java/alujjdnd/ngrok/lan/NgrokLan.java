package alujjdnd.ngrok.lan;

import alujjdnd.ngrok.lan.command.LanDeopCommand;
import alujjdnd.ngrok.lan.command.LanOpCommand;
import alujjdnd.ngrok.lan.command.LanWhitelistCommand;
import alujjdnd.ngrok.lan.command.ReloadJsonListsCommand;
import com.github.alexdlaird.ngrok.NgrokClient;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import alujjdnd.ngrok.lan.config.NLanConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;




public class NgrokLan implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.

	public static final String MODID = "ngroklan";

	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static NgrokClient ngrokClient;

	public static boolean serverOpen = false;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Minecraft started with Ngrok LAN");
		AutoConfig.register(NLanConfig.class, JanksonConfigSerializer::new);

		//register commands
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
		{
			LanOpCommand.register(dispatcher);
			LanDeopCommand.register(dispatcher);
			LanWhitelistCommand.register(dispatcher);
			ReloadJsonListsCommand.register(dispatcher);
		});
	}


}
