package uk.co.datumedge.hamcrest.json;

import org.json.JSONException;

import java.util.Map;

/**
 * @author timp
 * @since 2014-06-27
 *
 * @param <T> Either JSONObject, JSONArray or String
 */
public class NoOpMungingCaptor<T>
    extends JSONMungingCaptorBase<T>
    implements JSONMungingCaptor<T> {

  @Override
  public T munge(T expected, T actual, Map<String, Object> captured) throws JSONException, CaptureException {
    return expected;
  }
}
