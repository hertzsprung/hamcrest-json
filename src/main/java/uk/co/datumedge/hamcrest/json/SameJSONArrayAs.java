package uk.co.datumedge.hamcrest.json;

import static org.skyscreamer.jsonassert.JSONCompare.compareJSON;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT_ORDER;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.json.JSONArray;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompareResult;

/**
 * Matcher that asserts that one JSONArray is the same as another.
 */
public class SameJSONArrayAs extends TypeSafeDiagnosingMatcher<JSONArray> {

	private JSONArray expected;

	public SameJSONArrayAs(JSONArray expected) {
		this.expected = expected;
	}

	@Override
	public void describeTo(Description description) {
		description.appendValue(expected.toString());
	}

	@Override
	protected boolean matchesSafely(JSONArray actual, Description mismatchDescription) {
		try {
			JSONCompareResult result = compareJSON(expected, actual, STRICT_ORDER);
			return result.passed();
		} catch (JSONException e) {
			return true;
		}
	}
	
	@Factory
	public static Matcher<? super JSONArray> sameJSONArrayAs(JSONArray expected) {
		return new SameJSONArrayAs(expected);
	}
}
