package uk.co.datumedge.hamcrest.json;

import org.hamcrest.StringDescription;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.both;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static uk.co.datumedge.hamcrest.json.JSONArrayComparatorFactory.jsonArrayComparison;
import static uk.co.datumedge.hamcrest.json.JSONAssertComparator.modalComparatorFor;
import static uk.co.datumedge.hamcrest.json.JSONObjectComparatorFactory.jsonObjectComparison;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;
import static uk.co.datumedge.hamcrest.json.SameJSONAsCapturing.sameJSONArrayAsCapturing;
import static uk.co.datumedge.hamcrest.json.SameJSONAsCapturing.sameJSONAsCapturing;
import static uk.co.datumedge.hamcrest.json.SameJSONAsCapturing.sameJSONObjectAsCapturing;
import static uk.co.datumedge.hamcrest.json.StringComparatorFactory.stringComparison;
import static uk.co.datumedge.hamcrest.json.StringDescriptionAssert.assertThat;

public class SameJSONAsCapturingTest {
	private static final String EXCEPTION_MESSAGE = "exception message";
	private final JSONArray actual = new JSONArray("[13, 85]");
	private final JSONArray expected = new JSONArray("[42, 63]");

	@Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
	@SuppressWarnings("unchecked")
  private final JSONModalComparator<JSONArray> jsonComparator = context.mock(JSONModalComparator.class);

	public SameJSONAsCapturingTest() throws JSONException {
	}

  @Test
  public void matchesEmptyJSONArray() {
    assertThat(new JSONArray(), is(sameJSONArrayAsCapturing(new JSONArray())));
    assertThat(0, is(sameJSONArrayAsCapturing(new JSONArray()).getCaptured().size()));
  }

  @Test
  public void doesNotMatchOneEmptyAndOneNonEmptyJSONArray() throws JSONException {
		assertThat(new JSONArray(), is(not(sameJSONArrayAsCapturing(new JSONArray("[263]")))));
	}

	@Test
  public void appendsExpectedStringValueToDescription() throws JSONException {
		StringDescription description = new StringDescription();
		sameJSONArrayAsCapturing(new JSONArray("[831]")).describeTo(description);
		assertThat(description.toString(), is("\"[831]\""));
	}

	@Test
  public void appendsFieldMismatchToMismatchDescription() throws JSONException {
		SameJSONAsCapturing<JSONArray> matcher = new SameJSONAsCapturing<JSONArray>(expected, modalComparatorFor(jsonArrayComparison()));
		assertThat(mismatchDescriptionFor(actual, matcher), both(
				containsString("13")).and(containsString("42")).and(containsString("85")).and(containsString("63")));
	}

	@Test
  public void appendsMissingKeyToMismatchDescription() throws JSONException {
		assertThat(mismatchDescriptionFor("{}", sameJSONAsCapturing("{\"missingKey\": 6}")), containsString("missingKey"));
	}
	
	@Test
  public void appendsFailureStringToMismatchDescription() throws JSONException {
		assertThat(mismatchDescriptionFor("{\"myField\":[3]}", sameJSONAsCapturing("{\"myField\": 5}")), is(not("")));
	}

	private <T> StringDescription mismatchDescriptionFor(T actual, SameJSONAsCapturing<? super T> matcher) {
		StringDescription mismatchDescription = new StringDescription();
		matcher.matches(actual);
		matcher.describeMismatch(actual, mismatchDescription);
		return mismatchDescription;
	}

	@Test
  public void doesNotMatchWhenJSONExceptionIsCaught() throws JSONException {
		allowingJSONComparatorToThrowJSONException();
		
		assertThat(actual, is(not(sameJSONArrayAsCapturing(expected, jsonComparator, new HashMap<String, Object>()))));
	}

	@Test
  public void appendsJSONExceptionStacktraceToMismatchDescription() throws JSONException {
		allowingJSONComparatorToThrowJSONException();
		
		StringDescription mismatchDescription = new StringDescription();
		SameJSONAs<JSONArray> matcher = new SameJSONAs<JSONArray>(expected, jsonComparator);
		matcher.matches(actual);
		matcher.describeMismatch(actual, mismatchDescription);
		assertThat(mismatchDescription, both(containsString(EXCEPTION_MESSAGE)).and(containsString(SameJSONAs.class.getName())));
	}

