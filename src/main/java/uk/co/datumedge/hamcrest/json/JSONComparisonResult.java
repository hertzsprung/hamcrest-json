package uk.co.datumedge.hamcrest.json;

import org.hamcrest.Description;
import org.hamcrest.SelfDescribing;

/**
 * Models the result of a comparison between two JSON documents.
 */
public final class JSONComparisonResult implements SelfDescribing {
	private static final JSONComparisonResult PASSED = new JSONComparisonResult();
	private final boolean passed;
	private final SelfDescribing description;

	private JSONComparisonResult() {
		this.passed = true;
		this.description = new SelfDescribing() {
			@Override public void describeTo(Description description) {}
		};
	}

	private JSONComparisonResult(SelfDescribing description) {
		this.passed = false;
		this.description = description;
	}


	@Override
	public void describeTo(Description description) {
		description.appendDescriptionOf(this.description);
	}

	public boolean failed() {
		return !passed;
	}

	public boolean passed() {
		return passed;
	}

	static JSONComparisonResult comparisonPassed() {
		return PASSED;
	}

	static JSONComparisonResult comparisonFailed(final String message) {
		return new JSONComparisonResult(new SelfDescribing() {
			@Override
			public void describeTo(Description description) {
				description.appendText(message).toString();
			}
		});
	}
	
	static JSONComparisonResult comparisonFailed(final String field, final Object expected, final Object actual) {
		return new JSONComparisonResult(new SelfDescribing() {
			@Override
			public void describeTo(Description description) {
				description
					.appendText("was ").appendValue(actual)
					.appendText(" instead of ").appendValue(expected)
					.appendText(" at ").appendText(field)
					.toString();
			}
		});
	}
}
