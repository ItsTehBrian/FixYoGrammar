package xyz.tehbrian.fixyogrammar.listener;

import com.google.inject.Inject;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.languagetool.JLanguageTool;
import org.languagetool.rules.RuleMatch;
import xyz.tehbrian.fixyogrammar.config.Config;
import xyz.tehbrian.fixyogrammar.config.Lang;
import xyz.tehbrian.fixyogrammar.inject.PluginLogger;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Listens to chat-related events.
 */
public class ChatListener implements Listener {

    private final JavaPlugin javaPlugin;
    private final JLanguageTool languageTool;
    private final Logger logger;
    private final Config config;
    private final Lang lang;

    @Inject
    public ChatListener(
            final @NonNull JavaPlugin javaPlugin,
            final @NonNull JLanguageTool jLanguageTool,
            final @NonNull @PluginLogger Logger logger,
            final @NonNull Config config,
            final @NonNull Lang lang
    ) {
        this.javaPlugin = javaPlugin;
        this.languageTool = jLanguageTool;
        this.logger = logger;
        this.config = config;
        this.lang = lang;
    }

    /**
     * Called when a Player sends a message.
     *
     * @param event the event
     */
    @EventHandler
    public void onAsyncChat(final AsyncChatEvent event) {
        String plainMessage = PlainComponentSerializer.plain().serialize(event.message());
        Player player = event.getPlayer();

        // comment in to use statistical ngram data:
        //langTool.activateLanguageModelRules(new File("/data/google-ngram-data"));

        List<RuleMatch> matches;
        try {
            matches = this.languageTool.check(plainMessage);
        } catch (IOException e) {
            this.logger.severe("There was an error when checking a message: " + e.getMessage());
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

                    for (RuleMatch match : matches) {
                        List<String> corrections = match.getSuggestedReplacements()
                                .stream()
                                .limit(this.config.maxSuggestions())
                                .collect(Collectors.toList());

                        String errorKey = corrections.size() <= 1
                                ? "error_single_correction"
                                : "error_multiple_corrections";

                        player.sendMessage(this.lang.c(errorKey,
                                Map.of("from", String.valueOf(match.getFromPos()),
                                        "to", String.valueOf(match.getToPos()),
                                        "snippet", plainMessage.substring(match.getFromPos(), match.getToPos()),
                                        "error", match.getMessage(),
                                        "correction", String.join(", ", corrections))));
                    }

                    if (this.config.strict()) {
                        player.sendMessage(this.lang.c("footer_strict"));
                    } else {
                        player.sendMessage(this.lang.c("footer"));
                    }

                    if (this.config.shame()) {
                        String shameKey = matches.size() <= 1
                                ? "shame_single_error"
                                : "shame_multiple_errors";

                        Component shameMessage = this.lang.c(shameKey,
                                Map.of("player", player.getName(),
                                        "num", String.valueOf(matches.size())));

                        UUID playerUuid = player.getUniqueId();
                        for (Player serverPlayer : this.javaPlugin.getServer().getOnlinePlayers()) {
                            if (playerUuid.equals(serverPlayer.getUniqueId())) {
                                return;
                            }

                            serverPlayer.sendMessage(shameMessage);
                        }
                    }
                },
                1);
    }
}
