package uk.co.datumedge.hamcrest.json;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.co.datumedge.hamcrest.json.SameJSONObjectAs.sameJSONObjectAs;

import org.json.JSONObject;
import org.junit.Test;

public class SameJSONObjectAsTest {
	@Test public void matchesEmptyJSONObjects() {
		assertThat(new JSONObject(), is(sameJSONObjectAs(new JSONObject())));
	}
}
