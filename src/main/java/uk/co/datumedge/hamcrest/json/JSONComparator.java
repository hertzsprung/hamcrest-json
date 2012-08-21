package uk.co.datumedge.hamcrest.json;

import org.json.JSONException;

/**
 * Compares JSON documents.
 */
public interface JSONComparator<T> {
	JSONComparisonResult compare(T expected, T actual) throws JSONException;
}
