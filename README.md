Hamcrest matchers for comparing JSON documents, backed by the [JSONassert library](https://github.com/skyscreamer/JSONassert).  The code is released under the [MIT license](http://www.opensource.org/licenses/mit-license.php).

Installation
============

To install from Maven Central:

```xml
<dependency>
	<groupId>uk.co.datumedge</groupId>
	<artifactId>hamcrest-json</artifactId>
	<version>0.2</version>
</dependency>
```

Usage
=====
```java
assertThat(
	"{\"age\":43, \"friend_ids\":[16, 52, 23]}",
	sameJSONAs("{\"friend_ids\":[52, 23, 16]}")
		.allowingExtraUnexpectedFields()
		.allowingAnyArrayOrdering());

Map<String, Object> captured = new HashMap<String, Object>();
assertThat(
  "{\"id\": 445, \"age\":53, \"gender\":\"M\", \"friend_ids\":[16, 52, 23]}",
  sameJSONAsCapturing("{\"age\": +{age}, \"gender\": +{gender}, \"friend_ids\":[52, 23, 16]}", captured)
		.allowingExtraUnexpectedFields()
		.allowingAnyArrayOrdering());
assertThat(53, is(captured.get("age")));
assertThat("M", is(captured.get("gender")));

```

Resources
=========
 * [hamcrest-json website](http://datumedge.co.uk/hamcrest-json/)
 * [API documentation](http://datumedge.co.uk/hamcrest-json/apidocs/index.html)