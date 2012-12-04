package com.dataart.android.devicehive.network;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.dataart.android.devicehive.DeviceHive;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Base command for JSON-related commands.
 * 
 */
public abstract class JsonNetworkCommand extends NetworkCommand {

	private static Gson gson;

	static {
		GsonBuilder builder = new GsonBuilder();
		builder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);
		builder.setPrettyPrinting();
		builder.registerTypeAdapter(Date.class, new DateTypeAdapter());
		gson = builder.create();
	}

	private static class DateTypeAdapter implements JsonSerializer<Date>,
			JsonDeserializer<Date> {
		private final DateFormat dateFormat;

		private DateTypeAdapter() {
			dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
			dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		}

		@Override
		public synchronized JsonElement serialize(Date date, Type type,
				JsonSerializationContext jsonSerializationContext) {
			return new JsonPrimitive(dateFormat.format(date));
		}

		@Override
		public synchronized Date deserialize(JsonElement jsonElement,
				Type type, JsonDeserializationContext jsonDeserializationContext) {
			try {
				return dateFormat.parse(jsonElement.getAsString());
			} catch (ParseException e) {
				throw new JsonParseException(e);
			}
		}
	}

	@Override
	protected Map<String, String> getHeaders() {
		final Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json; charset=utf-8");
		return headers;
	}

	@Override
	protected HttpEntity getRequestEntity() {
		String data = toJson(gson);
		HttpEntity entity = null;
		try {
			entity = new StringEntity(data, "utf-8");
		} catch (UnsupportedEncodingException e) {
			Log.e(DeviceHive.TAG, "Failed to create entity", e);
		}
		return entity;
	}

	protected abstract String toJson(final Gson gson);

	protected abstract int fromJson(final String response, final Gson gson,
			final Bundle resultData);

	@Override
	protected int handleResponse(final String response,
			final Bundle resultData, final Context context) {
		return fromJson(response, gson, resultData);
	}
	
	protected static String encodedString(String stringToEncode) {
		String encodedString = null;
		try {
			encodedString = URLEncoder.encode(stringToEncode, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("String to encode is illegal");
		}
		return encodedString;
	}

}
