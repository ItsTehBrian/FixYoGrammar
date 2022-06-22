package xyz.tehbrian.fixyogrammar.config;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import dev.tehbrian.tehlib.core.configurate.AbstractConfig;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;
import java.util.Objects;

/**
 * For {@code config.yml}.
 */
public final class ConfigConfig extends AbstractConfig<YamlConfigurateWrapper> {

    private final Logger logger;

    private boolean strict;
    private int maxSuggestions;
    private boolean shame;

    @Inject
    public ConfigConfig(
            final @NonNull @Named("dataFolder") Path dataFolder,
            final @NonNull Logger logger
    ) {
        super(new YamlConfigurateWrapper(dataFolder.resolve("config.yml"), YamlConfigurationLoader.builder()
                .path(dataFolder.resolve("config.yml"))
                .defaultOptions(opts -> opts.implicitInitialization(false))
                .build()));
        this.logger = logger;
    }

    @Override
    public void load() throws ConfigurateException {
        this.configurateWrapper.load();
        // will not be null as we called #load()
        final @NonNull CommentedConfigurationNode rootNode = Objects.requireNonNull(this.configurateWrapper.get());

        final var strictNode = rootNode.node("strict");
        if (strictNode.virtual()) {
            this.logger.warn("Config value `strict` does not exist. Defaulting to `false`.");
        }
        this.strict = strictNode.getBoolean(false);

        final var maxSuggestionsNode = rootNode.node("max_suggestions");
        if (maxSuggestionsNode.virtual()) {
            this.logger.warn("Config value `max_suggestions` does not exist. Defaulting to `3`.");
        }
        this.maxSuggestions = maxSuggestionsNode.getInt(3);

        final var shameNode = rootNode.node("shame");
        if (shameNode.virtual()) {
            this.logger.warn("Config value `shame` does not exist. Defaulting to `false`.");
        }
        this.shame = shameNode.getBoolean(false);
    }

    /**
     * @return whether messages will be denied if they have a grammatical error
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
     * @return whether mistakes will be publicly shamed in chat
     */
    public boolean shame() {
        return this.shame;
    }

}
