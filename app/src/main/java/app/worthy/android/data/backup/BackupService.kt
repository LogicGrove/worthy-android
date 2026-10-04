package app.worthy.android.data.backup

import android.content.ContentResolver
import android.net.Uri
import app.worthy.android.data.repository.SavingsDataSnapshot
import app.worthy.android.data.repository.SavingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BackupService(
    private val contentResolver: ContentResolver,
    private val repository: SavingsRepository,
    private val codec: BackupCodec = BackupCodec(),
    private val timeProvider: () -> Long = System::currentTimeMillis,
) {
    suspend fun exportTo(uri: Uri) = withContext(Dispatchers.IO) {
        val value = codec.encode(repository.snapshot(), timeProvider())
        requireNotNull(contentResolver.openOutputStream(uri, "wt")).bufferedWriter().use { it.write(value) }
    }

    suspend fun readAndValidate(uri: Uri): SavingsDataSnapshot = withContext(Dispatchers.IO) {
        val value = requireNotNull(contentResolver.openInputStream(uri)).bufferedReader().use { it.readText() }
        codec.decodeAndValidate(value)
    }
}
