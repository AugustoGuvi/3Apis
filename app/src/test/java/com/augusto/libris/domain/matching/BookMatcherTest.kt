package com.augusto.libris.domain.matching

import org.junit.Assert.*
import org.junit.Test

class BookMatcherTest {
    @Test fun ignoresAccentsCaseAndOrder() {
        assertEquals(1.0, BookMatcher.similarity("Miguel de Cervantes", "CERVANTES, Miguel de"), 0.001)
    }

    @Test fun connectsSameWorkAcrossCatalogues() {
        assertNotNull(BookMatcher.matchScore(
            "Pride and Prejudice", listOf("Jane Austen"),
            "Pride and Prejudice", listOf("Austen, Jane")
        ))
    }

    @Test fun refusesDifferentAuthor() {
        assertNull(BookMatcher.matchScore(
            "Pride and Prejudice", listOf("Jane Austen"),
            "Pride and Prejudice", listOf("John Smith")
        ))
    }

    @Test fun toleratesShorterBookTitle() {
        assertNotNull(BookMatcher.matchScore(
            "Don Quijote de la Mancha", listOf("Miguel de Cervantes Saavedra"),
            "Don Quijote", listOf("Cervantes Saavedra, Miguel de")
        ))
    }

    @Test fun rejectsAuthorSharingOnlyOneName() {
        assertNull(BookMatcher.matchScore(
            "An Example Book", listOf("John Smith"),
            "An Example Book", listOf("John Doe")
        ))
    }

    @Test fun rejectsUnrelatedTitle() {
        assertNull(BookMatcher.matchScore(
            "Don Quijote de la Mancha", listOf("Miguel de Cervantes"),
            "La Regenta", listOf("Miguel de Cervantes")
        ))
    }
}
