package com.github.salvatorenovelli.redirectcheck.http;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SafeStringEncoderTest {
    @Test
    public void getSafeUri() throws Exception {
        String result1 = SafeStringEncoder.encodeString( "/teg/Ñ\u0083Ñ\u0085Ð¾Ð´-Ð·Ð°-Ð¾Ð´ÐµÐ¶Ð´Ð¾Ð¹");
        assertThat(result1, is("/teg/%D1%83%D1%85%D0%BE%D0%B4-%D0%B7%D0%B0-%D0%BE%D0%B4%D0%B5%D0%B6%D0%B4%D0%BE%D0%B9"));
    }
}