package com.github.davewolax.chattranslator;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class User {

	@Getter
	private final UUID uuid;

	@Getter
	private final Player player;

	/**
	 * google language format
	 */
	@Getter
	/**
	 * google language format
	 */
	@Setter
	private String language;

	private User(@NonNull final UUID uuid) {
		this.uuid = uuid;
		player = Bukkit.getPlayer(uuid);
	}

	private User(@NonNull final Player player) {
		uuid = player.getUniqueId();
		this.player = player;
	}

	////////////////////// static methods //////////////////////

	@Getter
	private static final HashMap<UUID, User> users = new HashMap<>(10);

	public static User get(@NonNull final UUID uuid) {
		if (!users.containsKey(uuid)) {
			final User user = new User(uuid);
			users.put(uuid, user);
			return user;
		}
		return users.get(uuid);
	}

	public static User get(@NonNull final Player player) {
		if (!users.containsKey(player.getUniqueId())) {
			final User user = new User(player);
			users.put(player.getUniqueId(), user);
			return user;
		}
		return users.get(player.getUniqueId());
	}

	public static void remove(@NonNull final UUID uuid) {
		if (!users.containsKey(uuid)) {
			return;
		}
		users.remove(uuid);
	}

	public static void remove(@NonNull final Player player) {
		remove(player.getUniqueId());
	}
}
