package xyz.tehbrian.fixyogrammar.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.tehbrian.fixyogrammar.inject.PluginLogger;

import java.util.logging.Logger;

public class Config extends AbstractConfig {

    private boolean strict;
    private int maxSuggestions;

    public Config(
            final @NonNull ConfigWrapper configWrapper,
            final @NonNull @PluginLogger Logger logger
    ) {
        super(configWrapper, logger);
    }

    /**
     * Loads values from {@link #configWrapper}.
     */
    @Override
    public void loadValues() {
        FileConfiguration config = this.configWrapper.get();

        this.strict = config.getBoolean("strict", false);
        if (!config.isBoolean("strict")) {
            logger.warning("Config value strict does not exist. Defaulting to false.");
        }

        this.maxSuggestions = config.getInt("max_suggestions", 3);
        if (!config.isInt("max_suggestions")) {
            logger.warning("Config value max_suggestions does not exist. Defaulting to 3.");
        }

        logger.info("Successfully loaded all values for config.yml!");
    }

    /**
     * Whether the language checking mode should be strict.
     */
    public boolean strict() {
        return this.strict;
    }

    /**
     * # The max amount of suggestions that will be sent per error.
     */
    public int maxSuggestions() {
        return this.maxSuggestions;
    }
}
