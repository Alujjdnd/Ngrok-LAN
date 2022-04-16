package alujjdnd.ngrok.lan.command;

import alujjdnd.ngrok.lan.NgrokLan;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.OperatorEntry;
import net.minecraft.server.OperatorList;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.Collection;

public class LanOpCommand {

    static MinecraftClient mc = MinecraftClient.getInstance();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("op")
                .requires(source -> source.hasPermissionLevel(4))
                .then(CommandManager.argument("players", EntityArgumentType.players()).executes(LanOpCommand::execute))
        );
    }

    private static int execute(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        NgrokLan.LOGGER.info("/op called"); //for debugging
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(ctx, "players");

        PlayerManager playerManager = ctx.getSource().getServer().getPlayerManager();



        for(ServerPlayerEntity playerToOp: targets){
            GameProfile gameProfile = playerToOp.getGameProfile();
            if(!playerManager.isOperator(gameProfile)){
                playerManager.addToOperators(gameProfile);

                ctx.getSource().sendFeedback(new TranslatableText("commands.op.success", gameProfile.getName()), true);

            }
            else{
                mc.inGameHud.getChatHud().addMessage(new TranslatableText("commands.op.fail"));
            }
        }

        return Command.SINGLE_SUCCESS;
    }
}
