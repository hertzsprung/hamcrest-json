package uk.co.datumedge.hamcrest.json;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONParser;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.co.datumedge.hamcrest.json.JSONArrayComparatorFactory.jsonArrayComparison;
import static uk.co.datumedge.hamcrest.json.JSONAssertComparator.modalComparatorFor;
import static uk.co.datumedge.hamcrest.json.JSONObjectComparatorFactory.jsonObjectComparison;
import static uk.co.datumedge.hamcrest.json.StringComparatorFactory.stringComparison;

/**
 * Matcher that first captures variables from the input JSON document then
 * asserts that the interpolated capturing template is the same as the input.
 *
 * @param <T>
 *            the type of the JSON document.
 *            This is typically {@code JSONObject}, {@code JSONArray} or {@code String}.
 */
public final class SameJSONAsCapturing<T> extends TypeSafeDiagnosingMatcher<T> {
  private final T expected;
	private final JSONModalComparator<T> comparator;
  private Map<String, Object> captured;

  private static final String CAPTURE_VAR_SYNTAX = "\\+\\{(\\w+)\\}";
  private static final String CAPTURE_START = "_captureStart_";
  private static final String CAPTURE_END = "_captureEnd_";
  private static final String CAPTURE_VAR_SYNTAX_ESCAPED = CAPTURE_START + "$1" + CAPTURE_END;
  private static final String CAPTURE_VAR_CAPTOR = CAPTURE_START + "(\\w+)" + CAPTURE_END;
  private static final String CAPTURE_VALUE_CAPTOR = "(.\\+)";
  private static final String ESCAPED_CAPTURE_VALUE_CAPTOR = "\\(.\\+\\)";

  public SameJSONAsCapturing(T expected, JSONModalComparator<T> comparator, Map<String, Object> captured) {
    this.expected = expected;
    this.comparator = comparator;
    this.captured = captured;
  }

  public SameJSONAsCapturing(T expected, JSONModalComparator<T> comparator) {
    this.expected = expected;
    this.comparator = comparator;
    this.captured = new HashMap<String, Object>();
  }

  @Override
	public void describeTo(Description description) {
		description.appendValue(expected.toString());
	}

	@Override
  @SuppressWarnings("unchecked")
	protected boolean matchesSafely(T actual, Description mismatchDescription) {
    try {
      T munged;
      if (actual instanceof JSONObject) {
        munged = (T) matchReplaceAndCaptureJSONObject((JSONObject) expected, (JSONObject) actual, captured);
      } else if (actual instanceof JSONArray) {
        munged = (T) matchReplaceAndCaptureJSONArray((JSONArray) expected, (JSONArray) actual, captured);
      } else {
        munged = (T) matchReplaceAndCapture((String) expected, (String) actual, captured);
      }
			JSONComparisonResult result = comparator.compare(munged, actual);
			if (result.failed()) {
				mismatchDescription.appendDescriptionOf(result);
			}
			return result.passed();
    } catch (CaptureException e) {
      mismatchDescription.appendText(e.getMessage());
      return false;
    } catch (JSONException e) {
			StringWriter out = new StringWriter();
			e.printStackTrace(new PrintWriter(out));
      mismatchDescription.appendText(out.toString());
      return false;
		}
	}

  private static Set<String> getKeys(JSONObject jsonObject) {
    Set<String> keys = new TreeSet<String>();
    Iterator<?> iter = jsonObject.keys();
    while (iter.hasNext()) {
      keys.add((String) iter.next());
    }
    return keys;
  }

  private static String matchReplaceAndCapture(
      final String expectedStr, final String actualStr, final Map<String, Object> captured)
      throws JSONException {
    Object expected = JSONParser.parseJSON(expectedStr.replaceAll(CAPTURE_VAR_SYNTAX, CAPTURE_VAR_SYNTAX_ESCAPED));
    Object actual = JSONParser.parseJSON(actualStr.replaceAll(CAPTURE_VAR_SYNTAX, CAPTURE_VAR_SYNTAX_ESCAPED));
    if (expected instanceof JSONObject) {
      if (actual instanceof JSONObject) {
        return matchReplaceAndCaptureJSONObject((JSONObject) expected, (JSONObject) actual, captured).toString();
      }
    } else { // will be instanceof JSONArray
      if (actual instanceof JSONArray) {
        return matchReplaceAndCaptureJSONArray((JSONArray) expected, (JSONArray) actual, captured).toString();
      }
    }
    // Capturing not possible, mismatch will be picked up
    return expectedStr;
  }

