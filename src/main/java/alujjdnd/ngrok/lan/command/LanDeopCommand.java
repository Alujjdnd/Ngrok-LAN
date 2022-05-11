package alujjdnd.ngrok.lan.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

import java.util.Collection;

public class LanDeopCommand {
    private static final SimpleCommandExceptionType ALREADY_DEOPPED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.deop.failed"));

    public LanDeopCommand() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder) CommandManager.literal("deop").requires((source) -> {
            return source.hasPermissionLevel(3);
        })).then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile()).suggests((context, builder) -> {
            return CommandSource.suggestMatching((context.getSource()).getServer().getPlayerManager().getOpNames(), builder);
        }).executes((context) -> {
            return deop(context.getSource(), GameProfileArgumentType.getProfileArgument(context, "targets"));
        })));
    }

    private static int deop(ServerCommandSource source, Collection<GameProfile> targets) throws CommandSyntaxException {
        PlayerManager playerManager = source.getServer().getPlayerManager();
        int i = 0;

        for (GameProfile gameProfile : targets) {
            if (playerManager.isOperator(gameProfile)) {
                playerManager.removeFromOperators(gameProfile);
                ++i;
                source.sendFeedback(new TranslatableText("commands.deop.success", targets.iterator().next().getName()), true);
            }
        }

        if (i == 0) {
            throw ALREADY_DEOPPED_EXCEPTION.create();
        } else {
            source.getServer().kickNonWhitelistedPlayers(source);
            return i;
        }
    }
}
