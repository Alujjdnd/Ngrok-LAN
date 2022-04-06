package alujjdnd.ngrok.lan.command;

import alujjdnd.ngrok.lan.NgrokLan;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
//import com.mojang.brigadier.builder.LiteralArgumentBuilder;
//import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
//import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.argument.EntityArgumentType;
//import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.OperatorEntry;
import net.minecraft.server.OperatorList;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
//import net.minecraft.text.Text;
//import net.minecraft.text.TranslatableText;
//
//import java.util.Collection;
//import java.util.Iterator;

public class LanOpCommand {


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("lanop")
                .requires(source -> source.hasPermissionLevel(4))
                .then(CommandManager.argument("player", EntityArgumentType.player()).executes(LanOpCommand::execute))
        );
    }

    private static int execute(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        NgrokLan.LOGGER.info("/op called"); //for debugging
        OperatorList ops = ctx.getSource().getServer().getPlayerManager().getOpList();
        ServerPlayerEntity playerToOp = EntityArgumentType.getPlayer(ctx, "player");

        ops.add(new OperatorEntry(playerToOp.getGameProfile(), 3, false) );
        //bypassPlayerLimit -> allow player to join when server is full (not sure if it kicks people)

        return Command.SINGLE_SUCCESS;
    }
}
