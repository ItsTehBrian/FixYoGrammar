package xyz.tehbrian.fixyogrammar;

import cloud.commandframework.paper.PaperCommandManager;
import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.tehbrian.tehlib.core.configurate.Config;
import dev.tehbrian.tehlib.paper.TehPlugin;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.languagetool.JLanguageTool;
import org.slf4j.Logger;
import org.spongepowered.configurate.ConfigurateException;
import xyz.tehbrian.fixyogrammar.command.CommandService;
import xyz.tehbrian.fixyogrammar.command.MainCommand;
import xyz.tehbrian.fixyogrammar.config.ConfigConfig;
import xyz.tehbrian.fixyogrammar.config.LangConfig;
import xyz.tehbrian.fixyogrammar.inject.LanguageToolModule;
import xyz.tehbrian.fixyogrammar.inject.PluginModule;
import xyz.tehbrian.fixyogrammar.inject.SingletonModule;
import xyz.tehbrian.fixyogrammar.listener.ChatListener;

import java.io.IOException;
import java.util.List;

/**
 * The main class for the FixYoGrammar plugin.
 */
public final class FixYoGrammar extends TehPlugin {

    private @MonotonicNonNull Injector injector;

    @Override
    public void onEnable() {
        try {
            this.injector = Guice.createInjector(
                    new PluginModule(this),
                    new LanguageToolModule(),
                    new SingletonModule()
            );
        } catch (final Exception e) {
            this.getSLF4JLogger().error("Something went wrong while creating the Guice injector.");
            this.getSLF4JLogger().error("Disabling plugin.");
            this.disableSelf();
            this.getSLF4JLogger().error("Printing stack trace, please send this to the developers:", e);
            return;
        }

        if (!this.loadConfiguration()) {
            this.disableSelf();
            return;
        }
        if (!this.setupCommands()) {
            this.disableSelf();
            return;
        }

        this.registerListeners(this.injector.getInstance(ChatListener.class));

        this.loadLanguageTool();
    }

    /**
     * @return whether it was successful
     */
    public boolean loadConfiguration() {
        this.saveResourceSilently("config.yml");
        this.saveResourceSilently("lang.yml");

        final List<Config> configsToLoad = List.of(
                this.injector.getInstance(ConfigConfig.class),
                this.injector.getInstance(LangConfig.class)
        );

        for (final Config config : configsToLoad) {
            try {
                config.load();
            } catch (final ConfigurateException e) {
                this.getSLF4JLogger().error("Exception caught during config load for {}", config.configurateWrapper().filePath());
                this.getSLF4JLogger().error("Please check your config.");
                this.getSLF4JLogger().error("Printing stack trace:", e);
                return false;
            }
        }

        this.getSLF4JLogger().info("Successfully loaded configuration.");
        return true;
    }

    /**
     * @return whether it was successful
     */
    private boolean setupCommands() {
        final @NonNull CommandService commandService = this.injector.getInstance(CommandService.class);
        try {
            commandService.init();
        } catch (final Exception e) {
            this.getSLF4JLogger().error("Failed to create the CommandManager.");
            this.getSLF4JLogger().error("Printing stack trace, please send this to the developers:", e);
            return false;
        }

        final @Nullable PaperCommandManager<CommandSender> commandManager = commandService.get();
        if (commandManager == null) {
            this.getSLF4JLogger().error("The CommandService was null after initialization!");
            return false;
        }

        this.injector.getInstance(MainCommand.class).register(commandManager);

        return true;
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
