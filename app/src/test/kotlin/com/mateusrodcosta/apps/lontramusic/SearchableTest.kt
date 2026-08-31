package org.sunsetware.phocid

import com.ibm.icu.text.Collator
import com.ibm.icu.text.RuleBasedCollator
import java.util.Locale
import org.assertj.core.api.Assertions.*
import org.junit.Test
import org.sunsetware.phocid.data.Searchable
import org.sunsetware.phocid.data.search

class SearchableTest {

    private val collator = Collator.getInstance(Locale.ROOT) as RuleBasedCollator

    data class TestSearchable(override val searchableStrings: List<String>) : Searchable

    @Test
    fun search_EmptyQuery() {
        val targets = listOf(TestSearchable(listOf("a", "b")), TestSearchable(listOf("c", "d")))
        val result = targets.search("", collator)
        assertThat(result).isEqualTo(targets)
    }

    @Test
    fun search_NoMatch() {
        val targets = listOf(TestSearchable(listOf("a", "b")), TestSearchable(listOf("c", "d")))
        val result = targets.search("e", collator)
        assertThat(result).isEmpty()
    }

    @Test
    fun search_OneMatch() {
        val targets = listOf(TestSearchable(listOf("a", "b")), TestSearchable(listOf("c", "d")))
        val result = targets.search("a", collator)
        assertThat(result).isEqualTo(listOf(targets[0]))
    }

    @Test
    fun search_TargetsWithEmptyString() {
        val targets = listOf(TestSearchable(listOf("", "a")), TestSearchable(listOf("", "")))
        val result = targets.search("a", collator)
        assertThat(result).isEqualTo(listOf(targets[0]))
    }

    @Test
    fun search_NoTargets() {
        val targets = emptyList<TestSearchable>()
        val result = targets.search("a", collator)
        assertThat(result).isEmpty()
    }

    @Test
    fun searchWithSelector_EmptyQuery() {
        val targets =
            listOf("1" to TestSearchable(listOf("a", "b")), "2" to TestSearchable(listOf("c", "d")))
        val result = targets.search("", collator) { it.second }
        assertThat(result).isEqualTo(targets)
    }

    @Test
    fun searchWithSelector_NoMatch() {
        val targets =
            listOf("1" to TestSearchable(listOf("a", "b")), "2" to TestSearchable(listOf("c", "d")))
        val result = targets.search("e", collator) { it.second }
        assertThat(result).isEmpty()
    }

    @Test
    fun searchWithSelector_OneMatch() {
        val targets =
            listOf("1" to TestSearchable(listOf("a", "b")), "2" to TestSearchable(listOf("c", "d")))
        val result = targets.search("a", collator) { it.second }
        assertThat(result).isEqualTo(listOf(targets[0]))
    }

    @Test
    fun searchWithSelector_TargetsWithEmptyString() {
        val targets =
            listOf("1" to TestSearchable(listOf("", "a")), "2" to TestSearchable(listOf("", "")))
        val result = targets.search("a", collator) { it.second }
        assertThat(result).isEqualTo(listOf(targets[0]))
    }

    @Test
    fun searchWithSelector_NoTargets() {
        val targets = emptyList<Pair<String, TestSearchable>>()
        val result = targets.search("a", collator) { it.second }
        assertThat(result).isEmpty()
    }
}
