package com.github.davewolax;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Main extends JavaPlugin {

	@Getter
	private TranslationProvider translationProvider; // TODO implement

	@Override
	public void onEnable() {
		final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

		protocolManager.getAsynchronousManager().registerAsyncHandler(new ChatMessageSendingListener(this));

		getConfig().options().copyDefaults(true).copyHeader(true);
		getConfig().options().header("ignore");
		getConfig().addDefault("ignoreSystemMessages", true);

		// To be 100% secure delete all user objects of users who are not online if the event didn't made it correctly
		Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
			@Override
			public void run() {
				final List<UUID> toRemove = new ArrayList<>(Math.max(User.getUsers().size() - Bukkit.getOnlinePlayers().size(), 5));
				for (final Map.Entry<UUID, User> entry : User.getUsers().entrySet()) {
					final Player player = entry.getValue().getPlayer();
					if (player == null || !player.isOnline()) {
						toRemove.add(entry.getKey());
					}
				}
				for (final UUID uuid : toRemove) {
					User.getUsers().remove(uuid);
				}
			}
		}, 2 * 60 * 20, 2 * 60 * 20);
	}

	@Override
	public void onDisable() {

	}

	public static String getGoogleLangFromMinecraftLang(@NonNull final String minecraftShorthand) {
		final int underscorePos = minecraftShorthand.indexOf("_");
		return minecraftShorthand.substring(0, underscorePos);
	}
}
