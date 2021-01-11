package net.advancius.listener;

import net.advancius.AdvanciusConfiguration;
import net.advancius.AdvanciusLogger;
import net.advancius.AdvanciusSpigot;
import net.advancius.communication.CommunicationConfiguration;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.packet.Packet;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@FlagManager.FlaggedClass
public class ChatListener implements Listener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 30)
    private static void chatListener() {
        Bukkit.getPluginManager().registerEvents(new ChatListener(), AdvanciusSpigot.getInstance());
    }

    private Map<String, Long> sentMessageList = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(PlayerChatEvent event) throws InterruptedException, ExecutionException, TimeoutException {
        if (AdvanciusConfiguration.getInstance().defaultFormatWorkaround && !event.getFormat().equals(defaultChatFormat(event))) return;
        if (event.isCancelled()) return;
        if (!CommunicationConfiguration.getInstance().isRedirectChat()) return;

        if (messageExists(event.getPlayer().getUniqueId(), event.getMessage())) {
            AdvanciusLogger.info("Encountered duplicate server-side chat message.");
            return;
        }

        AdvanciusLogger.info("Processing server-side chat message: " + event.getMessage());
        event.setCancelled(true);

        Packet packet = Packet.generatePacket("server_chat");
        packet.getMetadata().setMetadata("person", event.getPlayer().getUniqueId().toString());
        packet.getMetadata().setMetadata("message", event.getMessage());

        AdvanciusSpigot.getInstance().getCommunicationManager().getClient().sendPacket(packet, response -> {});
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    private String defaultChatFormat(PlayerChatEvent event) {
        return "<%1$s> %2$s";
    }

    private boolean messageExists(UUID player, String message) {
        String messageHash = player + message;
        if (sentMessageList.containsKey(messageHash) && sentMessageList.get(messageHash) + 100 > System.currentTimeMillis()) return true;
        sentMessageList.put(messageHash, System.currentTimeMillis());
        return false;
    }
}
