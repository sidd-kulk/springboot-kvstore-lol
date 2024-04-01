package com.springbootkvstore.lol;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {
    @Test
    public void testIsAnyNullOrBlank(){
        assertTrue(StringUtils.isAnyNullOrBlank(null, null, null));
        assertTrue(StringUtils.isAnyNullOrBlank(null, ""));
        assertTrue(StringUtils.isAnyNullOrBlank(null, " "));
        assertTrue(StringUtils.isAnyNullOrBlank("not_null", " "));
        assertTrue(StringUtils.isAnyNullOrBlank("not_null", null));

        assertFalse(StringUtils.isAnyNullOrBlank("not_null", "another_not_Null"));
    }
}