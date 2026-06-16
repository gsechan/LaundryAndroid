package com.gabesechan.laundrydemo.util

import junit.framework.TestCase.*
import org.junit.Test

class ShortIdTest {

    @Test
    fun testOutputIsAlways13Chars() {
        assertEquals(13, uuidToShortId("550e8400-e29b-41d4-a716-446655440000").length)
        assertEquals(13, uuidToShortId("00000000-0000-0000-0000-000000000000").length)
        assertEquals(13, uuidToShortId("ffffffff-ffff-ffff-ffff-ffffffffffff").length)
    }

    @Test
    fun testOutputOnlyContainsValidCrockfordChars() {
        val validChars = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toSet()
        val ambiguousChars = setOf('I', 'L', 'O', 'U')
        val result = uuidToShortId("550e8400-e29b-41d4-a716-446655440000")
        assertTrue(result.all { it in validChars })
        assertTrue(result.none { it in ambiguousChars })
    }

    @Test
    fun testAllZeroUuidProducesAllZeroShortId() {
        // MSB=0, LSB=0, folded=0 → all CROCKFORD[0] = '0'
        assertEquals("0000000000000", uuidToShortId("00000000-0000-0000-0000-000000000000"))
    }

    @Test
    fun testAllFUuidProducesAllZeroShortId() {
        // MSB=0xFFFF...FFFF, LSB=0xFFFF...FFFF, XOR=0 → same as all-zero
        assertEquals("0000000000000", uuidToShortId("ffffffff-ffff-ffff-ffff-ffffffffffff"))
    }

    @Test
    fun testFoldedAllOnesProducesCorrectOutput() {
        // MSB=0, LSB=0xFFFF...FFFF, folded=0xFFFF...FFFF
        // 64 bits all set: low 12×5=60 bits → CROCKFORD[31]='Z', high 4 bits=15 → CROCKFORD[15]='F'
        assertEquals("FZZZZZZZZZZZZ", uuidToShortId("00000000-0000-0000-ffff-ffffffffffff"))
    }

    @Test
    fun testFoldedOneProducesCorrectOutput() {
        // MSB=0, LSB=1, folded=1 → only lowest bit set
        assertEquals("0000000000001", uuidToShortId("00000000-0000-0000-0000-000000000001"))
    }

    @Test
    fun testIsDeterministic() {
        val uuid = "550e8400-e29b-41d4-a716-446655440000"
        assertEquals(uuidToShortId(uuid), uuidToShortId(uuid))
    }

    @Test
    fun testDifferentUuidsProduceDifferentShortIds() {
        val id1 = uuidToShortId("550e8400-e29b-41d4-a716-446655440000")
        val id2 = uuidToShortId("6ba7b810-9dad-11d1-80b4-00c04fd430c8")
        assertFalse(id1 == id2)
    }
}
