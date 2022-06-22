package xyz.tehbrian.fixyogrammar.inject;

import com.google.inject.AbstractModule;
import xyz.tehbrian.fixyogrammar.command.CommandService;
import xyz.tehbrian.fixyogrammar.config.ConfigConfig;
import xyz.tehbrian.fixyogrammar.config.LangConfig;

public final class SingletonModule extends AbstractModule {

    @Override
    protected void configure() {
        this.bind(ConfigConfig.class).asEagerSingleton();
        this.bind(LangConfig.class).asEagerSingleton();

        this.bind(CommandService.class).asEagerSingleton();
    }

}
