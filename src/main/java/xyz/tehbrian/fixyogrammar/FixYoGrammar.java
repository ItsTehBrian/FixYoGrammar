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
import xyz.tehbrian.fixyogrammar.command.CloudController;
import xyz.tehbrian.fixyogrammar.config.Config;
import xyz.tehbrian.fixyogrammar.config.ConfigWrapper;
import xyz.tehbrian.fixyogrammar.config.Lang;
import xyz.tehbrian.fixyogrammar.inject.CommandModule;
import xyz.tehbrian.fixyogrammar.inject.ConfigModule;
import xyz.tehbrian.fixyogrammar.inject.LanguageToolModule;
import xyz.tehbrian.fixyogrammar.inject.PluginModule;
import xyz.tehbrian.fixyogrammar.listener.ChatListener;

/**
 * The main class for FixYoGrammar.
 */
public final class FixYoGrammar extends JavaPlugin {

    @Override
    public void onEnable() {
        final Injector injector = Guice.createInjector(
                new PluginModule(this),
                new LanguageToolModule(),
                new ConfigModule(),
                new CommandModule()
        );

        PluginManager pluginManager = this.getServer().getPluginManager();

        pluginManager.registerEvents(injector.getInstance(ChatListener.class), this);

        CloudController cloudController = injector.getInstance(CloudController.class);

        try {
            cloudController.init();
        } catch (Exception e) {
            this.getServer().getPluginManager().disablePlugin(this);
        }

        PaperCommandManager<CommandSender> cloud = cloudController.getCommandManager();

        MinecraftHelp<CommandSender> minecraftHelp = new MinecraftHelp<>(
                "/fyg",
                player -> player,
                cloud
        );

        Command.Builder<CommandSender> base = cloud.commandBuilder("fixyogrammar", "fyg")
                .meta(CommandMeta.DESCRIPTION, "All commands for FixYoGrammar.")
                .handler((context) -> minecraftHelp.queryCommands("", context.getSender()));

        cloudController.registerCommand(base);

        cloudController.registerCommand(base.literal("reload")
                .meta(CommandMeta.DESCRIPTION, "Reloads the plugin.")
                .handler(context -> {
                    injector.getInstance(Key.get(ConfigWrapper.class, Names.named("config"))).load();
                    injector.getInstance(Key.get(ConfigWrapper.class, Names.named("lang"))).load();
                    injector.getInstance(Config.class).loadValues();
                    context.getSender().sendMessage(injector.getInstance(Lang.class).c("reload"));
                }));
    }
}