  private static JSONObject matchReplaceAndCaptureJSONObject(
      final JSONObject expected, final JSONObject actual, final Map<String, Object> captured)
      throws JSONException {
    Set<String> expectedKeys = getKeys(expected);
    for (String expectedKey : expectedKeys) {
      Object expectedValue = expected.get(expectedKey);
      if (actual.has(expectedKey)) {
        Object actualValue = actual.get(expectedKey);
        if (expectedValue instanceof String) {
          if (((String) expectedValue).contains(CAPTURE_START)) {
            if (actualValue instanceof String) {
              expected.put(expectedKey, capture((String) expectedValue, (String) actualValue, captured));
            } else if (actualValue instanceof Number) {
              expected.put(expectedKey, capture((String) expectedValue, (Number) actualValue, captured));
            } else if (actualValue instanceof Boolean) {
              expected.put(expectedKey, capture((String) expectedValue, (Boolean) actualValue, captured));
            } else if (actualValue.equals(JSONObject.NULL)) {
              captureNull((String) expectedValue, captured);
              expected.putOpt(expectedKey, JSONObject.NULL);
            }
          }
        } else if (expectedValue instanceof JSONObject) {
          expected.put(expectedKey, matchReplaceAndCaptureJSONObject(
              (JSONObject) expectedValue, (JSONObject) actualValue, captured));
        } else if (expectedValue instanceof JSONArray) {
          expected.put(expectedKey, matchReplaceAndCaptureJSONArray(
              (JSONArray) expectedValue, (JSONArray) actualValue, captured));
        }
      }
    }
    return expected;
  }


  private static JSONArray matchReplaceAndCaptureJSONArray(
      final JSONArray expected, final JSONArray actual, final Map<String, Object> captured)
      throws JSONException {
    for (int i = 0; i < expected.length(); i++) {
      Object expectedValue = expected.get(i);
      Object actualValue = actual.get(i);
      if (expectedValue instanceof String &&
          ((String) expectedValue).contains(CAPTURE_START)) {

        if (actualValue instanceof String) {
          expected.put(i, capture((String) expectedValue, (String) actualValue, captured));
        } else if (actualValue instanceof Number) {
          expected.put(i, capture((String) expectedValue, (Number) actualValue, captured));
        } else if (actualValue instanceof Boolean) {
          expected.put(i, capture((String) expectedValue, (Boolean) actualValue, captured));
        } else if (actualValue.equals(JSONObject.NULL)) {
          captureNull((String) expectedValue, captured);
          expected.put(i, JSONObject.NULL);
        }
      } else if (expectedValue instanceof JSONObject) {
        expected.put(i, matchReplaceAndCaptureJSONObject(
            (JSONObject) expectedValue, (JSONObject) actualValue, captured));
      } else if (expectedValue instanceof JSONArray) {
        expected.put(i, matchReplaceAndCaptureJSONArray(
            (JSONArray) expectedValue, (JSONArray) actualValue, captured));
      }
    }
    return expected;
  }

  private static String capture(String expectedValue,
                                final String actualValue,
                                final Map<String, Object> captured) {
    Matcher capturedValueNameMatcher = Pattern.compile(CAPTURE_VAR_CAPTOR).matcher(expectedValue);
    List<String> capturedKeys = new ArrayList<String>();
    while (capturedValueNameMatcher.find()) {
      String varName = capturedValueNameMatcher.group(1);
      capturedKeys.add(varName);
      expectedValue = expectedValue.replaceFirst(CAPTURE_START + varName + CAPTURE_END, "VAR_CAPTURE_PLACEHOLDER");
    }
    expectedValue = expectedValue.replaceAll("VAR_CAPTURE_PLACEHOLDER", CAPTURE_VALUE_CAPTOR);
    Pattern p = Pattern.compile(expectedValue);
    Matcher orderedMatcher = p.matcher(actualValue);
    String replaced = expectedValue;
    if (orderedMatcher.find()) {
      int index = 0;
      for (String capturedKey : capturedKeys) {
        index++;
        String value = orderedMatcher.group(index);
        captured.put(capturedKey, value);
        replaced = replaced.replaceFirst(ESCAPED_CAPTURE_VALUE_CAPTOR, value);
      }
    }
    return replaced;
  }

  private static Number capture(String expectedValue,
                                final Number actualValue,
                                final Map<String, Object> captured) {
    List<String> capturedKeys = captureVarNames(expectedValue);
    if (capturedKeys.size() != 1) {
      throw new CaptureException("Only one capture key per Number:" + capturedKeys.toString());
    }
    captured.put(capturedKeys.get(0), actualValue);
    return actualValue;
  }

  private static Boolean capture(String expectedValue,
                                 final Boolean actualValue,
                                 final Map<String, Object> captured) {
    List<String> capturedKeys = captureVarNames(expectedValue);
    if (capturedKeys.size() != 1) {
      throw new CaptureException("Only one capture key per Boolean:" + capturedKeys.toString());
    }
    captured.put(capturedKeys.get(0), actualValue);
    return actualValue;
  }

  private static void captureNull(String expectedValue,
                                 final Map<String, Object> captured) {
    List<String> capturedKeys = captureVarNames(expectedValue);
    if (capturedKeys.size() != 1) {
      throw new CaptureException("Only one capture key per Null:" + capturedKeys.toString());
    }
    captured.put(capturedKeys.get(0), null);
  }