	@Test
  public void doesNotMatchWhenActualJSONHasExtraFields() throws JSONException {
		assertThat(new JSONArray("[{'a':3, 'b':5}, 2]"), is(not(sameJSONArrayAsCapturing(new JSONArray("[{'a':3}, 2]")))));
	}

	@Test
  public void matchesWithAdditionalFieldsInActualJSONArray() throws JSONException {
		assertThat(new JSONArray("[{\"foo\":6, \"bar\":1}, 2]"), sameJSONArrayAsCapturing(new JSONArray("[{\"bar\":1}, 2]")).allowingExtraUnexpectedFields());
	}

	@Test
  public void matchesJSONArrayHavingElementsInAnyOrder() throws JSONException {
		assertThat(new JSONArray("[1, 5, 2]"), sameJSONArrayAsCapturing(new JSONArray("[5, 2, 1]")).allowingAnyArrayOrdering());
	}

	@Test
  public void doesNotMatchJSONArrayHavingElementsInAnyOrderWithAdditionalFieldsInExpectedJSONObject() throws JSONException {
		assertThat(
				new JSONArray("[{\"b\":3, \"arr\":[1, 5, 2]}]"),
				not(sameJSONArrayAsCapturing(new JSONArray("[{\"arr\":[5, 2, 1]}]"),
            new HashMap<String, Object>()).allowingAnyArrayOrdering()));
	}

	@Test
  public void matchesJSONArrayWithAdditionalFieldsInExpectedJSONArrayHavingElementsInAnyOrder() throws JSONException {
		assertThat(
				new JSONArray("[{\"b\":3, \"arr\":[1, 5, 2]}]"),
				sameJSONArrayAsCapturing(new JSONArray("[{\"arr\":[5, 2, 1]}]"))
					.allowingAnyArrayOrdering()
					.allowingExtraUnexpectedFields());
	}

	@Test
  public void matchesEmptyJSONObjects() {
		assertThat(new JSONObject(), is(sameJSONObjectAsCapturing(new JSONObject(), modalComparatorFor(jsonObjectComparison()), new HashMap<String, Object>())));
	}

	@Test
  public void doesNotMatchOneEmptyAndOneNonEmptyJSONObject() throws JSONException {
		assertThat(new JSONObject(), is(not(sameJSONObjectAsCapturing(new JSONObject("{\"foo\":3}")))));
	}

	@Test
  public void matchesWithAdditionalFieldsInActualJSONObject() throws JSONException {
		assertThat(new JSONObject("{\"a\":3,\"b\":7}"), sameJSONObjectAsCapturing(new JSONObject("{\"b\":7}")).allowingExtraUnexpectedFields());
	}

	@Test
  public void doesNotMatchWithAdditionalFieldsInExpectedJSONObject() throws JSONException {
		assertThat(new JSONObject("{\"b\":7}"), not(sameJSONObjectAsCapturing(new JSONObject("{\"a\":3,\"b\":7}")).allowingExtraUnexpectedFields()));
	}

	@Test
  public void matchesJSONObjectHavingArrayElementsInAnyOrder() throws JSONException {
		assertThat(
				new JSONObject("{\"arr\":[1, 5, 2]}"),
				sameJSONObjectAsCapturing(new JSONObject("{\"arr\":[5, 2, 1]}")).allowingAnyArrayOrdering());
	}

	@Test
  public void doesNotMatchJSONObjectHavingArrayElementsInAnyOrderWithAdditionalFieldsInExpectedJSONObject() throws JSONException {
		assertThat(
				new JSONObject("{\"b\":3, \"arr\":[1, 5, 2]}"),
				not(sameJSONObjectAsCapturing(new JSONObject("{\"arr\":[5, 2, 1]}")).allowingAnyArrayOrdering()));
	}

	@Test
  public void matchesJSONObjectWithAdditionalFieldsInExpectedJSONObjectAndHavingArrayElementsInAnyOrder() throws JSONException {
		assertThat(
				new JSONObject("{\"b\":3, \"arr\":[1, 5, 2]}"),
				sameJSONObjectAsCapturing(new JSONObject("{\"arr\":[5, 2, 1]}"))
					.allowingExtraUnexpectedFields()
					.allowingAnyArrayOrdering());
	}

