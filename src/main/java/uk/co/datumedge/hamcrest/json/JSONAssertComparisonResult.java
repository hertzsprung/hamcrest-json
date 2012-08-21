package uk.co.datumedge.hamcrest.json;

import static uk.co.datumedge.hamcrest.json.JSONComparisonResult.comparisonFailed;
import static uk.co.datumedge.hamcrest.json.JSONComparisonResult.comparisonPassed;

import org.skyscreamer.jsonassert.JSONCompareResult;

final class JSONAssertComparisonResult {
	
	static JSONComparisonResult resultOf(JSONCompareResult result) {
		if (result.isFailureOnField()) {
			return comparisonFailed(result.getField(), result.getExpected(), result.getActual());
		} else if (result.failed()) {
			return comparisonFailed(result.getMessage());
		} else {
			return comparisonPassed();
		}
	}
}
