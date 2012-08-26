package uk.co.datumedge.hamcrest.json;

import static org.skyscreamer.jsonassert.JSONCompare.compareJSON;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT_ORDER;
import static uk.co.datumedge.hamcrest.json.JSONAssertComparisonResult.resultOf;

import org.json.JSONArray;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * A {@code JSONComparator} implementation that compares {@code JSONArray}s, backed by SkyScreamer's JSONAssert library.
 */
public final class JSONArrayAssertComparator implements JSONComparator<JSONArray> {
	private final JSONCompareMode compareMode;

	public static JSONComparator<JSONArray> actualJSONArraySameAsExpected() {
		return new JSONArrayAssertComparator(STRICT);
	}

	public static JSONComparator<JSONArray> actualJSONArraySuperSetOfExpected() {
		return new JSONArrayAssertComparator(STRICT_ORDER);
	}

	private JSONArrayAssertComparator(JSONCompareMode compareMode) {
		this.compareMode = compareMode;
	}

	@Override
	public JSONComparisonResult compare(JSONArray expected, JSONArray actual) throws JSONException {
		return resultOf(compareJSON(expected, actual, compareMode));
	}

	@Override
	public JSONComparator<JSONArray> butAllowingAnyArrayOrdering() {
		return new JSONArrayAssertComparator(compareMode.butNotStrict());
	}

	@Override
	public JSONComparator<JSONArray> butAllowingExtraUnexpectedFields() {
		return new JSONArrayAssertComparator(compareMode.withExtensibility());
	}
}
