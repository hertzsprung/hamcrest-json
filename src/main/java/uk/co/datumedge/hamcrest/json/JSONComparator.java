package uk.co.datumedge.hamcrest.json;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Compares JSON documents.
 */
public interface JSONComparator {
	JSONComparisonResult compare(JSONArray expected, JSONArray actual) throws JSONException;
}
