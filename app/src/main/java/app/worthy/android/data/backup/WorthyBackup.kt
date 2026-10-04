package app.worthy.android.data.backup

import app.worthy.android.core.validation.isValidProductName
import app.worthy.android.core.validation.isValidProductUrl
import app.worthy.android.core.validation.normalizedCurrencyCodeOrNull
import app.worthy.android.data.local.SavingsContributionEntity
import app.worthy.android.data.local.SavingsGoalEntity
import app.worthy.android.data.repository.SavingsDataSnapshot
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

const val BACKUP_FORMAT_VERSION = 1

@Serializable
data class WorthyBackup(
    val backupFormatVersion: Int,
    val exportedAt: Long,
    val goals: List<BackupGoal>,
    val contributions: List<BackupContribution>,
)

@Serializable
data class BackupGoal(
    val id: Long,
    val productName: String,
    val productUrl: String,
    val targetAmountMinor: Long,
    val currencyCode: String,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)

@Serializable
data class BackupContribution(
    val id: Long,
    val goalId: Long,
    val amountMinor: Long,
    val createdAtEpochMillis: Long,
)

class BackupCodec(
    private val json: Json = Json { prettyPrint = true; ignoreUnknownKeys = false },
) {
    fun encode(snapshot: SavingsDataSnapshot, exportedAt: Long): String = json.encodeToString(
        WorthyBackup(
            backupFormatVersion = BACKUP_FORMAT_VERSION,
            exportedAt = exportedAt,
            goals = snapshot.goals.map {
                BackupGoal(it.id, it.productName, it.productUrl, it.targetAmountMinor, it.currencyCode, it.createdAtEpochMillis, it.updatedAtEpochMillis)
            },
            contributions = snapshot.contributions.map {
                BackupContribution(it.id, it.goalId, it.amountMinor, it.createdAtEpochMillis)
            },
        ),
    )

    fun decodeAndValidate(value: String): SavingsDataSnapshot {
        val backup = json.decodeFromString<WorthyBackup>(value)
        require(backup.backupFormatVersion == BACKUP_FORMAT_VERSION) { "Unsupported backup version" }
        require(backup.exportedAt >= 0)
        require(backup.goals.map { it.id }.distinct().size == backup.goals.size)
        require(backup.contributions.map { it.id }.distinct().size == backup.contributions.size)
        require(backup.goals.all { it.id > 0 && isValidProductName(it.productName) && isValidProductUrl(it.productUrl) && it.targetAmountMinor > 0 && normalizedCurrencyCodeOrNull(it.currencyCode) == it.currencyCode && it.createdAtEpochMillis >= 0 && it.updatedAtEpochMillis >= it.createdAtEpochMillis })
        val goalIds = backup.goals.mapTo(mutableSetOf()) { it.id }
        require(backup.contributions.all { it.id > 0 && it.goalId in goalIds && it.amountMinor > 0 && it.createdAtEpochMillis >= 0 })
        backup.contributions.groupBy { it.goalId }.values.forEach { contributions ->
            contributions.fold(0L) { total, contribution -> Math.addExact(total, contribution.amountMinor) }
        }
        return SavingsDataSnapshot(
            goals = backup.goals.map { SavingsGoalEntity(it.id, it.productName, it.productUrl, it.targetAmountMinor, it.currencyCode, it.createdAtEpochMillis, it.updatedAtEpochMillis) },
            contributions = backup.contributions.map { SavingsContributionEntity(it.id, it.goalId, it.amountMinor, it.createdAtEpochMillis) },
        )
    }
}
