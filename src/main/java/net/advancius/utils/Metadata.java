package net.advancius.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import net.advancius.AdvanciusSpigot;

import java.util.HashMap;
import java.util.Map;

public class Metadata {

	@Getter private final Map<String, Object> internal = new HashMap<>();
	
	public <T> Object setMetadata(String key, T value) {
		return internal.put(key, value);
	}
	
	public Object unsetMetadata(String key) {
		return internal.remove(key);
	}
	
	public boolean hasMetadata(String key) {
		return internal.containsKey(key) && internal.get(key) != null;
	}
	 
	public boolean hasMetadata(String key, Object value) {
		return hasMetadata(key) && getMetadata(key).equals(value);
	}
	
	public <T> T getMetadata(String key) {
		return (T) internal.get(key);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getMetadata(String key, T def) {
		return isMetadataOf(key, def.getClass()) ? (T) getMetadata(key) : def;
	}
	
	public <T> boolean isMetadataOf(String key, Class<T> clazz) {
		return hasMetadata(key) && clazz.isInstance(getMetadata(key));
	}

	public JsonObject serialize() {
		JsonParser parser = new JsonParser();
		return parser.parse(AdvanciusSpigot.GSON.toJson(internal)).getAsJsonObject();
	}

	public void deserialize(Map<String, Object> data) {
		internal.putAll(data);
	}

	public void deserialize(JsonObject json) {
		internal.putAll(AdvanciusSpigot.GSON.fromJson(json, Map.class));
	}
}
