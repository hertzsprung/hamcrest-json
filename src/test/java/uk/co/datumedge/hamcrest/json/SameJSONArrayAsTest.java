package uk.co.datumedge.hamcrest.json;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static uk.co.datumedge.hamcrest.json.SameJSONArrayAs.sameJSONArrayAs;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;

public class SameJSONArrayAsTest {
	@Test public void matchesEmptyJSONArrays() {
		assertThat(new JSONArray(), sameJSONArrayAs(new JSONArray()));
	}
	
	@Test public void doesNotMatchOneEmptyAndOneNonEmptyJSONArray() throws JSONException {
		assertThat(new JSONArray(), not(sameJSONArrayAs(new JSONArray("[263]"))));
	}
}
