package uk.co.datumedge.hamcrest.json;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONArrayAs;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONObjectAs;
import static uk.co.datumedge.hamcrest.json.StringDescriptionAssert.assertThat;

import org.hamcrest.StringDescription;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;

public class SameJSONAsTest {
	private static final String EXCEPTION_MESSAGE = "exception message";
	private final JSONArray actual = new JSONArray("[13]");
	private final JSONArray expected = new JSONArray("[42]");
	
	@Rule public final JUnitRuleMockery context = new JUnitRuleMockery();
	@SuppressWarnings("unchecked") private final JSONComparator<JSONArray> jsonComparator = context.mock(JSONComparator.class);
	
	public SameJSONAsTest() throws JSONException {
	}
	
	@Test public void matchesEmptyJSONArrays() {
		assertThat(new JSONArray(), is(sameJSONArrayAs(new JSONArray())));
	}
	
	@Test public void doesNotMatchOneEmptyAndOneNonEmptyJSONArray() throws JSONException {
		assertThat(new JSONArray(), is(not(sameJSONArrayAs(new JSONArray("[263]")))));
	}
	
	@Test public void appendsExpectedStringValueToDescription() throws JSONException {
		StringDescription description = new StringDescription();
		sameJSONArrayAs(new JSONArray("[831]")).describeTo(description);
		assertThat(description.toString(), is("\"[831]\""));
	}
	
	@Test public void appendsTextualComparisonToMismatchDescription() throws JSONException {
		StringDescription mismatchDescription = new StringDescription();
		SameJSONAs<JSONArray> matcher = new SameJSONAs<JSONArray>(new JSONArray("[194]"), new JSONArrayAssertComparator());
		matcher.matches(actual);
		matcher.describeMismatch(actual, mismatchDescription);
		assertThat(mismatchDescription, both(containsString("13")).and(containsString("194")));
	}
	
	@Test public void doesNotMatchWhenJSONExceptionIsCaught() throws JSONException {
		allowingJSONComparatorToThrowJSONException();
		
		assertThat(actual, is(not(sameJSONArrayAs(expected, jsonComparator))));
	}
	
	@Test public void appendsJSONExceptionStacktraceToMismatchDescription() throws JSONException {
		allowingJSONComparatorToThrowJSONException();
		
		StringDescription mismatchDescription = new StringDescription();
		SameJSONAs<JSONArray> matcher = new SameJSONAs<JSONArray>(expected, jsonComparator);
		matcher.matches(actual);
		matcher.describeMismatch(actual, mismatchDescription);
		assertThat(mismatchDescription, both(containsString(EXCEPTION_MESSAGE)).and(containsString(SameJSONAs.class.getName())));
	}
	
	@Test public void doesNotMatchWhenActualJSONHasExtraFields() throws JSONException {
		assertThat(new JSONArray("[{'a':3, 'b':5}, 2]"), is(not(sameJSONArrayAs(new JSONArray("[{'a':3}, 2]")))));
	}
	
	@Test public void matchesEmptyJSONObjects() {
		assertThat(new JSONObject(), is(sameJSONObjectAs(new JSONObject())));
	}
	
	@Test public void doesNotMatchOneEmptyAndOneNonEmptyJSONObject() throws JSONException {
		assertThat(new JSONObject(), is(not(sameJSONObjectAs(new JSONObject("{\"foo\":3}")))));
	}

	private void allowingJSONComparatorToThrowJSONException() throws JSONException {
		context.checking(new Expectations() {{
			allowing(jsonComparator).compare(expected, actual); will(throwException(new JSONException(EXCEPTION_MESSAGE)));
		}});
	}
}
