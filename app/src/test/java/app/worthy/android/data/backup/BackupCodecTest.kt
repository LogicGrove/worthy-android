package app.worthy.android.data.backup

import app.worthy.android.data.local.SavingsContributionEntity
import app.worthy.android.data.local.SavingsGoalEntity
import app.worthy.android.data.repository.SavingsDataSnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class BackupCodecTest {
    private val codec = BackupCodec()
    private val snapshot = SavingsDataSnapshot(
        goals = listOf(SavingsGoalEntity(7, "Camera", "https://example.com/camera", 12_345, "EUR", 10, 11)),
        contributions = listOf(SavingsContributionEntity(9, 7, 1_234, 12)),
    )

    @Test fun `round trip preserves ids money and relationships`() {
        val restored = codec.decodeAndValidate(codec.encode(snapshot, exportedAt = 100))
        assertEquals(snapshot, restored)
    }

    @Test fun `unsupported version is rejected`() {
        val invalid = codec.encode(snapshot, exportedAt = 100).replace("\"backupFormatVersion\": 1", "\"backupFormatVersion\": 2")
        assertThrows(IllegalArgumentException::class.java) { codec.decodeAndValidate(invalid) }
    }

    @Test fun `orphan contribution is rejected`() {
        val invalid = codec.encode(snapshot, exportedAt = 100).replace("\"goalId\": 7", "\"goalId\": 8")
        assertThrows(IllegalArgumentException::class.java) { codec.decodeAndValidate(invalid) }
    }
}
