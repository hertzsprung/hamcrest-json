package uk.co.datumedge.hamcrest.json;

import static uk.co.datumedge.hamcrest.json.JSONArrayComparatorFactory.jsonArrayComparison;
import static uk.co.datumedge.hamcrest.json.JSONArrayMungingCaptor.jsonArrayMungingCaptor;
import static uk.co.datumedge.hamcrest.json.JSONAssertComparator.modalComparatorFor;
import static uk.co.datumedge.hamcrest.json.JSONObjectComparatorFactory.jsonObjectComparison;
import static uk.co.datumedge.hamcrest.json.JSONObjectMungingCaptor.jsonObjectMungingCaptor;
import static uk.co.datumedge.hamcrest.json.StringComparatorFactory.stringComparison;
import static uk.co.datumedge.hamcrest.json.StringMungingCaptor.stringMungingCaptor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Matcher that asserts that one JSON document is the same as another.
 *
 * @param <T>
 *            the type of the JSON document. This is typically {@code JSONObject}, {@code JSONArray} or {@code String}.
 */
public final class SameJSONAs<T> extends TypeSafeDiagnosingMatcher<T> {
	private final T expected;
	private final JSONModalComparator<T> comparator;
  private JSONMungingCaptor<T> captor = new NoOpMungingCaptor<T>();

	public SameJSONAs(T expected, JSONModalComparator<T> comparator) {
		this.expected = expected;
		this.comparator = comparator;
	}

	@Override
	public void describeTo(Description description) {
		description.appendValue(expected.toString());
	}

	@Override
	protected boolean matchesSafely(T actual, Description mismatchDescription) {
		try {
			JSONComparisonResult result = comparator.compare(
          captor.munge(expected, actual, captor.getCaptured()),
          actual);
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

	/**
	 * Creates a matcher that allows any element ordering within JSON arrays. For example,
	 * <code>{"fib":[0,1,1,2,3]}</code> will match <code>{"fib":[3,1,0,2,1]}</code>.
	 *
	 * @return the configured matcher
	 */
	public SameJSONAs<T> allowingAnyArrayOrdering() {
		return new SameJSONAs<T>(expected, comparator.butAllowingAnyArrayOrdering())
        .capturingTo(getCaptured());
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
	public SameJSONAs<T> allowingExtraUnexpectedFields() {
		return new SameJSONAs<T>(expected, comparator.butAllowingExtraUnexpectedFields())
        .capturingTo(getCaptured());
	}

  /**
   * @return a map of captured keys and values if capturing has been enabled
   */
  public Map<String, Object> getCaptured() {
    return captor.getCaptured();
  }

  JSONMungingCaptor<T> captorFor(Class clazz, Map<String, Object> captured) {
    if (expected instanceof JSONObject) {
      return (JSONMungingCaptor<T>) jsonObjectMungingCaptor(captured);
    } else if (expected instanceof JSONArray) {
      return (JSONMungingCaptor<T>) jsonArrayMungingCaptor(captured);
    } else {
      return (JSONMungingCaptor<T>) stringMungingCaptor(captured);
    }
  }

  /**
   * Enable capturing to a new Map.
   * @return this matcher enabled
   */
  public SameJSONAs<T> capturing() {
    return capturingTo(new HashMap<String, Object>());
  }

  /**
   * Enable capturing to the supplied Map.
   * @return this matcher enabled
   */
  public SameJSONAs<T> capturingTo(final Map<String, Object> captured) {
    captor = captorFor(expected.getClass(), captured);
    return this;
  }

	/**
	 * Creates a matcher that compares {@code JSONObject}s.
	 *
	 * @param expected the expected {@code JSONObject} instance
	 * @return the {@code Matcher} instance
	 */
	@Factory
	public static SameJSONAs<JSONObject> sameJSONObjectAs(JSONObject expected) {
		return new SameJSONAs<JSONObject>(expected, modalComparatorFor(jsonObjectComparison()));
	}

	@Factory
	public static SameJSONAs<JSONObject> sameJSONObjectAs(JSONObject expected, JSONModalComparator<JSONObject> comparator) {
		return new SameJSONAs<JSONObject>(expected, comparator);
	}

	/**
	 * Creates a matcher that compares {@code JSONArray}s.
	 *
	 * @param expected the expected {@code JSONArray} instance
	 * @return the {@code Matcher} instance
	 */
	@Factory
	public static SameJSONAs<JSONArray> sameJSONArrayAs(JSONArray expected) {
		return new SameJSONAs<JSONArray>(expected, modalComparatorFor(jsonArrayComparison()));
	}

	@Factory
	public static SameJSONAs<? super JSONArray> sameJSONArrayAs(JSONArray expected, JSONModalComparator<JSONArray> comparator) {
		return new SameJSONAs<JSONArray>(expected, comparator);
	}

	/**
	 * Creates a matcher that compares {@code JSONObject}s or {@code JSONArray}s represented as {@code String}s.
	 *
	 * @param expected the expected JSON document
	 * @return the {@code Matcher} instance
	 */
	@Factory
	public static SameJSONAs<? super String> sameJSONAs(String expected) {
		return new SameJSONAs<String>(expected, modalComparatorFor(stringComparison()));
	}
	
	@Factory
	public static SameJSONAs<? super String> sameJSONAs(String expected, JSONModalComparator<String> comparator) {
		return new SameJSONAs<String>(expected, comparator);
	}
}
