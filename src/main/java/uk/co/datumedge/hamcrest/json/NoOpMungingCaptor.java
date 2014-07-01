package uk.co.datumedge.hamcrest.json;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author timp21337
 * @since 2014-06-27
 *
 * @param <T> Either JSONObject, JSONArray or String
 */
public class NoOpMungingCaptor<T>
    extends JSONMungingCaptorBase<T>
    implements JSONMungingCaptor<T> {

	public NoOpMungingCaptor() {}

	@Override
	public final Map<String, Object> getCaptured() {
		if (captured == null)
			captured = new HashMap<String, Object>();
		return captured;
	}

  @Override
  public T munge(T expected, T actual, Map<String, Object> captured) throws JSONException, CaptureException {
    return expected;
  }
}
