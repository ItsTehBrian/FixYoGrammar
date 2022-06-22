package xyz.tehbrian.fixyogrammar.listener;

import com.google.inject.Inject;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.languagetool.JLanguageTool;
import org.languagetool.rules.RuleMatch;
import org.slf4j.Logger;
import xyz.tehbrian.fixyogrammar.config.Config;
import xyz.tehbrian.fixyogrammar.config.Lang;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class ChatListener implements Listener {

    private final JavaPlugin javaPlugin;
    private final JLanguageTool languageTool;
    private final Logger logger;
    private final Config config;
    private final Lang lang;

    @Inject
    public ChatListener(
            final @NonNull JavaPlugin javaPlugin,
            final @NonNull JLanguageTool jLanguageTool,
            final @NonNull Logger logger,
            final @NonNull Config config,
            final @NonNull Lang lang
    ) {
        this.javaPlugin = javaPlugin;
        this.languageTool = jLanguageTool;
        this.logger = logger;
        this.config = config;
        this.lang = lang;
    }

    @EventHandler
    public void onChat(final AsyncChatEvent event) {
        final String plainMessage = PlainTextComponentSerializer.plainText().serialize(event.message());
        final Player player = event.getPlayer();

        // comment in to use statistical ngram data:
        //langTool.activateLanguageModelRules(new File("/data/google-ngram-data"));

        final List<RuleMatch> matches;
        try {
            matches = this.languageTool.check(plainMessage);
        } catch (final IOException e) {
            this.logger.error("There was an error when checking a message: " + e.getMessage());
            return;
        }

        if (matches.isEmpty()) {
            return;
        }

        if (this.config.strict()) {
            event.setCancelled(true);
        }

        this.javaPlugin.getServer().getScheduler().scheduleSyncDelayedTask(
                this.javaPlugin,
                () -> {
                    player.sendMessage(this.lang.c("header"));

                    for (final RuleMatch match : matches) {
                        final List<String> corrections = match.getSuggestedReplacements()
                                .stream()
                                .limit(this.config.maxSuggestions())
                                .collect(Collectors.toList());

                        if (corrections.isEmpty()) {
                            final TagResolver resolver = TagResolver.resolver(
                                    Placeholder.unparsed("from", String.valueOf(match.getFromPos())),
                                    Placeholder.unparsed("to", String.valueOf(match.getToPos())),
                                    Placeholder.unparsed("snippet", plainMessage.substring(match.getFromPos(), match.getToPos())),
                                    Placeholder.unparsed("error", match.getMessage())
                            );

                            player.sendMessage(this.lang.c("error_no_corrections", resolver));
                        } else {
                            final String errorKey = corrections.size() <= 1
                                    ? "error_single_correction"
                                    : "error_multiple_corrections";

                            final TagResolver resolver = TagResolver.resolver(
                                    Placeholder.unparsed("from", String.valueOf(match.getFromPos())),
                                    Placeholder.unparsed("to", String.valueOf(match.getToPos())),
                                    Placeholder.unparsed("snippet", plainMessage.substring(match.getFromPos(), match.getToPos())),
                                    Placeholder.unparsed("error", match.getMessage()),
                                    Placeholder.unparsed("correction", String.join(", ", corrections))
                            );

                            player.sendMessage(this.lang.c(errorKey, resolver));
                        }
                    }

                    if (this.config.strict()) {
                        player.sendMessage(this.lang.c("footer_strict"));
                    } else {
                        player.sendMessage(this.lang.c("footer"));
                    }

                    if (this.config.shame()) {
                        final String shameKey = matches.size() <= 1
                                ? "shame_single_error"
                                : "shame_multiple_errors";

                        final Component shameMessage = this.lang.c(
                                shameKey,
                                TagResolver.resolver(
                                        Placeholder.unparsed("player", player.getName()),
                                        Placeholder.unparsed("num", String.valueOf(matches.size()))
                                )
                        );

                        final UUID playerUuid = player.getUniqueId();
                        for (final Player serverPlayer : this.javaPlugin.getServer().getOnlinePlayers()) {
                            if (playerUuid.equals(serverPlayer.getUniqueId())) {
                                return;
                            }

                            serverPlayer.sendMessage(shameMessage);
                        }
                    }
                },
                1
        );
    }

}