	@Test
  public void matchesJSONObjectsAsStrings() {
		assertThat("{\"foo\": 5}", sameJSONAsCapturing("{\"foo\": 5}", modalComparatorFor(stringComparison()), new HashMap<String, Object>()));
	}

  @Test
  public void doesNotMatchJSONArrayAndJSONObject() {
    assertThat("{\"foo\": 5}", not(sameJSONAsCapturing("[5, 2]")));
  }

	private void allowingJSONComparatorToThrowJSONException() throws JSONException {
		context.checking(new Expectations() {{
			allowing(jsonComparator).compare(expected, actual); will(throwException(new JSONException(EXCEPTION_MESSAGE)));
		}});
	}

  // Capturing

  @Test
  public void capturesStringValue() {
    SameJSONAsCapturing<String> matched = sameJSONAsCapturing("{\"foo\": +{foo_val}}");
    assertThat("{\"foo\": \"5\"}", matched);
    Map<String, Object> captured =  matched.getCaptured();
    assertEquals(1, captured.size());
    assertThat("5", is(captured.get("foo_val")));
  }

  @Test
  public void capturesNumberValue() {
    SameJSONAsCapturing<String> matched = sameJSONAsCapturing("{\"foo\": +{foo_val}}");
    assertThat("{\"foo\": 5}", matched);
    Map<String, Object> captured =  matched.getCaptured();
    assertEquals(1, captured.size());
    assertEquals(5, captured.get("foo_val"));
  }

  @Test
  public void numberCanOnlyBeCapturedOnce() {
    SameJSONAsCapturing<String> matched = sameJSONAsCapturing("{\"foo\": \"+{foo1} +{foo2}\"}");
    assertThat("{\"foo\": 7.7}", not(matched));
    Map<String, Object> captured =  matched.getCaptured();
    assertEquals(0, captured.size());
  }

  @Test
  public void capturesBooleanValue() {
    SameJSONAsCapturing<String> matched = sameJSONAsCapturing("{\"foo\": +{foo_val}}");
    assertThat("{\"foo\": true}", matched);
    Map<String, Object> captured =  matched.getCaptured();
    assertEquals(1, captured.size());
    assertEquals(Boolean.TRUE, captured.get("foo_val"));
  }

  @Test
  public void booleanCanOnlyBeCapturedOnce() {
    SameJSONAsCapturing<String> matched = sameJSONAsCapturing("{\"foo\": \"+{foo1} +{foo2}\"}");
    assertThat("{\"foo\": true}", not(matched));
    Map<String, Object> captured =  matched.getCaptured();
    assertEquals(0, captured.size());
  }

  @Test
  public void capturesNullValue() {
    SameJSONAsCapturing<String> matched = sameJSONAsCapturing("{\"foo\": +{foo_val}}");
    assertThat("{\"foo\": null}", matched);
    Map<String, Object> captured =  matched.getCaptured();
    assertEquals(1, captured.size());
    assertNull(captured.get("foo_val"));
  }

  @Test
  public void nullCanOnlyBeCapturedOnce() {
    SameJSONAsCapturing<String> matched = sameJSONAsCapturing("{\"foo\": \"+{foo1} +{foo2}\"}");
    assertThat("{\"foo\": null}", not(matched));
    Map<String, Object> captured =  matched.getCaptured();
    assertEquals(0, captured.size());
  }

  @Test
  public void captureTwiceFromSameString() throws Exception {
    String template = "{\"text\":\"fixed text +{foo} txet dexif +{bar}\"}";
    String instance    = "{\"text\":\"fixed text bar txet dexif foo\"}";
    SameJSONAsCapturing<String> it = SameJSONAsCapturing.sameJSONAsCapturing(template, new HashMap<String, Object>());
    it.matches(instance);
    assertThat((String) it.getCaptured().get("bar"), is("foo"));
    assertThat((String) it.getCaptured().get("foo"), is("bar"));
  }

