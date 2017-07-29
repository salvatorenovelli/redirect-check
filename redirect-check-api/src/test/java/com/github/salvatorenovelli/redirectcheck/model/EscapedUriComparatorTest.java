package com.github.salvatorenovelli.redirectcheck.model;

import org.junit.Test;

import static com.github.salvatorenovelli.redirectcheck.model.EscapedUriComparator.compare;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class EscapedUriComparatorTest {


    @Test
    public void compareUnescapedUri() throws Exception {
        assertThat(compare("http://www.example.com", "http://www.example.com"), is(true));
    }

    @Test
    public void shouldRecognizeDifferentUri() throws Exception {
        assertThat(compare("http://www.example.com", "http://www.example1.com"), is(false));
    }

    @Test
    public void shouldAllowEscapedCharacter() throws Exception {
        assertThat(compare("http://www.example.com/ü", "http://www.example.com/%C3%BC"), is(true));
    }

}