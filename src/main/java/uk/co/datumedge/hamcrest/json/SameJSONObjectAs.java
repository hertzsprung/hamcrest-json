package uk.co.datumedge.hamcrest.json;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.json.JSONObject;

public class SameJSONObjectAs extends TypeSafeDiagnosingMatcher<JSONObject> {

	public SameJSONObjectAs(JSONObject expected) {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void describeTo(Description description) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected boolean matchesSafely(JSONObject item, Description mismatchDescription) {
		// TODO Auto-generated method stub
		return true;
	}

	@Factory
	public static Matcher<? super JSONObject> sameJSONObjectAs(JSONObject expected) {
		return new SameJSONObjectAs(expected);
	}
}
