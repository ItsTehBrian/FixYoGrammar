package xyz.tehbrian.fixyogrammar.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.tehbrian.fixyogrammar.config.Config;
import xyz.tehbrian.fixyogrammar.config.ConfigWrapper;
import xyz.tehbrian.fixyogrammar.config.Lang;

import java.util.logging.Logger;

/**
 * Guice module which provides bindings for the various configuration files.
 */
public class ConfigModule extends AbstractModule {

    /**
     * Provides the ConfigWrapper for config.yml.
     *
     * @return the config wrapper
     */
    @Provides
    @Singleton
    public @Named("config") ConfigWrapper provideConfigWrapper(
            final @NonNull JavaPlugin javaPlugin,
            final @NonNull @PluginLogger Logger logger
    ) {
        ConfigWrapper configWrapper = new ConfigWrapper(javaPlugin, logger, "config.yml");
        configWrapper.saveDefault();

        return configWrapper;
    }

    /**
     * Provides Config.
     *
     * @return the config
     */
    @Provides
    @Singleton
    public Config provideConfig(
            final @NonNull @Named("config") ConfigWrapper configWrapper,
            final @NonNull @PluginLogger Logger logger
    ) {
        Config config = new Config(configWrapper, logger);
        config.loadValues();

        return config;
    }

    /**
     * Provides the ConfigWrapper for lang.yml
     *
     * @return the config wrapper
     */
    @Provides
    @Singleton
    public @Named("lang") ConfigWrapper provideLangWrapper(
            final @NonNull JavaPlugin javaPlugin,
            final @NonNull @PluginLogger Logger logger
    ) {
        ConfigWrapper langWrapper = new ConfigWrapper(javaPlugin, logger, "lang.yml");
        langWrapper.saveDefault();

        return langWrapper;
    }

    /**
     * Provides Lang.
     *
     * @return the lang
     */
    @Provides
    @Singleton
    public Lang provideLang(
            final @NonNull @Named("lang") ConfigWrapper langWrapper,
            final @NonNull @PluginLogger Logger logger
    ) {
        return new Lang(langWrapper, logger);
    }
}
