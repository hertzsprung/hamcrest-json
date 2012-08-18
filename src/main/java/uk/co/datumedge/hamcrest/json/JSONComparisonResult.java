package uk.co.datumedge.hamcrest.json;

/**
 * Models the result of a comparison between two JSON documents.
 */
public final class JSONComparisonResult {
	private static final JSONComparisonResult PASSED = new JSONComparisonResult();
	private final boolean passed;
	private final String message;
	
	private JSONComparisonResult() {
		this.passed = true;
		this.message = "";
	}
	
	private JSONComparisonResult(String message) {
		this.passed = false;
		this.message = message;
	}

	public boolean failed() {
		return !passed;
	}

	public boolean passed() {
		return passed;
	}

	/**
	 * @return the failure message, or an empty string if the comparison passed.
	 */
	public String getFailureMessage() {
		return message;
	}

	static JSONComparisonResult comparisonPassed() {
		return PASSED;
	}

	static JSONComparisonResult comparisonFailed(String message) {
		return new JSONComparisonResult(message);
	}
}
