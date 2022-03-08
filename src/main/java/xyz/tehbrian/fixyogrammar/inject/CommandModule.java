package xyz.tehbrian.fixyogrammar.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import xyz.tehbrian.fixyogrammar.command.CloudController;

/**
 * Guice module which provides bindings for {@link CloudController}.
 */
public class CommandModule extends AbstractModule {

    /**
     * Configures bindings for {@link CloudController}.
     */
    @Override
    protected void configure() {
        this.bind(CloudController.class).in(Singleton.class);
    }

}
