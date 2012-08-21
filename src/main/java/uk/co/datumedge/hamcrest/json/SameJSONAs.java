package uk.co.datumedge.hamcrest.json;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Matcher that asserts that one JSON document is the same as another.
 * 
 * @param <T>
 *            the type of the JSON document. This is typically {@code JSONObject}, {@code JSONArray} or {@code String}.
 */
public final class SameJSONAs<T> extends TypeSafeDiagnosingMatcher<T> {
	private final T expected;
	private final JSONComparator<T> comparator;

	public SameJSONAs(T expected, JSONComparator<T> comparator) {
		this.expected = expected;
		this.comparator = comparator;
	}

	@Override
	public void describeTo(Description description) {
		description.appendValue(expected.toString());
	}

	@Override
	protected boolean matchesSafely(T actual, Description mismatchDescription) {
		try {
			JSONComparisonResult result = comparator.compare(expected, actual);
			if (result.failed()) {
				mismatchDescription.appendDescriptionOf(result);
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
	public static Matcher<? super JSONObject> sameJSONObjectAs(JSONObject expected) {
		return new SameJSONAs<JSONObject>(expected, new JSONObjectAssertComparator());
	}
	
	@Factory
	public static Matcher<? super JSONArray> sameJSONArrayAs(JSONArray expected) {
		return new SameJSONAs<JSONArray>(expected, new JSONArrayAssertComparator());
	}
	
	@Factory
	public static Matcher<? super JSONArray> sameJSONArrayAs(JSONArray expected, JSONComparator<JSONArray> jsonComparator) {
		return new SameJSONAs<JSONArray>(expected, jsonComparator);
	}
}
