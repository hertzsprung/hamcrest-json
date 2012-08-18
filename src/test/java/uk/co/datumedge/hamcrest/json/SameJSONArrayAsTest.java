package uk.co.datumedge.hamcrest.json;

import static org.hamcrest.MatcherAssert.assertThat;
import static uk.co.datumedge.hamcrest.json.SameJSONArrayAs.sameJSONArrayAs;

import org.json.JSONArray;
import org.junit.Test;

public class SameJSONArrayAsTest {
	@Test public void matchesEmptyJSONArrays() {
		assertThat(new JSONArray(), sameJSONArrayAs(new JSONArray()));
	}
}
