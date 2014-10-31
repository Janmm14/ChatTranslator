package com.github.davewolax;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import lombok.NonNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.List;
import java.util.Map;

public class ChatMessageSendingListener extends PacketAdapter {

	private final Main main;
	private final TranslationProvider translator;

	public ChatMessageSendingListener(@NonNull final Main main) {
		super(PacketAdapter.params(main, PacketType.Play.Server.CHAT).serverSide().listenerPriority(ListenerPriority.HIGHEST).optionAsync());
		this.main = main;
		translator = main.getTranslationProvider();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onPacketSending(final PacketEvent event) {
		if (event.getPacketType() != PacketType.Play.Server.CHAT) {
			return;
		}
		if (event.isCancelled()) {
			return;
		}
		final PacketContainer packet = event.getPacket();
		final List<WrappedChatComponent> chatComponentValues = packet.getChatComponents().getValues();

		// igonre system messages if set in config
		if (!main.getConfig().getBoolean("ignoreSystemMessages") || packet.getBytes().size() <= 0 || packet.getBytes().read(0) != 1) {
			// Parse Json, translate messages only
			event.setCancelled(true);
			final String lang = User.get(event.getPlayer()).getLanguage();
			for (final WrappedChatComponent chatComponent : chatComponentValues) {
				final Object parsed = JSONValue.parse(chatComponent.getJson());
				if (parsed == null) {
					if (chatComponent.getJson().isEmpty()) {
						continue;
					} else {
						// TODO print error
					}
				} else if (parsed instanceof String) {
					chatComponent.setJson(translator.getTranslationOf(((String) parsed), "auto", lang));
				} else if (parsed instanceof JSONArray) {
					chatComponent.setJson(parseAndTranslate((JSONArray) parsed, lang).toJSONString());
				} else if (parsed instanceof JSONObject) {
					chatComponent.setJson(parseAndTranslate((JSONObject) parsed, lang).toJSONString());
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private JSONArray parseAndTranslate(final JSONArray json, final String languageTo) {
		for (int i = 0, jsonSize = json.size(); i < jsonSize; i++) {
			final Object obj = json.get(i);
			if (obj instanceof JSONArray) {
				json.set(i, parseAndTranslate((JSONArray) obj, languageTo));
			} else if (obj instanceof JSONObject) {
				json.set(i, parseAndTranslate((JSONObject) obj, languageTo));
			}
		}
		return json;
	}

	@SuppressWarnings("unchecked")
	private JSONObject parseAndTranslate(final JSONObject json, final String languageTo) {
		// look for excluding tags: text, translate & score (to just give google the strings that will be displayed)
		boolean hasText = false, hasTranslate = false, hasScore = false, hasSelector = false;
		// score exludes: selector
		// translate excludes: score, selector
		// text excludes: translate, score, selector
		for (final Object obj : json.entrySet()) {
			if (hasText)
				break;
			final Map.Entry entry = (Map.Entry) obj;
			if (entry.getKey() instanceof String) {
				final String key = ((String) entry.getKey()).toLowerCase().trim().intern();
				switch (key) {
					case "text":
						hasText = true;
						break;
					case "translate":
						hasTranslate = true;
						break;
					case "score":
						hasScore = true;
						break;
					case "selector":
						hasSelector = true;
						break;
				}
			}
		}
		if (hasText || hasTranslate || hasScore ||hasSelector) {
			for (final Object obj : json.entrySet()) {
				final Map.Entry entry = (Map.Entry) obj;
				final String key = ((String) entry.getKey()).toLowerCase().trim().intern();
				switch (key) {
					case "text": // TODO look how google handles \n (=newline) and @p, @a, @r and @e
						entry.setValue(translator.getTranslationOf((String) entry.getValue(), "auto", languageTo));
						break;
					case "extra":
						if (entry.getValue() instanceof String) {
							entry.setValue(translator.getTranslationOf((String) entry.getValue(), "auto", languageTo));
						} else if (entry.getValue() instanceof JSONObject) {
							entry.setValue(parseAndTranslate((JSONObject) entry.getValue(), languageTo));
						} else if (entry.getValue() instanceof JSONArray) {
							entry.setValue(parseAndTranslate((JSONArray) entry.getValue(), languageTo));
						}
						break;
					case "selector": // TODO look how google handles \n (=newline) and @p, @a, @r and @e
						if (!hasText && !hasTranslate && !hasScore) {
							entry.setValue(translator.getTranslationOf((String) entry.getValue(), "auto", languageTo));
						}
						break;
				}
			}
		}
		return json;
	}
}
