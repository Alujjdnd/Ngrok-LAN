package alujjdnd.ngrok.lan.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.*;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ReloadJsonListsCommand {

    private static final SimpleCommandExceptionType LOAD_JSON_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("text.info.ngroklan.reload.message"));

    public ReloadJsonListsCommand() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("reloadngroklanlists")
                .requires(source -> source.hasPermissionLevel(3))
                .executes(ReloadJsonListsCommand::loadJson));
    }

    private static int loadJson(CommandContext<ServerCommandSource> serverCommandSourceCommandContext) throws CommandSyntaxException {
        PlayerManager playerManager = serverCommandSourceCommandContext.getSource().getServer().getPlayerManager();

        Whitelist whitelist = playerManager.getWhitelist();
        OperatorList opList = playerManager.getOpList();

        try {
            whitelist.load();
            opList.load();

            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.translatable("text.info.ngroklan.reload.success").styled(style -> style.withColor(Formatting.GREEN) ));
        } catch (Exception e) {
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
