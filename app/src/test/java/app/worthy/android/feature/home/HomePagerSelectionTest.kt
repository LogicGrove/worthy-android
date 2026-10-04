package app.worthy.android.feature.home

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HomePagerSelectionTest {
    @Test fun `virtual pages map cyclically forwards`() {
        val start = centeredVirtualPage(goalCount = 3, logicalIndex = 0)
        assertEquals(listOf(0, 1, 2, 0), (start..start + 3).map { logicalGoalIndex(it, 3) })
    }

    @Test fun `floor mod maps backwards cyclically`() {
        val start = centeredVirtualPage(goalCount = 3, logicalIndex = 0)
        assertEquals(listOf(0, 2, 1, 0), (start downTo start - 3).map { logicalGoalIndex(it, 3) })
        assertEquals(2, logicalGoalIndex(-1, 3))
    }

    @Test fun `one goal maps every virtual page to itself`() {
        assertEquals(listOf(0, 0, 0), listOf(0, 10, Int.MAX_VALUE).map { logicalGoalIndex(it, 1) })
    }

    @Test fun `existing active goal survives list reorder`() {
        assertEquals(2L, reconcileActiveGoalId(listOf(1L, 2L, 3L), listOf(3L, 2L, 1L), 2L))
    }

    @Test fun `deleted active goal selects next neighbor at the same position`() {
        assertEquals(3L, reconcileActiveGoalId(listOf(1L, 2L, 3L), listOf(1L, 3L), 2L))
    }

    @Test fun `deleting last active goal selects previous neighbor`() {
        assertEquals(2L, reconcileActiveGoalId(listOf(1L, 2L, 3L), listOf(1L, 2L), 3L))
    }

    @Test fun `empty goal list has no active goal`() {
        assertNull(reconcileActiveGoalId(listOf(1L), emptyList(), 1L))
    }

    @Test fun `repeated logical goals still have unique virtual keys`() {
        assertNotEquals(virtualPageKey(10, 1L), virtualPageKey(13, 1L))
    }
}
