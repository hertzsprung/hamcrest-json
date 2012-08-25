package uk.co.datumedge.hamcrest.json;

import static uk.co.datumedge.hamcrest.json.JSONArrayAssertComparator.actualJSONArraySameAsExpected;
import static uk.co.datumedge.hamcrest.json.JSONArrayAssertComparator.actualJSONArraySuperSetOfExpected;
import static uk.co.datumedge.hamcrest.json.JSONObjectAssertComparator.actualJSONObjectSameAsExpected;
import static uk.co.datumedge.hamcrest.json.JSONObjectAssertComparator.actualJSONObjectSuperSetOfExpected;

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
	
	public Matcher<? super T> havingAnyArrayOrdering() {
		return new SameJSONAs<T>(expected, comparator.butHavingAnyArrayOrdering());
	}

	@Factory
	public static SameJSONAs<JSONObject> sameJSONObjectAs(JSONObject expected) {
		return new SameJSONAs<JSONObject>(expected, actualJSONObjectSameAsExpected());
	}
	
	@Factory
	public static SameJSONAs<JSONObject> containsJSONObject(JSONObject expected) {
		return new SameJSONAs<JSONObject>(expected, actualJSONObjectSuperSetOfExpected());
	}

	@Factory
	public static SameJSONAs<JSONArray> sameJSONArrayAs(JSONArray expected) {
		return new SameJSONAs<JSONArray>(expected, actualJSONArraySameAsExpected());
	}

	@Factory
	public static Matcher<? super JSONArray> sameJSONArrayAs(JSONArray expected, JSONComparator<JSONArray> jsonComparator) {
		return new SameJSONAs<JSONArray>(expected, jsonComparator);
	}
	
	@Factory
	public static SameJSONAs<JSONArray> containsJSONArray(JSONArray expected) {
		return new SameJSONAs<JSONArray>(expected, actualJSONArraySuperSetOfExpected());
	}
}
