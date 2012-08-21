package uk.co.datumedge.hamcrest.json;

import static uk.co.datumedge.hamcrest.json.JSONComparisonResult.comparisonFailed;
import static uk.co.datumedge.hamcrest.json.JSONComparisonResult.comparisonPassed;

import org.skyscreamer.jsonassert.JSONCompareResult;

final class JSONAssertComparisonResult {
	static JSONComparisonResult resultOf(JSONCompareResult result) {
		return result.passed() ? comparisonPassed() : comparisonFailed(result.getMessage());
	}
}
