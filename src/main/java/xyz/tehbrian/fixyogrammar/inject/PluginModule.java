package xyz.tehbrian.fixyogrammar.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import xyz.tehbrian.fixyogrammar.FixYoGrammar;

import java.nio.file.Path;

public final class PluginModule extends AbstractModule {

    private final FixYoGrammar fixYoGrammar;

    public PluginModule(final @NonNull FixYoGrammar fixYoGrammar) {
        this.fixYoGrammar = fixYoGrammar;
    }

    @Override
    protected void configure() {
        this.bind(FixYoGrammar.class).toInstance(this.fixYoGrammar);
        this.bind(JavaPlugin.class).toInstance(this.fixYoGrammar);
    }

    @Provides
    public @NonNull Logger provideSLF4JLogger() {
        return this.fixYoGrammar.getSLF4JLogger();
    }

    @Provides
    @Named("dataFolder")
    public @NonNull Path provideDataFolder() {
        return this.fixYoGrammar.getDataFolder().toPath();
    }

}
