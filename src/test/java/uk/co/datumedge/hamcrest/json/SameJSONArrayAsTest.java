package uk.co.datumedge.hamcrest.json;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static uk.co.datumedge.hamcrest.json.SameJSONArrayAs.sameJSONArrayAs;
import static uk.co.datumedge.hamcrest.json.StringDescriptionAssert.assertThat;

import org.hamcrest.StringDescription;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;

public class SameJSONArrayAsTest {
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
		JSONArray actual = new JSONArray("[170]");
		SameJSONArrayAs matcher = new SameJSONArrayAs(new JSONArray("[194]"));
		matcher.matches(actual);
		matcher.describeMismatch(actual, mismatchDescription);
		assertThat(mismatchDescription, both(containsString("170")).and(containsString("194")));
	}
}
