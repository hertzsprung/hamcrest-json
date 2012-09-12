package uk.co.datumedge.hamcrest.json;

import static org.skyscreamer.jsonassert.JSONCompare.compareJSON;
import static uk.co.datumedge.hamcrest.json.JSONAssertComparisonResult.resultOf;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompareMode;

final class StringComparatorFactory implements JSONAssertComparatorFactory<String> {
	private static final JSONAssertComparatorFactory<String> INSTANCE = new StringComparatorFactory();

	static JSONAssertComparatorFactory<String> stringComparison() {
		return INSTANCE;
	}

	private StringComparatorFactory() { }

	@Override
	public JSONComparator<String> comparatorWith(final JSONCompareMode compareMode) {
		return new JSONComparator<String>() {
			@Override
			public JSONComparisonResult compare(String expected, String actual) throws JSONException {
				return resultOf(compareJSON(expected, actual, compareMode));
			}
		};
	}
}
