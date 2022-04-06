package alujjdnd.ngrok.lan.command;

import alujjdnd.ngrok.lan.NgrokLan;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
//import com.mojang.brigadier.builder.LiteralArgumentBuilder;
//import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
//import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.EntityArgumentType;
//import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.OperatorEntry;
import net.minecraft.server.OperatorList;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import java.util.Collection;
//import net.minecraft.text.Text;
//import net.minecraft.text.TranslatableText;
//
//import java.util.Collection;
//import java.util.Iterator;

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

        OperatorList ops = ctx.getSource().getServer().getPlayerManager().getOpList();



        for(ServerPlayerEntity playerToOp: targets){
            GameProfile gameProfile = playerToOp.getGameProfile();

            if(ops.get(gameProfile) == null){
                ops.add(new OperatorEntry(gameProfile, 3, false) );
                //bypassPlayerLimit -> allow player to join when server is full (not sure if it kicks people)

                mc.inGameHud.getChatHud().addMessage(new TranslatableText("commands.op.success", gameProfile.getName()));
            }
            else{
                mc.inGameHud.getChatHud().addMessage(new TranslatableText("commands.op.fail"));
            }


        }

        return Command.SINGLE_SUCCESS;
    }
}
