package alujjdnd.ngrok.lan.command;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Texts;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class LanWhitelistCommand {
    private static final SimpleCommandExceptionType ALREADY_ON_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.whitelist.alreadyOn"));
    private static final SimpleCommandExceptionType ALREADY_OFF_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.whitelist.alreadyOff"));
    private static final SimpleCommandExceptionType ADD_FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.whitelist.add.failed"));
    private static final SimpleCommandExceptionType REMOVE_FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.whitelist.remove.failed"));

    public LanWhitelistCommand() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandManager.literal("whitelist").requires((source) -> {
            return source.hasPermissionLevel(3);
        })).then(CommandManager.literal("on").executes((context) -> {
            return executeOn(context.getSource());
        })).then(CommandManager.literal("off").executes((context) -> {
            return executeOff(context.getSource());
        })).then(CommandManager.literal("list").executes((context) -> {
            return executeList(context.getSource());
        })).then(CommandManager.literal("add").then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile()).suggests((context, builder) -> {
            PlayerManager playerManager = context.getSource().getServer().getPlayerManager();
            return CommandSource.suggestMatching(playerManager.getPlayerList().stream().filter((player) -> {
                return !playerManager.getWhitelist().isAllowed(player.getGameProfile());
            }).map((player) -> {
                return player.getGameProfile().getName();
            }), builder);
        }).executes((context) -> {
            return executeAdd(context.getSource(), GameProfileArgumentType.getProfileArgument(context, "targets"));
        }))).then(CommandManager.literal("remove").then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile()).suggests((context, builder) -> {
            return CommandSource.suggestMatching(context.getSource().getServer().getPlayerManager().getWhitelistedNames(), builder);
        }).executes((context) -> {
            return executeRemove(context.getSource(), GameProfileArgumentType.getProfileArgument(context, "targets"));
        }))).then(CommandManager.literal("reload").executes((context) -> {
            return executeReload(context.getSource());
        })));
    }

    private static int executeReload(ServerCommandSource source) {
        source.getServer().getPlayerManager().reloadWhitelist();
        source.sendFeedback(Text.translatable("commands.whitelist.reloaded"), true);
        kickNonWhitelistedPlayers(source);
        return 1;
    }

    private static int executeAdd(ServerCommandSource source, Collection<GameProfile> targets) throws CommandSyntaxException {
        Whitelist whitelist = source.getServer().getPlayerManager().getWhitelist();
        int i = 0;

        for (GameProfile gameProfile : targets) {
            if (!whitelist.isAllowed(gameProfile)) {
                WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile);
                whitelist.add(whitelistEntry);
                source.sendFeedback(Text.translatable("commands.whitelist.add.success", Texts.toText(gameProfile)), true);
                ++i;
            }
        }

        if (i == 0) {
            throw ADD_FAILED_EXCEPTION.create();
        } else {
            return i;
        }
    }

    private static int executeRemove(ServerCommandSource source, Collection<GameProfile> targets) throws CommandSyntaxException {
        Whitelist whitelist = source.getServer().getPlayerManager().getWhitelist();
        int i = 0;

        for (GameProfile gameProfile : targets) {
            if (whitelist.isAllowed(gameProfile)) {
                WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile);
                whitelist.remove(whitelistEntry);
                source.sendFeedback(Text.translatable("commands.whitelist.remove.success", Texts.toText(gameProfile)), true);
                ++i;
            }
        }

        if (i == 0) {
            throw REMOVE_FAILED_EXCEPTION.create();
        } else {
            kickNonWhitelistedPlayers(source);
            return i;
        }
    }

    private static int executeOn(ServerCommandSource source) throws CommandSyntaxException {
        PlayerManager playerManager = source.getServer().getPlayerManager();
        if (playerManager.isWhitelistEnabled()) {
            throw ALREADY_ON_EXCEPTION.create();
        } else {
            playerManager.setWhitelistEnabled(true);
            source.sendFeedback(Text.translatable("commands.whitelist.enabled"), true);
            kickNonWhitelistedPlayers(source);
            return 1;
        }
    }

    private static int executeOff(ServerCommandSource source) throws CommandSyntaxException {
        PlayerManager playerManager = source.getServer().getPlayerManager();
        if (!playerManager.isWhitelistEnabled()) {
            throw ALREADY_OFF_EXCEPTION.create();
        } else {
            playerManager.setWhitelistEnabled(false);
            source.sendFeedback(Text.translatable("commands.whitelist.disabled"), true);
            return 1;
        }
    }

    private static int executeList(ServerCommandSource source) {
        String[] strings = source.getServer().getPlayerManager().getWhitelistedNames();
        if (strings.length == 0) {
            source.sendFeedback(Text.translatable("commands.whitelist.none"), false);
        } else {
            source.sendFeedback(Text.translatable("commands.whitelist.list", strings.length, String.join(", ", strings)), false);
        }

        return strings.length;
    }
    private static void kickNonWhitelistedPlayers(ServerCommandSource source){
        PlayerManager playerManager = source.getServer().getPlayerManager();
        if (playerManager.isWhitelistEnabled()) {
            Whitelist whitelist = playerManager.getWhitelist();
            List<ServerPlayerEntity> list = Lists.newArrayList(playerManager.getPlayerList());

            for (ServerPlayerEntity serverPlayerEntity : list) {
                GameProfile profile = serverPlayerEntity.getGameProfile();
                if(!source.getServer().isHost(profile)){
                    if (!whitelist.isAllowed(profile) && !playerManager.isOperator(profile)) {
                        serverPlayerEntity.networkHandler.disconnect(Text.translatable("multiplayer.disconnect.not_whitelisted"));
                    }
                }

            }

        }
    }
}
