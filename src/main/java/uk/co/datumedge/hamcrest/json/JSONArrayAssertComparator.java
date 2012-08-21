package uk.co.datumedge.hamcrest.json;

import static org.skyscreamer.jsonassert.JSONCompare.compareJSON;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;
import static uk.co.datumedge.hamcrest.json.JSONComparisonResult.comparisonFailed;
import static uk.co.datumedge.hamcrest.json.JSONComparisonResult.comparisonPassed;

import org.json.JSONArray;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompareResult;

/**
 * A {@code JSONComparator} implementation that compares {@code JSONArray}s, backed by SkyScreamer's JSONAssert library.
 */
public final class JSONArrayAssertComparator implements JSONComparator<JSONArray> {
	@Override
	public JSONComparisonResult compare(JSONArray expected, JSONArray actual) throws JSONException {
		JSONCompareResult compareResult = compareJSON(expected, actual, STRICT);
		return compareResult.passed() ? comparisonPassed() : comparisonFailed(compareResult.getMessage());
	}
}
