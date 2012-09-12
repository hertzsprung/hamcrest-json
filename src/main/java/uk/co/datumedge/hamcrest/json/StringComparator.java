package uk.co.datumedge.hamcrest.json;

import static org.skyscreamer.jsonassert.JSONCompare.compareJSON;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;
import static uk.co.datumedge.hamcrest.json.JSONAssertComparisonResult.resultOf;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompareMode;

public class StringComparator implements JSONComparator<String> {
	public static JSONComparator<String> actualSameAsExpected() {
		return new StringComparator(STRICT);
	}
	
	private StringComparator(JSONCompareMode strict) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public JSONComparisonResult compare(String expected, String actual) throws JSONException {
		return resultOf(compareJSON(expected, actual, STRICT));
	}

	@Override
	public JSONComparator<String> butAllowingAnyArrayOrdering() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONComparator<String> butAllowingExtraUnexpectedFields() {
		// TODO Auto-generated method stub
		return null;
	}
}