  @Test
  public void captureFromNestedList() throws Exception {
    String template = "{\"errors\":[{\"error\":\"+{message}\",\"errorCode\":\"+{code}\"}]}";
    String instance =    "{\"errors\":[{\"error\":\"The method you requested does not exist.\",\"errorCode\":\"007\"}]}";
    SameJSONAsCapturing<String> it = SameJSONAsCapturing.sameJSONAsCapturing(template, new HashMap<String, Object>());
    it.matches(instance);
    assertThat((String)it.getCaptured().get("message"), is("The method you requested does not exist."));
    assertThat("007", is(it.getCaptured().get("code")));
  }

  @Test
  public void captureFromNestedObject() throws Exception {
    String template = "{\"a\":{\"b\":\"+{message}\",\"errorCode\":\"+{code}\"}}";
    String instance =    "{\"a\":{\"b\":\"The method you requested does not exist.\",\"errorCode\":\"007\"}}";
    SameJSONAsCapturing<String> it = SameJSONAsCapturing.sameJSONAsCapturing(template, new HashMap<String, Object>());
    it.matches(instance);
    assertThat((String)it.getCaptured().get("message"), is("The method you requested does not exist."));
    assertThat("007", is(it.getCaptured().get("code")));
  }

  @Test
  public void capturingJSONObjectFails() throws Exception {
    HashMap<String, Object> map = new HashMap<String, Object>();
    assertThat("{\"foo\":{\"inner\":\"bar\"}}",
        not(sameJSONAsCapturing("{\"foo\":\"+{bar}\"}",
            map)));
    assertEquals(0, map.size());
  }

  @Test
  public void multipleNonCapturingElements() {
    SameJSONAsCapturing<String> matched = sameJSONAsCapturing("{\"foo\":\"foo\", \"bar\":\"bar\"}");
    assertThat("{\"foo\":\"foo\", \"bar\":\"bar\"}", matched);
    Map<String, Object> captured =  matched.getCaptured();
    assertEquals(0, captured.size());
  }

  @Test
  public void nonMatchingJSONArray() throws Exception {
    HashMap<String, Object> map = new HashMap<String, Object>();
    assertThat("[]", sameJSONAsCapturing("[]", map));
    assertThat("[\"foo\",\"bar\"]",
            sameJSONAsCapturing(
                "[\"foo\",\"bar\"]",
                map));
  }

  @Test
  public void capturingJSONObjectString() throws Exception {
    HashMap<String, Object> map = new HashMap<String, Object>();
    assertThat("{\"foo\":\"bar\"}",
        sameJSONAsCapturing("{\"foo\":\"+{foo}\"}", map));
    assertEquals(1, map.size());
    assertEquals("bar", map.get("foo"));
  }

  @Test
  public void fallThroughJSONObjectJSONArray() {
    HashMap<String, Object> map = new HashMap<String, Object>();
    assertThat("[\"foo\",\"bar\"]",
        not(sameJSONAsCapturing("{\"foo\":\"+{foo}\"}", map)));
    assertEquals(0, map.size());
  }

  @Test
  public void craftedToFallThroughIf() throws Exception {
    HashMap<String, Object> map = new HashMap<String, Object>();
    assertThat("{\"foo\":\"miss\"}",
        not(sameJSONAsCapturing(
            "{\"foo\":\"_captureStart_\"}",
            map)));
    assertEquals(0, map.size());
  }

  @Test
  public void capturingJSONArrayString() throws Exception {
    HashMap<String, Object> map = new HashMap<String, Object>();
    assertThat("[\"foo\",\"bar\"]", sameJSONAsCapturing(
        "[\"+{foo}\",\"bar\"]",
        map));
    assertEquals(1, map.size());
    assertEquals("foo", map.get("foo"));
  }

  @Test
  public void capturingJSONArrayNumber() throws Exception {
    HashMap<String, Object> map = new HashMap<String, Object>();
    assertThat("[5.4,\"bar\"]", sameJSONAsCapturing(
        "[\"+{foo}\",\"bar\"]",
        map));
    assertEquals(1, map.size());
    assertEquals(5.4, map.get("foo"));
  }

  @Test
  public void capturingJSONArrayBoolean() throws Exception {
    HashMap<String, Object> map = new HashMap<String, Object>();
    assertThat("[true,\"bar\"]", sameJSONAsCapturing(
        "[\"+{foo}\",\"bar\"]",
        map));
    assertEquals(1, map.size());
    assertEquals(true, map.get("foo"));
  }

