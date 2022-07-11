package alujjdnd.ngrok.lan.command;

import alujjdnd.ngrok.lan.NgrokLan;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ReloadJsonListsCommand {

    //check if player has permission (same permission level as /op and /deop)
    //run loadJson();
    //get source.getServer() from command

    private static final SimpleCommandExceptionType LOAD_JSON_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("text.info.ngroklan.reload.message"));

    public ReloadJsonListsCommand() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("reloadngroklanlists")
                .requires(source -> source.hasPermissionLevel(3))
                .executes(ReloadJsonListsCommand::loadJson));
    }

    private static int loadJson(CommandContext<ServerCommandSource> serverCommandSourceCommandContext) throws CommandSyntaxException {
        MinecraftServer server = serverCommandSourceCommandContext.getSource().getServer();

        boolean bl3 = ServerConfigHandler.convertOperators(server);

        boolean bl4 = ServerConfigHandler.convertWhitelist(server);


        if(! (bl3 && bl4) ){
            throw LOAD_JSON_EXCEPTION.create();
        }
        else{
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage( Text.translatable("text.info.ngroklan.reload.success"));
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
