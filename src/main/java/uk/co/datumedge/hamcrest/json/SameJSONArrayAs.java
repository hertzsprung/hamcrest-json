package uk.co.datumedge.hamcrest.json;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Matcher that asserts that one JSONArray is the same as another.
 */
public final class SameJSONArrayAs extends TypeSafeDiagnosingMatcher<JSONArray> {
	private final JSONArray expected;
	private JSONComparator<JSONArray> comparator;

	public SameJSONArrayAs(JSONArray expected) {
		this(expected, new JSONArrayAssertComparator());
	}

	public SameJSONArrayAs(JSONArray expected, JSONComparator<JSONArray> comparator) {
		this.expected = expected;
		this.comparator = comparator;
	}

	@Override
	public void describeTo(Description description) {
		description.appendValue(expected.toString());
	}

	@Override
	protected boolean matchesSafely(JSONArray actual, Description mismatchDescription) {
		try {
			JSONComparisonResult result = comparator.compare(expected, actual);
			if (result.failed()) {
				mismatchDescription.appendText(result.getFailureMessage());
			}
			return result.passed();
		} catch (JSONException e) {
			StringWriter out = new StringWriter();
			e.printStackTrace(new PrintWriter(out));
			mismatchDescription.appendText(out.toString());
			return false;
		}
	}

	@Factory
	public static Matcher<? super JSONArray> sameJSONArrayAs(JSONArray expected) {
		return new SameJSONArrayAs(expected);
	}
	
	@Factory
	public static Matcher<? super JSONArray> sameJSONArrayAs(JSONArray expected, JSONComparator<JSONArray> jsonComparator) {
		return new SameJSONArrayAs(expected, jsonComparator);
	}
}
