package com.github.davewolax;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class ChatMessageSendingListener extends PacketAdapter {

	public ChatMessageSendingListener(final Main main) {
		super(PacketAdapter.params(main, PacketType.Play.Server.CHAT).serverSide().listenerPriority(ListenerPriority.HIGHEST).optionAsync());
	}

	@Override
	public void onPacketSending(final PacketEvent event) {
		if (event.getPacketType() != PacketType.Play.Server.CHAT) {
			return;
		}
		final PacketContainer packet = event.getPacket();
		final WrappedChatComponent cc = packet.getChatComponents().read(1);
		String jsonChatMessage =  cc.getJson();

		if(jsonChatMessage.contains(Main.IDENTIFY_TRANSLATED_MESSAGE)) {
			return;
		}

		event.setCancelled(true);
		final ClientVersion version = ClientVersion.getOfPlayer(event.getPlayer());

		if (version == ClientVersion.V1_7) {

		} else if (version == ClientVersion.V1_8) {
			if(packet.getBytes().size() != 0) {

			}
		} else { // Unsupported  version
			System.out.println("[ChatTranslator] Minecraft version of player " + event.getPlayer().getName() +
					" is not supported. Please inform the developers of this plugin.");
			event.setCancelled(false);
		}
	}
}
