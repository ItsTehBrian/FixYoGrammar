package xyz.tehbrian.fixyogrammar.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.file.FileConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.logging.Logger;

/**
 * Retrieves {@code String}s from a {@link FileConfiguration}
 * and formats them using {@link MiniMessage}.
 */
public class Lang {

    /**
     * The {@code ConfigWrapper} to get messages from.
     */
    private final ConfigWrapper configWrapper;
    private final Logger logger;

    /**
     * @param configWrapper the config wrapper
     * @param logger        the logger
     */
    public Lang(
            final @NonNull ConfigWrapper configWrapper,
            final @NonNull Logger logger
    ) {
        this.configWrapper = configWrapper;
        this.logger = logger;
    }

    /**
     * Gets the value for {@code configKey} in {@link #configWrapper}, and parses it
     * using {@link MiniMessage}.
     *
     * @param configKey   the config key
     * @param tagResolver the tag resolver
     * @return the component
     */
    public Component c(final String configKey, final TagResolver tagResolver) {
        return MiniMessage.miniMessage().deserialize(this.getAndVerifyString(configKey), tagResolver);
    }

    /**
     * Gets the value for {@code configKey} in {@link #configWrapper}, and parses it
     * using {@link MiniMessage}.
     *
     * @param configKey the config key
     * @return the component
     */
    public Component c(final String configKey) {
        return MiniMessage.miniMessage().deserialize(this.getAndVerifyString(configKey));
    }

    /**
     * Gets the value for {@code configKey} in {@link #configWrapper}, and verifies
     * that is not null.
     */
    private String getAndVerifyString(final String configKey) {
        final String rawValue = this.configWrapper.get().getString(configKey);

        if (rawValue == null) {
            this.logger.severe("Attempted to get message from non-existent config key \"" + configKey + "\"!");
            throw new IllegalArgumentException("No value found in the config for that given config key.");
        }

        return rawValue;
    }

}
