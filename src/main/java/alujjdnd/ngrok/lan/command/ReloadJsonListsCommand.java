package alujjdnd.ngrok.lan.command;

import alujjdnd.ngrok.lan.NgrokLan;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class ReloadJsonListsCommand {

    //check if player has permission (same permission level as /op and /deop)
    //run loadJson();
    //get source.getServer() from command

    public ReloadJsonListsCommand() {
    }

    //TODO: this is literally /op rn just need to change it
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder) CommandManager.literal("op").requires((source) -> {
            return source.hasPermissionLevel(3);
        })).then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile()).suggests((context, builder) -> {
            PlayerManager playerManager = (context.getSource()).getServer().getPlayerManager();
            return CommandSource.suggestMatching(playerManager.getPlayerList().stream().filter((player) -> {
                return !playerManager.isOperator(player.getGameProfile());
            }).map((player) -> {
                return player.getGameProfile().getName();
            }), builder);
        }).executes((context) -> {
            return op(context.getSource(), GameProfileArgumentType.getProfileArgument(context, "targets"));
        })));
    }


    private boolean loadJson(ServerCommandSource source){
        int i;
        boolean bl3 = false;

        for(i = 0; !bl3 && i <= 2; ++i) {
            if (i > 0) {
                NgrokLan.LOGGER.warn("Encountered a problem while converting the op list, retrying in a few seconds");
                this.sleepFiveSeconds();
            }

            bl3 = ServerConfigHandler.convertOperators(source.getServer());
            //fail is false
        }

        boolean bl4 = false;

        for(i = 0; !bl4 && i <= 2; ++i) {
            if (i > 0) {
                NgrokLan.LOGGER.warn("Encountered a problem while converting the whitelist, retrying in a few seconds");
                this.sleepFiveSeconds();
            }

            bl4 = ServerConfigHandler.convertWhitelist(source.getServer());
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
