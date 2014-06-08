package uk.co.datumedge.hamcrest.json;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.hamcrest.StringDescription;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author timp
 * @since 2014-06-25
 */
public class CoverageTest {

  @Test
  public void testPrivateConstructedConstant() throws Exception {
    StringDescription description = new StringDescription();
    JSONComparisonResult.comparisonPassed().describeTo(description);
    assertThat("", is(description.toString()));
  }

  @Test
  public void testPrivateConstructors() throws Exception {
    excercisePrivateConstuctor(JSONAssertComparisonResult.class);
  }

  public static void excercisePrivateConstuctor(final Class<?> clazz)
      throws NoSuchMethodException, InvocationTargetException,
      InstantiationException, IllegalAccessException {
    assertThat("There must be only one constructor", 1,
        is(clazz.getDeclaredConstructors().length));
    final Constructor<?> constructor = clazz.getDeclaredConstructor();
    assertThat("The constructor is accessible", false, is(constructor.isAccessible()));
    assertThat("The constructor is not private", false, is(!Modifier.isPrivate(constructor.getModifiers())));
    constructor.setAccessible(true);
    constructor.newInstance();
    constructor.setAccessible(false);
    for (final Method method : clazz.getMethods()) {
      assertThat("There exists a non-static method:" + method,
          false,
          is(!Modifier.isStatic(method.getModifiers()) && method.getDeclaringClass().equals(clazz)));
    }
  }

}
