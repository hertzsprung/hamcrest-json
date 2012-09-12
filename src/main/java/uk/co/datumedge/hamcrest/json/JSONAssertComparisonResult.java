package uk.co.datumedge.hamcrest.json;

import static uk.co.datumedge.hamcrest.json.JSONComparisonResult.comparisonFailed;
import static uk.co.datumedge.hamcrest.json.JSONComparisonResult.comparisonPassed;

import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.SelfDescribing;
import org.skyscreamer.jsonassert.FieldComparisonFailure;
import org.skyscreamer.jsonassert.JSONCompareResult;

final class JSONAssertComparisonResult {
	private JSONAssertComparisonResult() { }

	static JSONComparisonResult resultOf(JSONCompareResult result) {
		if (result.isFailureOnField()) {
			return diagnose(result.getFieldFailures());
		} else if (result.failed()) {
			return comparisonFailed(result.getMessage());
		} else {
			return comparisonPassed();
		}
	}

	private static JSONComparisonResult diagnose(final List<FieldComparisonFailure> fieldFailures) {
		return new JSONComparisonResult(new SelfDescribing() {
			@Override
			public void describeTo(Description description) {
				boolean first = true;
				for (FieldComparisonFailure failure : fieldFailures) {
					if (!first) description.appendText(" and ");
					description
						.appendText("field ").appendText(failure.getField())
						.appendText(" was ").appendValue(failure.getActual())
						.appendText(" instead of ").appendValue(failure.getExpected());
					first = false;
				}
			}
		});
	}
}
