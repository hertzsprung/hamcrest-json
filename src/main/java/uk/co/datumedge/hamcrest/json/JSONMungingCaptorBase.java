package uk.co.datumedge.hamcrest.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author timp
 * @since 2014-06-27
 *
 * @param <T> Either JSONObject, JSONArray or String
 */
public abstract class JSONMungingCaptorBase<T> implements JSONMungingCaptor<T> {

  protected static final String CAPTURE_VAR_SYNTAX = "\\+\\{(\\w+)\\}";
  protected static final String CAPTURE_START = "_captureStart_";
  protected static final String CAPTURE_END = "_captureEnd_";
  protected static final String CAPTURE_VAR_SYNTAX_ESCAPED = CAPTURE_START + "$1" + CAPTURE_END;
  protected static final String CAPTURE_VAR_CAPTOR = CAPTURE_START + "(\\w+)" + CAPTURE_END;
  protected static final String CAPTURE_VALUE_CAPTOR = "(.\\+)";
  protected static final String ESCAPED_CAPTURE_VALUE_CAPTOR = "\\(.\\+\\)";

  protected Map<String, Object> captured;

  @Override
  public final Map<String, Object> getCaptured() {
    return captured;
  }

  protected static String capture(String expectedValue,
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

  protected static Number capture(String expectedValue,
                                final Number actualValue,
                                final Map<String, Object> captured) throws CaptureException {
    List<String> capturedKeys = captureVarNames(expectedValue);
    if (capturedKeys.size() != 1) {
      throw new CaptureException("Only one capture key per Number:" + capturedKeys.toString());
    }
    captured.put(capturedKeys.get(0), actualValue);
    return actualValue;
  }

  protected static Boolean capture(String expectedValue,
                                 final Boolean actualValue,
                                 final Map<String, Object> captured) throws CaptureException {
    List<String> capturedKeys = captureVarNames(expectedValue);
    if (capturedKeys.size() != 1) {
      throw new CaptureException("Only one capture key per Boolean:" + capturedKeys.toString());
    }
    captured.put(capturedKeys.get(0), actualValue);
    return actualValue;
  }

  protected static void captureNull(String expectedValue,
                                  final Map<String, Object> captured) throws CaptureException {
    List<String> capturedKeys = captureVarNames(expectedValue);
    if (capturedKeys.size() != 1) {
      throw new CaptureException("Only one capture key per Null:" + capturedKeys.toString());
    }
    captured.put(capturedKeys.get(0), null);
  }

  protected static List<String> captureVarNames(String expectedValue) {
    Matcher capturedValueNameMatcher = Pattern.compile(CAPTURE_VAR_CAPTOR).matcher(expectedValue);
    List<String> capturedKeys = new ArrayList<String>();
    while (capturedValueNameMatcher.find()) {
      String varName = capturedValueNameMatcher.group(1);
      capturedKeys.add(varName);
    }
    return capturedKeys;
  }

  protected static String matchReplaceAndCapture(
      final String expectedStr, final String actualStr, final Map<String, Object> captured)
      throws JSONException, CaptureException {
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
    // Capturing not possible, mismatch will be picked up by normal matcher
    return expectedStr;
  }

  protected static Set<String> getKeys(JSONObject jsonObject) {
    Set<String> keys = new TreeSet<String>();
    Iterator<?> iter = jsonObject.keys();
    while (iter.hasNext()) {
      keys.add((String) iter.next());
    }
    return keys;
  }

  protected static JSONObject matchReplaceAndCaptureJSONObject(
      final JSONObject expected, final JSONObject actual, final Map<String, Object> captured)
      throws JSONException, CaptureException {
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


  protected static JSONArray matchReplaceAndCaptureJSONArray(
      final JSONArray expected, final JSONArray actual, final Map<String, Object> captured)
      throws JSONException, CaptureException {
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

}
