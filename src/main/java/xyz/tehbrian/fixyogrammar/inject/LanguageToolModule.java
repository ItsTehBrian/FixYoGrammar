package xyz.tehbrian.fixyogrammar.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.language.AmericanEnglish;

/**
 * Guice module which provides bindings for LanguageTool.
 */
public class LanguageToolModule extends AbstractModule {

    /**
     * The Language to be used for providing the JLanguageTool.
     */
    // TODO Support all languages, configurable by config.
    @MonotonicNonNull
    private final Language language;

    public LanguageToolModule() {
        this.language = new AmericanEnglish();
    }

    /**
     * Provides a JLanguageTool instance.
     *
     * @return a JLanguageTool instance
     */
    @Provides
    public JLanguageTool provideJLanguageTool() {
        return new JLanguageTool(this.language);
    }
}
