package alujjdnd.ngrok.lan.command;

import alujjdnd.ngrok.lan.NgrokLan;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class ReloadJsonListsCommand {

    //check if player has permission (same permission level as /op and /deop)
    //run loadJson();
    //get source.getServer() from command

    private static final SimpleCommandExceptionType LOAD_JSON_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("text.info.ngroklan.reload.message"));

    public ReloadJsonListsCommand() {
    }

    //TODO: this is literally /op rn just need to change it
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("reloadngroklanlists")
                .requires(source -> source.hasPermissionLevel(3))
                .executes(ReloadJsonListsCommand::loadJson));
    }

    private static int loadJson(CommandContext<ServerCommandSource> serverCommandSourceCommandContext) throws CommandSyntaxException {
        ServerCommandSource source = serverCommandSourceCommandContext.getSource();

        int i;
        boolean bl3 = false;

        for(i = 0; !bl3 && i <= 2; ++i) {
            if (i > 0) {
                NgrokLan.LOGGER.warn("Encountered a problem while converting the op list, retrying in a few seconds");
                ReloadJsonListsCommand.sleepFiveSeconds();
            }

            bl3 = ServerConfigHandler.convertOperators(source.getServer());
            //fail is false
        }

        boolean bl4 = false;

        for(i = 0; !bl4 && i <= 2; ++i) {
            if (i > 0) {
                NgrokLan.LOGGER.warn("Encountered a problem while converting the whitelist, retrying in a few seconds");
                ReloadJsonListsCommand.sleepFiveSeconds();
            }

            bl4 = ServerConfigHandler.convertWhitelist(source.getServer());
            //fail is false
        }

        if(! (bl3 && bl4) ){
            throw LOAD_JSON_EXCEPTION.create();
        }

        return 1;
    }




    private static void sleepFiveSeconds() {
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException ignored) {
        }
    }
}
