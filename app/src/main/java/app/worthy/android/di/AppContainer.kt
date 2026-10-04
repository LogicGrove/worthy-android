package app.worthy.android.di

import app.worthy.android.data.repository.SavingsRepository
import app.worthy.android.data.settings.SettingsRepository
import app.worthy.android.data.backup.BackupService

interface AppContainer {
    val savingsRepository: SavingsRepository
    val settingsRepository: SettingsRepository
    val backupService: BackupService
}