  private static List<String> captureVarNames(String expectedValue) {
    Matcher capturedValueNameMatcher = Pattern.compile(CAPTURE_VAR_CAPTOR).matcher(expectedValue);
    List<String> capturedKeys = new ArrayList<String>();
    while (capturedValueNameMatcher.find()) {
      String varName = capturedValueNameMatcher.group(1);
      capturedKeys.add(varName);
    }
    return capturedKeys;
  }


  /**
   * Returns a map of any key/value pairs captured.
   */
  public Map<String, Object> getCaptured() {
    return captured;
  }

	/**
	 * Creates a matcher that allows any element ordering within JSON arrays. For example,
	 * <code>{"fib":[0,1,1,2,3]}</code> will match <code>{"fib":[3,1,0,2,1]}</code>.
	 *
	 * @return the configured matcher
	 */
	public SameJSONAsCapturing<T> allowingAnyArrayOrdering() {
		return new SameJSONAsCapturing<T>(expected, comparator.butAllowingAnyArrayOrdering(), captured);
	}

	/**
	 * Creates a matcher that allows fields not present in the expected JSON document.  For example, if the expected
	 * document is
<pre>{
    "name" : "John Smith",
    "address" : {
        "street" : "29 Acacia Road"
    }
}</pre>
	 * then the following document will match:
<pre>{
    "name" : "John Smith",
    "age" : 34,
    "address" : {
        "street" : "29 Acacia Road",
        "city" : "Huddersfield"
    }
}</pre>
	 *
	 * All array elements must exist in both documents, so the expected document
<pre>[
    { "name" : "John Smith" }
]</pre>
	 *  will not match the actual document
<pre>[
    { "name" : "John Smith" },
    { "name" : "Bob Jones" }
]</pre>
	 *
	 * @return the configured matcher
	 */
	public SameJSONAsCapturing<T> allowingExtraUnexpectedFields() {
		return new SameJSONAsCapturing<T>(expected, comparator.butAllowingExtraUnexpectedFields(), captured);
	}

	/**
	 * Creates a matcher that compares {@code JSONObject}s.
	 *
	 * @param expected the expected {@code JSONObject} instance
	 * @return the {@code Matcher} instance
	 */
  @Factory
  public static SameJSONAsCapturing<JSONObject> sameJSONObjectAsCapturing(
      JSONObject expected) {
    return sameJSONObjectAsCapturing(expected, new HashMap<String, Object>());
  }

  @Factory
  public static SameJSONAsCapturing<JSONObject> sameJSONObjectAsCapturing(
      JSONObject expected, Map<String, Object> map) {
    return sameJSONObjectAsCapturing(expected, modalComparatorFor(jsonObjectComparison()), map);
  }

  @Factory
	public static SameJSONAsCapturing<JSONObject> sameJSONObjectAsCapturing(
      JSONObject expected, JSONModalComparator<JSONObject> comparator, Map<String, Object> map) {
		return new SameJSONAsCapturing<JSONObject>(expected, comparator, map);
	}

	/**
	 * Creates a matcher that compares {@code JSONArray}s.
	 *
	 * @param expected the expected {@code JSONArray} instance
	 * @return the {@code Matcher} instance
	 */
  @Factory
  public static SameJSONAsCapturing<JSONArray> sameJSONArrayAsCapturing(
      JSONArray expected) {
    return sameJSONArrayAsCapturing(expected, new HashMap<String, Object>());
  }

	@Factory
  public static SameJSONAsCapturing<JSONArray> sameJSONArrayAsCapturing(
      JSONArray expected, Map<String, Object> map) {
		return new SameJSONAsCapturing<JSONArray>(expected, modalComparatorFor(jsonArrayComparison()), map);
	}

	@Factory
	public static SameJSONAsCapturing<JSONArray> sameJSONArrayAsCapturing(
      JSONArray expected, JSONModalComparator<JSONArray> comparator, Map<String, Object> map) {
		return new SameJSONAsCapturing<JSONArray>(expected, comparator, map);
	}

	/**
	 * Creates a matcher that compares {@code JSONObject}s or {@code JSONArray}s
   * represented as {@code String}s.
	 *
	 * @param expected the expected JSON document
	 * @return the {@code Matcher} instance
	 */
  @Factory
  public static SameJSONAsCapturing<String> sameJSONAsCapturing(String expected) {
    return sameJSONAsCapturing(expected, new HashMap<String, Object>());
  }

  @Factory
  public static SameJSONAsCapturing<String> sameJSONAsCapturing(
      String expected, Map<String, Object> map) {
    return sameJSONAsCapturing(expected, modalComparatorFor(stringComparison()), map);
  }


  @Factory
	public static SameJSONAsCapturing<String> sameJSONAsCapturing(
      String expected, JSONModalComparator<String> comparator, Map<String, Object> map) {
		return new SameJSONAsCapturing<String>(expected, comparator, map);
	}
}
