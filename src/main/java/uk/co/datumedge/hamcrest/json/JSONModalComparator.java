package uk.co.datumedge.hamcrest.json;

public interface JSONModalComparator<T> extends JSONComparator<T> {
	/**
	 * @return a {@code JSONModalComparator} instance that is equivalent to this, but does not check the ordering of
	 *         array elements
	 */
	JSONModalComparator<T> butAllowingAnyArrayOrdering();

	/**
	 * @return a {@code JSONModalComparator} instance that is equivalent to this, but does allows fields to appear in
	 *         the actual JSON document that do not appear in the expected JSON document. elements
	 */
	JSONModalComparator<T> butAllowingExtraUnexpectedFields();
}
