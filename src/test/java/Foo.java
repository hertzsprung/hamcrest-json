import java.util.ArrayList;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;

public class Foo {
	@Test
	public void test() {
		List<String> expected = new ArrayList<String>();
		expected.add("A");
		expected.add("B");
		MatcherAssert.assertThat(expected , Matchers.contains("A", "B", "C"));
	}
	
	@Test
	public void test2() throws JSONException {
		JSONCompareResult result = JSONCompare.compareJSON("[2, 1, 3]", "[1, 2, 3]", JSONCompareMode.STRICT);
		System.out.println(result.getFieldFailures());
	}
}
