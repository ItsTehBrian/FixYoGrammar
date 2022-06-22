package xyz.tehbrian.fixyogrammar.command;

import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.AudienceProvider;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.inject.Inject;
import dev.tehbrian.tehlib.paper.cloud.PaperCloudCommand;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.configurate.NodePath;
import xyz.tehbrian.fixyogrammar.FixYoGrammar;
import xyz.tehbrian.fixyogrammar.config.LangConfig;

public final class MainCommand extends PaperCloudCommand<CommandSender> {

    private final FixYoGrammar fixYoGrammar;
    private final LangConfig langConfig;

    @Inject
    public MainCommand(
            final @NonNull FixYoGrammar fixYoGrammar,
            final @NonNull LangConfig lang
    ) {
        this.fixYoGrammar = fixYoGrammar;
        this.langConfig = lang;
    }

    @Override
    public void register(@NonNull final PaperCommandManager<CommandSender> commandManager) {
        final MinecraftHelp<CommandSender> minecraftHelp = new MinecraftHelp<>(
                "/fyg",
                AudienceProvider.nativeAudience(), commandManager
        );

        final var main = commandManager.commandBuilder("fyg")
                .meta(CommandMeta.DESCRIPTION, "The main command for FixYoGrammar.");

        final var help = main
                .handler((context) -> minecraftHelp.queryCommands("", context.getSender()));

        commandManager.command(help);

        commandManager.command(main.literal("reload")
                .meta(CommandMeta.DESCRIPTION, "Reloads the plugin.")
                .handler(context -> {
                    final boolean success = this.fixYoGrammar.loadConfiguration();

                    if (success) {
                        context.getSender().sendMessage(this.langConfig.c(NodePath.path("reload")));
                    } else {
                        context.getSender().sendMessage(this.langConfig.c(NodePath.path("reload"))); // TODO: different message
                    }
                }));
    }

}
