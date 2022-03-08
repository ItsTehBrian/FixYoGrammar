package xyz.tehbrian.fixyogrammar.config;

import com.google.common.base.Charsets;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.tehbrian.fixyogrammar.inject.PluginLogger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A wrapper for a {@link FileConfiguration}, which provides convenience methods
 * for loading, saving, and getting.
 */
public class ConfigWrapper {

    private final JavaPlugin javaPlugin;
    private final Logger logger;
    private final File file;

    private FileConfiguration fileConfig;

    /**
     * @param javaPlugin the javaPlugin
     * @param logger     the logger
     * @param file       the file
     */
    public ConfigWrapper(
            final @NonNull JavaPlugin javaPlugin,
            final @NonNull Logger logger,
            final @NonNull File file
    ) {
        this.javaPlugin = javaPlugin;
        this.logger = logger;
        this.file = file;
    }

    /**
     * @param javaPlugin the plugin
     * @param logger     the logger
     * @param fileName   the file name
     */
    public ConfigWrapper(
            final @NonNull JavaPlugin javaPlugin,
            final @NonNull @PluginLogger Logger logger,
            final @NonNull String fileName
    ) {
        this(javaPlugin, logger, new File(javaPlugin.getDataFolder(), fileName));
    }

    /**
     * Gets the {@link FileConfiguration} in memory and if not in memory,
     * executes {@link #load()} first.
     *
     * @return the file configuration
     */
    public @NonNull FileConfiguration get() {
        if (this.fileConfig == null) {
            this.load();
        }
        return this.fileConfig;
    }

    /**
     * Loads the {@link FileConfiguration} from the {@link #file} and if present,
     * sets the defaults using the default config provided by the plugin jar.
     */
    public void load() {
        this.fileConfig = YamlConfiguration.loadConfiguration(this.file);

        final InputStream defConfigStream = this.javaPlugin.getResource(this.file.getName());
        if (defConfigStream != null) {
            final InputStreamReader reader = new InputStreamReader(defConfigStream, Charsets.UTF_8);
            this.fileConfig.setDefaults(YamlConfiguration.loadConfiguration(reader));
        }
    }

    /**
     * Saves the {@link FileConfiguration} in memory to the plugin's data folder.
     *
     * <b>WARNING:</b> Since {@link FileConfiguration} does not store comments,
     * all comments will be deleted. As such, usage of this method is heavily
     * discouraged if the config contains comments and is to be user-facing.
     */
    public void save() {
        try {
            this.get().save(this.file);
        } catch (final IOException ex) {
            this.logger.log(Level.SEVERE, "Could not save config to " + this.file.getAbsolutePath(), ex);
        }
    }

    /**
     * Saves the default config provided by the plugin jar to
     * the plugin's data folder if the file is not present.
     */
    public void saveDefault() {
        if (!this.file.exists()) {
            this.javaPlugin.saveResource(this.file.getName(), false);
        }
    }

}
