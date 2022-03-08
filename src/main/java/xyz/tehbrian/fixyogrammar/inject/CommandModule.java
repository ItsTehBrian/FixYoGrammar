package xyz.tehbrian.fixyogrammar.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import xyz.tehbrian.fixyogrammar.command.CloudController;

public final class CommandModule extends AbstractModule {

    @Override
    protected void configure() {
        this.bind(CloudController.class).in(Singleton.class);
    }

}
