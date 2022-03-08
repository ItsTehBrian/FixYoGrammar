package xyz.tehbrian.fixyogrammar.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;

import org.slf4j.Logger;

public class Config extends AbstractConfig {

    private boolean strict;
    private int maxSuggestions;
    private boolean shame;

    public Config(
            final @NonNull ConfigWrapper configWrapper,
            final @NonNull Logger logger
    ) {
        super(configWrapper, logger);
    }

    /**
     * Loads values from {@link #configWrapper}.
     */
    @Override
    public void loadValues() {
        final FileConfiguration config = this.configWrapper.get();

        this.strict = config.getBoolean("strict", false);
        if (!config.isBoolean("strict")) {
            logger.warn("Config value strict does not exist. Defaulting to false.");
        }

        this.maxSuggestions = config.getInt("max_suggestions", 3);
        if (!config.isInt("max_suggestions")) {
            logger.warn("Config value max_suggestions does not exist. Defaulting to 3.");
        }

        this.shame = config.getBoolean("shame", true);
        if (!config.isBoolean("shame")) {
            logger.warn("Config value shame does not exist. Defaulting to true.");
        }

        logger.info("Successfully loaded all values for config.yml!");
    }

    /**
     * @return whether the language checking mode should be strict
     */
    public boolean strict() {
        return this.strict;
    }

    /**
     * @return the max amount of suggestions that will be sent per error
     */
    public int maxSuggestions() {
        return this.maxSuggestions;
    }

    /**
     * @return whether mistakes should be publicly shamed in chat
     */
    public boolean shame() {
        return this.shame;
    }

}