  @Test
  public void capturingJSONArrayNull() throws Exception {
    HashMap<String, Object> map = new HashMap<String, Object>();
    assertThat("[null,\"bar\"]", sameJSONAsCapturing(
        "[\"+{foo}\",\"bar\"]",
        map));
    assertEquals(1, map.size());
    assertNull(map.get("foo"));
  }

  @Test
  public void capturingJSONArrayFails() throws Exception {
    HashMap<String, Object> map = new HashMap<String, Object>();
    assertThat("[\"foo\",[\"e1\",\"e2\"]]",
        not(sameJSONAsCapturing("[\"foo\",\"+{bar}\"]",
            map)));
    assertEquals(0, map.size());
  }
  @Test
  public void capturingJSONArrayJSONArray() throws Exception {
    HashMap<String, Object> map = new HashMap<String, Object>();
    assertThat("[\"foo\",[\"e1\",\"e2\"]]",
        sameJSONAsCapturing("[\"foo\",[\"e1\",\"+{bar}\"]]",
            map));
    assertEquals(1, map.size());
    assertEquals("e2", map.get("bar"));
  }

  @Test
  public void fallThroughJSONArrayJSONObject() {
    HashMap<String, Object> map = new HashMap<String, Object>();
    assertThat("{\"foo\":[\"e1\",\"e2\"]}",
        not(sameJSONAsCapturing("[\"foo\",[\"e1\",\"+{bar}\"]]",
            map)));
    assertEquals(0, map.size());
  }

  @Test
  public void fallThroughStringString() {
    HashMap<String, Object> map = new HashMap<String, Object>();
    assertThat("bar",
        not(sameJSONAsCapturing("foo",
            map)));
    assertEquals(0, map.size());
  }

  @Test
  public void matchingTwiceJSONObject() throws Exception {
    HashMap<String, Object> map = new HashMap<String, Object>();
    assertThat("{\"foo\":\"bar also stool\"}",
            sameJSONAsCapturing("{\"foo\":\"+{foo} also +{second}\"}", map));
    assertEquals(2, map.size());
    assertEquals("bar", map.get("foo"));
    assertEquals("stool", map.get("second"));
  }

  @Test
  public void matchingMiddle() throws Exception {
    HashMap<String, Object> map = new HashMap<String, Object>();
    assertThat("{\"foo\":\"bar also stool more text\"}",
        sameJSONAsCapturing(
            "{\"foo\":\"+{foo} also +{second} text\"}",
            map));
    assertEquals(2, map.size());
    assertEquals("bar", map.get("foo"));
    assertEquals("stool more", map.get("second"));
  }

  @Test
  public void testMatchingTwiceJSONArray() throws Exception {
    HashMap<String, Object> map = new HashMap<String, Object>();
    assertThat("[\"foo also mad\", \"bar\",]", 
       sameJSONAsCapturing("[\"+{foo} also +{mad}\",\"bar\"]", map));
    assertEquals(2, map.size());
    assertEquals("foo", map.get("foo"));
    assertEquals("mad", map.get("mad"));
  }


  @Test
  public void capturingUnorderedArray() {
    SameJSONAsCapturing<String> matcher = sameJSONAsCapturing("[52, +{second}, 16]}")
        .allowingExtraUnexpectedFields()
        .allowingAnyArrayOrdering();
    assertThat(
        "[16, 23, 52]",
        matcher);

    matcher = sameJSONAsCapturing("[16, +{second}, 52]}")
        .allowingExtraUnexpectedFields()
        .allowingAnyArrayOrdering();
    assertThat(
        "[16, 23, 52]",
        matcher);
  }

  @Test
  public void testReadmeExample() {
    Map<String, Object> captured = new HashMap<String, Object>();
    assertThat(
        "{\"id\": 445, \"age\":53, \"gender\":\"M\", \"friend_ids\":[16, 52, 23]}",
        sameJSONAsCapturing("{\"age\": +{age}, \"gender\": +{gender}, \"friend_ids\":[52, 23, 16]}", captured)
            .allowingExtraUnexpectedFields()
            .allowingAnyArrayOrdering());
    assertThat(53, is(captured.get("age")));
    assertThat("M", is(captured.get("gender")));
  }

}
