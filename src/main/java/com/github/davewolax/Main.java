package com.github.davewolax;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	@Override
	public void onEnable() {
		final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

		protocolManager.getAsynchronousManager().registerAsyncHandler(new ChatSendingAdapter(this));
	}

	@Override
	public void onDisable() {

	}
}
