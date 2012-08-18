package uk.co.datumedge.hamcrest.json;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.json.JSONArray;

/**
 * Matcher that asserts that one JSONArray is the same as another.
 */
public class SameJSONArrayAs extends TypeSafeDiagnosingMatcher<JSONArray> {

	public SameJSONArrayAs(JSONArray expected) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void describeTo(Description description) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean matchesSafely(JSONArray item, Description mismatchDescription) {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Factory
	public static Matcher<? super JSONArray> sameJSONArrayAs(JSONArray expected) {
		return new SameJSONArrayAs(expected);
	}
}
