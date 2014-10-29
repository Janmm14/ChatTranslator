package com.github.davewolax;

import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public enum ClientVersion {
	V1_7, V1_8;

	public static ClientVersion getOfPlayer(final Player player) {
		final CraftPlayer craftPlayer = (CraftPlayer) player;
		final int version = craftPlayer.getHandle().playerConnection.networkManager.getVersion();
		if (version == 4 || version == 5) {
			return V1_7;
		}
		if (version >= 47) {
			return V1_8;
		}
		return null;
	}
}
