package uk.co.datumedge.hamcrest.json;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Map;

/**
 * @author timp
 * @since 2014-06-27
 */
public class JSONArrayMungingCaptor
    extends JSONMungingCaptorBase<JSONArray>
    implements JSONMungingCaptor<JSONArray> {

  static JSONMungingCaptor<JSONArray> jsonArrayMungingCaptor(Map<String, Object> captured) {
    return new JSONArrayMungingCaptor(captured);
  }

  private JSONArrayMungingCaptor(Map<String, Object> captured) {
    this.captured = captured;
  }

  @Override
  public JSONArray munge(JSONArray expected, JSONArray actual, Map<String, Object> captured)
      throws JSONException, CaptureException {
    return matchReplaceAndCaptureJSONArray(expected, actual, captured);
  }
}
