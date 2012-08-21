package uk.co.datumedge.hamcrest.json;

import static org.skyscreamer.jsonassert.JSONCompare.compareJSON;
import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.json.JSONException;
import org.json.JSONObject;

public class SameJSONObjectAs extends TypeSafeDiagnosingMatcher<JSONObject> {
	private final JSONObject expected;

	public SameJSONObjectAs(JSONObject expected) {
		this.expected = expected;
	}
	
	@Override
	public void describeTo(Description description) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected boolean matchesSafely(JSONObject actual, Description mismatchDescription) {
		try {
			return compareJSON(expected, actual, LENIENT).passed();
		} catch (JSONException e) {
			return true;
			// TODO Auto-generated catch block
		}
	}

	@Factory
	public static Matcher<? super JSONObject> sameJSONObjectAs(JSONObject expected) {
		return new SameJSONObjectAs(expected);
	}
}
