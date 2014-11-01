package com.github.davewolax.chattranslator;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BukkitListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(final PlayerJoinEvent event) {
		User.get(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(final PlayerQuitEvent event) {
		User.remove(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onKick(final PlayerKickEvent event) {
		User.remove(event.getPlayer());
	}
}
