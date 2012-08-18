package uk.co.datumedge.hamcrest.json;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.StringDescription;

public class StringDescriptionAssert {
	public static void assertThat(StringDescription description, Matcher<String> matcher) {
		MatcherAssert.assertThat(description.toString(), matcher);
	}
}
