package uk.co.datumedge.hamcrest.json;

import org.skyscreamer.jsonassert.JSONCompareMode;

interface JSONAssertComparatorFactory<T> {
	JSONComparator<T> comparatorWith(JSONCompareMode compareMode);
}
