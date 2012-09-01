package uk.co.datumedge.hamcrest.json;

import static org.skyscreamer.jsonassert.JSONCompare.compareJSON;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT_ORDER;
import static uk.co.datumedge.hamcrest.json.JSONAssertComparisonResult.resultOf;

import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * A {@code JSONComparator} implementation that compares {@code JSONObject}s, backed by SkyScreamer's JSONAssert library.
 */
public final class JSONObjectAssertComparator implements JSONComparator<JSONObject> {
	private final JSONCompareMode compareMode;

	public static JSONComparator<JSONObject> actualJSONObjectSameAsExpected() {
		return new JSONObjectAssertComparator(STRICT);
	}

	public static JSONComparator<JSONObject> actualJSONObjectSuperSetOfExpected() {
		return new JSONObjectAssertComparator(STRICT_ORDER);
	}

	private JSONObjectAssertComparator(JSONCompareMode compareMode) {
		this.compareMode = compareMode;
	}

	@Override
	public JSONComparisonResult compare(JSONObject expected, JSONObject actual) throws JSONException {
		return resultOf(compareJSON(expected, actual, compareMode));
	}

	@Override
	public JSONComparator<JSONObject> butAllowingAnyArrayOrdering() {
		return new JSONObjectAssertComparator(compareMode.withStrictOrdering(false));
	}

	@Override
	public JSONComparator<JSONObject> butAllowingExtraUnexpectedFields() {
		return new JSONObjectAssertComparator(compareMode.withExtensible(true));
	}
}
