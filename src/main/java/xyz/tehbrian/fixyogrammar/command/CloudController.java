package xyz.tehbrian.fixyogrammar.command;

import cloud.commandframework.Command;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.inject.Inject;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.tehbrian.fixyogrammar.inject.PluginLogger;

import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Manages a {@link cloud.commandframework.CommandManager} instance.
 */
public class CloudController {

    private final Logger logger;
    private final JavaPlugin javaPlugin;

    private PaperCommandManager<CommandSender> commandManager;

    @Inject
    public CloudController(
            final @NonNull @PluginLogger Logger logger,
            final @NonNull JavaPlugin javaPlugin
    ) {
        this.logger = logger;
        this.javaPlugin = javaPlugin;
    }

    /**
     * Initializes the {@link cloud.commandframework.CommandManager} instance.
     */
    public void init() throws Exception {
        if (this.commandManager != null) {
            throw new RuntimeException("The CommandManager has already been instantiated!");
        }
        this.logger.info("Instantiating new CommandManager.");

        try {
            this.commandManager = new PaperCommandManager<>(
                    this.javaPlugin,
                    CommandExecutionCoordinator.simpleCoordinator(),
                    Function.identity(),
                    Function.identity()
            );
        } catch (final Exception e) {
            this.logger.severe("Failed to construct the CommandManager. Something has gone very, very wrong!");
            throw e;
        }

        if (this.commandManager.queryCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            this.commandManager.registerAsynchronousCompletions();
        }
    }

    /**
     * Registers a new {@link Command}.
     *
     * @param command the {@link Command} which should be registered
     */
    public void registerCommand(final Command<CommandSender> command) {
        this.logger.info("Registering Command: " + command.getArguments().get(0).getName());

        this.commandManager.command(command);
    }

    /**
     * Registers a new {@link Command}. Calls {@link Command.Builder#build()}.
     *
     * @param commandBuilder the {@link Command.Builder} which should be registered
     */
    public void registerCommand(final Command.Builder<CommandSender> commandBuilder) {
        this.registerCommand(commandBuilder.build());
    }

    /**
     * @return the internal {@link cloud.commandframework.CommandManager}
     */
    public PaperCommandManager<CommandSender> getCommandManager() {
        return this.commandManager;
    }

}
