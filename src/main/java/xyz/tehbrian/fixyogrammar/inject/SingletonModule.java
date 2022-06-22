package xyz.tehbrian.fixyogrammar.inject;

import com.google.inject.AbstractModule;
import xyz.tehbrian.fixyogrammar.command.CommandService;

public final class SingletonModule extends AbstractModule {

    @Override
    protected void configure() {
        this.bind(CommandService.class).asEagerSingleton();
    }

}
