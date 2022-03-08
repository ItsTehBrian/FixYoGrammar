package xyz.tehbrian.fixyogrammar;

import cloud.commandframework.Command;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.languagetool.JLanguageTool;
import xyz.tehbrian.fixyogrammar.command.CloudController;
import xyz.tehbrian.fixyogrammar.config.Config;
import xyz.tehbrian.fixyogrammar.config.ConfigWrapper;
import xyz.tehbrian.fixyogrammar.config.Lang;
import xyz.tehbrian.fixyogrammar.inject.CommandModule;
import xyz.tehbrian.fixyogrammar.inject.ConfigModule;
import xyz.tehbrian.fixyogrammar.inject.LanguageToolModule;
import xyz.tehbrian.fixyogrammar.inject.PluginModule;
import xyz.tehbrian.fixyogrammar.listener.ChatListener;

import java.io.IOException;
import org.slf4j.Logger;

/**
 * The main class for the FixYoGrammar plugin.
 */
public final class FixYoGrammar extends JavaPlugin {

    private @MonotonicNonNull Injector injector;

    @Override
    public void onEnable() {
        this.injector = Guice.createInjector(
                new PluginModule(this),
                new LanguageToolModule(),
                new ConfigModule(),
                new CommandModule()
        );

        final PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(this.injector.getInstance(ChatListener.class), this);

        final CloudController cloudController = this.injector.getInstance(CloudController.class);
        try {
            cloudController.init();
        } catch (final Exception e) {
            this.getServer().getPluginManager().disablePlugin(this);
        }

        final PaperCommandManager<CommandSender> cloud = cloudController.getCommandManager();

        final MinecraftHelp<CommandSender> minecraftHelp = new MinecraftHelp<>(
                "/fyg",
                player -> player,
                cloud
        );

        final Command.Builder<CommandSender> base = cloud.commandBuilder("fixyogrammar", "fyg")
                .meta(CommandMeta.DESCRIPTION, "All commands for FixYoGrammar.")
                .handler((context) -> minecraftHelp.queryCommands("", context.getSender()));

        cloudController.registerCommand(base);

        cloudController.registerCommand(base.literal("reload")
                .meta(CommandMeta.DESCRIPTION, "Reloads the plugin.")
                .handler(context -> {
                    this.injector.getInstance(Key.get(ConfigWrapper.class, Names.named("config"))).load();
                    this.injector.getInstance(Key.get(ConfigWrapper.class, Names.named("lang"))).load();
                    this.injector.getInstance(Config.class).loadValues();
                    context.getSender().sendMessage(this.injector.getInstance(Lang.class).c("reload"));
                }));
    }

    public void loadLanguageTool() {
        final Logger logger = this.injector.getInstance(Logger.class);
        logger.info("Loading LanguageTool, this may take a moment.");

        final JLanguageTool jLanguageTool = this.injector.getInstance(JLanguageTool.class);
        try {
            jLanguageTool.check("dummy text");
        } catch (final IOException e) {
            logger.error("There was an error when checking a message: " + e.getMessage());
            return;
        }
        logger.info("Finished loading LanguageTool!");
    }

}
