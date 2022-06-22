package xyz.tehbrian.fixyogrammar.command;

import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.AudienceProvider;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import dev.tehbrian.tehlib.paper.cloud.PaperCloudCommand;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.tehbrian.fixyogrammar.config.Config;
import xyz.tehbrian.fixyogrammar.config.ConfigWrapper;
import xyz.tehbrian.fixyogrammar.config.Lang;

public final class MainCommand extends PaperCloudCommand<CommandSender> {

    private final ConfigWrapper configWrapper;
    private final ConfigWrapper langWrapper;
    private final Config config;
    private final Lang lang;

    @Inject
    public MainCommand(
            final @NonNull @Named("config") ConfigWrapper configWrapper,
            final @NonNull @Named("lang") ConfigWrapper langWrapper,
            final @NonNull Config config,
            final @NonNull Lang lang
    ) {
        this.configWrapper = configWrapper;
        this.langWrapper = langWrapper;
        this.config = config;
        this.lang = lang;
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
                    this.configWrapper.load();
                    this.langWrapper.load();
                    this.config.loadValues();
                    context.getSender().sendMessage(this.lang.c("reload"));
                }));
    }

}
