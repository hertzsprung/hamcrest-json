package uk.co.datumedge.hamcrest.json;

import static org.skyscreamer.jsonassert.JSONCompare.compareJSON;
import static uk.co.datumedge.hamcrest.json.JSONAssertComparisonResult.resultOf;

import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * A {@code JSONComparator} implementation that compares {@code JSONObject}s, backed by SkyScreamer's JSONAssert library.
 */
final class JSONObjectComparatorFactory implements JSONAssertComparatorFactory<JSONObject> {
	private static final JSONAssertComparatorFactory<JSONObject> INSTANCE = new JSONObjectComparatorFactory();

	static JSONAssertComparatorFactory<JSONObject> jsonObjectComparison() {
		return INSTANCE;
	}

	private JSONObjectComparatorFactory() { }

	@Override
	public JSONComparator<JSONObject> comparatorWith(final JSONCompareMode compareMode) {
		return new JSONComparator<JSONObject>() {
			@Override
			public JSONComparisonResult compare(JSONObject expected, JSONObject actual) throws JSONException {
				return resultOf(compareJSON(expected, actual, compareMode));
			}
		};
	}
}
