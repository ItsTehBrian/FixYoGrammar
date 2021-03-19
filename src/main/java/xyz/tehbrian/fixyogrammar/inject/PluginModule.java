package xyz.tehbrian.fixyogrammar.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.tehbrian.fixyogrammar.FixYoGrammar;

import java.util.logging.Logger;

/**
 * Guice module which provides bindings for the plugin and the plugin's logger.
 */
public class PluginModule extends AbstractModule {

    /**
     * FixYoGrammar reference.
     */
    private final FixYoGrammar fixYoGrammar;

    /**
     * @param fixYoGrammar FixYoGrammar reference
     */
    public PluginModule(
            final @NonNull FixYoGrammar fixYoGrammar
    ) {
        this.fixYoGrammar = fixYoGrammar;
    }

    /**
     * Configures the plugin bindings.
     */
    @Override
    protected void configure() {
        this.bind(FixYoGrammar.class).toInstance(this.fixYoGrammar);
        this.bind(JavaPlugin.class).toInstance(this.fixYoGrammar);
    }

    /**
     * Provides the plugin's {@code Logger}.
     *
     * @return the plugin's logger
     */
    @Provides
    @PluginLogger
    public Logger providePluginLogger() {
        return this.fixYoGrammar.getLogger();
    }

    /**
     * Provides the server.
     *
     * @return the server
     */
    @Provides
    public Server provideServer() {
        return this.fixYoGrammar.getServer();
    }
}
