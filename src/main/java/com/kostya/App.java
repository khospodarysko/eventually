package com.kostya;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class App {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        try {
            // those all work
            // assertThat("msg", () -> "aa", EventuallMatcher.eventually(containsString("b")));
            // assertThat("msg", () -> "aa", EventuallMatcher.eventually(equalTo("b")));
            // assertThat("msg", () -> Arrays.asList("1", "2"), EventuallMatcher.eventually(hasItem("3")));
            // assertThat("msg", () -> ImmutableMap.of("1", "a", "2", "b"), EventuallMatcher.eventually(hasEntry("3", "c")));

            List<Map<String, String>> listOfMaps = Lists.newArrayList(
                ImmutableMap.of("1", "a"),
                ImmutableMap.of("2", "b")
            );
            // FIXME: bad return type here
            assertThat("msg", () -> listOfMaps, EventuallMatcher.eventually(hasItem(hasEntry("1", "a"))));
        } finally {
            System.out.println("debug time " + (System.currentTimeMillis() - start));
        }
    }
}

class EventuallMatcher<T> extends TypeSafeMatcher<Supplier<T>> {
    private Matcher<T> matcher;

    public EventuallMatcher(Matcher<T> matcher) {
        this.matcher = matcher;
    }

    public void describeTo(Description description) {
        description.appendText("eventually " + matcher.toString());
    }

    @Override
    protected void describeMismatchSafely(Supplier<T> supplier, Description mismatchDescription) {
        mismatchDescription.appendText("was ").appendValue(supplier.get());
    }

    @Override
    public boolean matchesSafely(Supplier<T> supplier) {
        long end = System.currentTimeMillis() + 2 * 1000;
        while (System.currentTimeMillis() < end) {
            if (matcher.matches(supplier.get())) {
                return true;
            }
        }
        return false;
    }

    public static <T> EventuallMatcher<T> eventually(Matcher<T> matcher) {
        return new EventuallMatcher<>(matcher);
    }
}
