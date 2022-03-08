package xyz.tehbrian.fixyogrammar.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.language.AmericanEnglish;

public final class LanguageToolModule extends AbstractModule {

    /**
     * The Language to be used for JLanguageTool.
     */
    // TODO: support all languages, configurable by config
    private final Language language = new AmericanEnglish();

    @Provides
    public JLanguageTool provideJLanguageTool() {
        return new JLanguageTool(this.language);
    }

}
