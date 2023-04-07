package net.leoj07.controlswapper.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.leoj07.controlswapper.ControlSwapper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;



public class ControlCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("control")
                .then(CommandManager.argument("player", EntityArgumentType.player()).executes(ControlCommand::controlStart))
                .then(CommandManager.literal("stop").executes(ControlCommand::controlStop)));
    }

    public static int controlStart(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        if(ctx.getSource().getPlayer() == null) {
            // TODO: Not tested. Should activate when the console runs the command
            ctx.getSource().sendFeedback(Text.literal("Only players can run this command")
                    .fillStyle(Style.EMPTY.withColor(Formatting.DARK_RED)), false);
            return 0;
        }

        if(ctx.getSource().getPlayer() == EntityArgumentType.getPlayer(ctx, "player")){
            ctx.getSource().sendFeedback(Text.literal("You can not control yourself")
                    .fillStyle(Style.EMPTY.withColor(Formatting.DARK_RED)), false);
            return 0;
        }

        ServerPlayerEntity playerControlIn = ctx.getSource().getPlayer();
        ServerPlayerEntity playerControlOut = EntityArgumentType.getPlayer(ctx, "player");

        ControlSwapper.LOGGER.info("isClient: " + ctx.getSource().getWorld().isClient);

        ctx.getSource().sendFeedback(Text.literal("You are now controlling " + playerControlOut.getDisplayName().getString())
                .fillStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN)), false);
        ControlSwapper.control(playerControlIn, playerControlOut);

        return 0;
    }

    // TODO: Test the command
    private static int controlStop(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity controlIn = ctx.getSource().getPlayer();

        if(ctx.getSource().getPlayer() == null) {
            // TODO: Not tested. Should activate when the console runs the command
            ctx.getSource().sendFeedback(Text.literal("Only players can run this command")
                    .fillStyle(Style.EMPTY.withColor(Formatting.DARK_RED)), false);
            return 0;
        }

        if(!ControlSwapper.isControlIn(controlIn)) {
            ctx.getSource().sendFeedback(Text.literal("You are not controlling anyone at the moment")
                    .fillStyle(Style.EMPTY.withColor(Formatting.DARK_RED)), false);
            return 0;
        }

        ControlSwapper.stop(controlIn);
        ctx.getSource().sendFeedback(Text.literal("You are no longer controlling anyone")
                .fillStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN)), false);

        return 0;
    }
}
