package io.vertx.kotlin.lang

import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.Test as test

class GlobTests {
    @test fun testGlobAbsolutePath() {
        assertTrue("/path/*.txt".globToPattern().matcher("/path/a.txt").find())
        assertFalse("/path/*.txt".globToPattern().matcher("/path/a.txtz").find())
        assertFalse("/path/*.txt".globToPattern().matcher("/path/subdir/a.txt").find())
    }

    @test fun testGlobFilePattern() {
        assertTrue("*.txt".globToPattern().matcher("/path/a.txt").find())
        assertTrue("*.txt".globToPattern().matcher("/path/subdir/a.txt").find())
        assertTrue("*.txt".globToPattern().matcher(".txt").find())
        assertFalse("*.txt".globToPattern().matcher("a.txtz").find())
    }

    @test fun testGlobAnySubdir() {
        assertFalse("/path/**/*.txt".globToPattern().matcher("/path/a.txt").find())
        assertTrue("/path/**/*.txt".globToPattern().matcher("/path/subdir/a.txt").find())
        assertTrue("/path/**/*.txt".globToPattern().matcher("/path/subdir/sub-subdir/a.txt").find())
    }
}