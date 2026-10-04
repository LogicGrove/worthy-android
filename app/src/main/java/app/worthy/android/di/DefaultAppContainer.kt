package app.worthy.android.di

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import app.worthy.android.data.local.WorthyDatabase
import app.worthy.android.data.repository.RoomSavingsRepository
import app.worthy.android.data.repository.SavingsRepository
import app.worthy.android.data.settings.DataStoreSettingsRepository
import app.worthy.android.data.settings.SettingsRepository
import app.worthy.android.data.backup.BackupService

class DefaultAppContainer(context: Context) : AppContainer {
    private val database = Room.databaseBuilder<WorthyDatabase>(
        context = context.applicationContext,
        name = "worthy.db",
    ).setDriver(AndroidSQLiteDriver()).build()

    override val savingsRepository: SavingsRepository = RoomSavingsRepository(database.worthyDao())
    override val settingsRepository: SettingsRepository = DataStoreSettingsRepository(context.applicationContext)
    override val backupService: BackupService = BackupService(context.contentResolver, savingsRepository)
}
