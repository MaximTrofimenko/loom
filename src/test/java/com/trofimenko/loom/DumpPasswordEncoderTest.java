package com.trofimenko.loom;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DumpPasswordEncoderTest {

    @Test
    void encode() {
        DumpPasswordEncoder dumpPasswordEncoder = new DumpPasswordEncoder();
        assertEquals("secret: 'mypwd'", dumpPasswordEncoder.encode("mypwd"));
        assertThat( dumpPasswordEncoder.encode("mypwd"), Matchers.containsString("mypwd"));
    }
}