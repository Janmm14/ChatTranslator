package com.github.davewolax.chattranslator;

import lombok.NonNull;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author vemacs
 */
public class GoogleTranslator implements TranslationProvider {

	public String getTranslationOf(@NonNull final String string, @NonNull final String fromLang, @NonNull final String toLang) {
		return getTranslation(string, toLang);
	}

	private String readURL(final String url) {
		final StringBuilder response = new StringBuilder();
		try {
			final URL toRead = new URL(url);
			final URLConnection yc = toRead.openConnection();
			// Yahoo uses this UserAgent, so might as well use it to prevent 403s
			yc.setRequestProperty("User-Agent", "Mozilla/5.0");
			final BufferedReader in = new BufferedReader(new InputStreamReader(yc
					.getInputStream(), "UTF-8"));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return response.toString();
	}

	private final Pattern p = Pattern.compile("(?i)\\b((?:https?://|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:'\".,<>?������]))");

	private String getTranslation(String text, final String lang) {
		final HashMap<String, String> hm = new HashMap<>();
		final Matcher m = p.matcher(text);
		final StringBuffer sb = new StringBuffer();
		String urlTmp;
		// URL handling
		while (m.find()) {
			urlTmp = m.group(1);
			final String uuid = UUID.randomUUID().toString().replace("-", "");
			hm.put(uuid, urlTmp);
			text = text.replace(urlTmp, uuid);
			m.appendReplacement(sb, "");
			sb.append(urlTmp);
		}
		m.appendTail(sb);
		text = sb.toString();
		// end replace with UUID
		text = URLEncoder.encode(text);
		String response = readURL("http://translate.google.com/translate_a/t?q=" + text + "&client=p&text=&sl=auto&tl=" + lang + "&ie=UTF-8&oe=UTF-8");
		response = parse(response);

		// begin UUID to URL
		final Set<Map.Entry<String, String>> set = hm.entrySet();
		for (final Map.Entry<String, String> me : set) {
			response = response.replace(me.getKey(), me.getValue());
		}
		// end UUID to URL
		response = postProcess(response, lang);
		return response;
	}

	private String postProcess(String response, final String lang) {
		// post processing text
		response = response.replace(" :", ":");
		response = response.replace(" ,", ",");
		response = response.replace(". / ", "./");

		if (response.startsWith("\u00BF") && StringUtils.countMatches(response, "?") == 0) {
			response = response + "?";
		}
		if (response.startsWith("\u00A1") && StringUtils.countMatches(response, "!") == 0) {
			response = response + "!";
		}
		if (lang.equals("en") && response.startsWith("'re")) {
			response = "You" + response;
		}
		return response;
	}

	private String parse(final String response) {
		final JSONParser parser = new JSONParser();
		JSONObject obj = new JSONObject();
		try {
			obj = (JSONObject) parser.parse(response);
		} catch (final ParseException ignored) {
		}
		final JSONArray sentences = (JSONArray) obj.get("sentences");
		String finalResponse = "";
		for (final Object sentence : sentences) {
			final String line = "" + sentence;
			final String trans = getTrans(line);
			finalResponse = finalResponse + trans;
		}
		return finalResponse;
	}

	private String getTrans(final String sentence) {
		final JSONParser parser = new JSONParser();
		JSONObject obj = new JSONObject();
		try {
			obj = (JSONObject) parser.parse(sentence);
		} catch (final ParseException ignored) {
		}
		return (String) obj.get("trans");
	}

}
