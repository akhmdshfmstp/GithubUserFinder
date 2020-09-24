package com.test.github.user.finder.framework

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.test.github.user.finder.framework.helper.ValidationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ValidationHelperTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var validationHelper: ValidationHelper

    @Before
    fun setUp() {
        Dispatchers.resetMain()
        MockitoAnnotations.initMocks(this)
        validationHelper = ValidationHelper
    }

    @Test
    fun test_isEmptyTrue() {
        val actualResult1 = validationHelper.isEmpty("")
        val actualResult2 = validationHelper.isEmpty(" ")

        assertTrue(actualResult1)
        assertTrue(actualResult2)
    }

    @Test
    fun test_isEmptyFalse() {
        val actualResult = validationHelper.isEmpty("random")

        assertFalse(actualResult)
    }
}