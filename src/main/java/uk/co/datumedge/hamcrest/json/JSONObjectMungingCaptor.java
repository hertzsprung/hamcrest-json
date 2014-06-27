package uk.co.datumedge.hamcrest.json;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * @author timp
 * @since 2014-06-27
 */
public final class JSONObjectMungingCaptor
    extends JSONMungingCaptorBase<JSONObject>
    implements JSONMungingCaptor<JSONObject> {

  static JSONMungingCaptor<JSONObject> jsonObjectMungingCaptor(Map<String, Object> captured) {
    return new JSONObjectMungingCaptor(captured);
  }

  private JSONObjectMungingCaptor(Map<String, Object> captured) {
    this.captured = captured;
  }

  @Override
  public JSONObject munge(JSONObject expected, JSONObject actual, Map<String, Object> captured)
      throws JSONException, CaptureException {
    return matchReplaceAndCaptureJSONObject(expected, actual, captured);
  }
}
