package xyz.tehbrian.fixyogrammar.config;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.logging.Logger;

/**
 * Loads and holds values from a {@link ConfigWrapper}.
 */
public abstract class AbstractConfig {

    protected final ConfigWrapper configWrapper;
    protected final Logger logger;

    public AbstractConfig(
            final @NonNull ConfigWrapper configWrapper,
            final @NonNull Logger logger
    ) {
        this.configWrapper = configWrapper;
        this.logger = logger;
    }

    /**
     * Loads values from {@link #configWrapper}.
     */
    public abstract void loadValues();

    /**
     * @return the config wrapper
     */
    public ConfigWrapper getConfigWrapper() {
        return this.configWrapper;
    }

}
