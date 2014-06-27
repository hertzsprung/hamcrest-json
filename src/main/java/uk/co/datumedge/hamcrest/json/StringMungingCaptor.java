package uk.co.datumedge.hamcrest.json;

import org.json.JSONException;

import java.util.Map;

/**
 * @author timp
 * @since 2014-06-27
 */
public final class StringMungingCaptor
    extends JSONMungingCaptorBase<String>
    implements JSONMungingCaptor<String> {

  static JSONMungingCaptor<String> stringMungingCaptor(Map<String, Object> captured) {
    return new StringMungingCaptor(captured);
  }

  private StringMungingCaptor(Map<String, Object> captured) {
    this.captured = captured;
  }

  @Override
  public String munge(String expected, String actual, Map<String, Object> captured)
      throws JSONException, CaptureException {
    return matchReplaceAndCapture(expected, actual, captured);
  }

}
