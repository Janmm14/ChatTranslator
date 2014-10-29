package com.github.davewolax;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

class ChatSendingAdapter extends PacketAdapter {

	private Main main;

	public ChatSendingAdapter(final Main main) {
		super(PacketAdapter.params(main, PacketType.Play.Server.CHAT).serverSide().listenerPriority(ListenerPriority.HIGHEST).optionAsync());
		this.main = main;
	}

	@Override
	public void onPacketSending(final PacketEvent event) {
		if (event.getPacketType() != PacketType.Play.Server.CHAT) {
			return;
		}
		final ClientVersion version = ClientVersion.getOfPlayer(event.getPlayer());

	}
}
