package uk.co.datumedge.hamcrest.json;

import static org.skyscreamer.jsonassert.JSONCompare.compareJSON;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT_ORDER;
import static uk.co.datumedge.hamcrest.json.JSONComparisonResult.comparisonFailed;
import static uk.co.datumedge.hamcrest.json.JSONComparisonResult.comparisonPassed;

import org.json.JSONArray;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompareResult;

/**
 * A {@code JSONComparator} implementation backed by SkyScreamer's JSONAssert library.
 */
public final class JSONAssertComparator implements JSONComparator {
	@Override
	public JSONComparisonResult compare(JSONArray expected, JSONArray actual) throws JSONException {
		JSONCompareResult compareResult = compareJSON(expected, actual, STRICT_ORDER);
		return compareResult.passed() ? comparisonPassed() : comparisonFailed(compareResult.getMessage());
	}
}
