package uk.co.datumedge.hamcrest.json;

import static org.skyscreamer.jsonassert.JSONCompare.compareJSON;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;
import static uk.co.datumedge.hamcrest.json.JSONAssertComparisonResult.resultOf;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A {@code JSONComparator} implementation that compares {@code JSONObject}s, backed by SkyScreamer's JSONAssert library.
 */
public final class JSONObjectAssertComparator implements JSONComparator<JSONObject> {

	@Override
	public JSONComparisonResult compare(JSONObject expected, JSONObject actual) throws JSONException {
		return resultOf(compareJSON(expected, actual, STRICT));
	}
}
