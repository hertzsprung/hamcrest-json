package uk.co.datumedge.hamcrest.json;

import org.json.JSONException;

/**
 * Compares JSON documents.
 *
 * @param <T> the document type, typically {@code JSONObject} or {@code JSONArray}.
 */
public interface JSONComparator<T> {
	JSONComparisonResult compare(T expected, T actual) throws JSONException;
}
