package uk.co.datumedge.hamcrest.json;

import org.json.JSONException;

import java.util.Map;

/**
 * Interpolate a template against an instance, capturing the matches and returning the interpolation.
 *
 * @param <T> the document type, typically {@code JSONObject} or {@code JSONArray}.
 *
 * @author timp
 * @since 2014-06-26
 */
public interface JSONMungingCaptor<T> {
  Map<String, Object> getCaptured();

  T munge(T expected, T actual, Map<String, Object> captured) throws JSONException, CaptureException;

}
