package uk.co.datumedge.hamcrest.json;

/**
 * Allows a comparator's behaviour to be configured to allow any array ordering, or extra unexpected fields.
 */
public interface JSONModalComparator<T> extends JSONComparator<T> {
	/**
	 * @return a {@code JSONModalComparator} instance that is equivalent to this, but does not check the ordering of
	 *         array elements
	 */
	JSONModalComparator<T> butAllowingAnyArrayOrdering();

	/**
	 * @return a {@code JSONModalComparator} instance that is equivalent to this, but does allows fields to appear in
	 *         the actual JSON document that do not appear in the expected JSON document.
	 */
	JSONModalComparator<T> butAllowingExtraUnexpectedFields();
}
