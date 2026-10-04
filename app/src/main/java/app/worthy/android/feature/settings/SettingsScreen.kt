package app.worthy.android.feature.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.draw.clip
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import app.worthy.android.R
import app.worthy.android.core.haptics.WorthyHaptics
import app.worthy.android.data.settings.AppLanguage
import app.worthy.android.ui.components.worthyPressClickable
import java.util.Currency
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    versionName: String,
    onBack: () -> Unit,
    onDefaultCurrencyChanged: (String) -> Unit,
    onHapticFeedbackChanged: (Boolean) -> Unit,
    onHighRefreshRateChanged: (Boolean) -> Unit,
    haptics: WorthyHaptics? = null,
    onAppLanguageChanged: (AppLanguage) -> Unit,
    onExport: (android.net.Uri) -> Unit,
    onImport: (android.net.Uri) -> Unit,
    onRequestDeleteAll: () -> Unit,
    onDismissDeleteAll: () -> Unit,
    onConfirmDeleteAll: () -> Unit,
    onDismissImport: () -> Unit,
    onConfirmImport: () -> Unit,
    onConsumeMessage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showCurrencyPicker by rememberSaveable { mutableStateOf(false) }
    var showLanguagePicker by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var previousHapticsEnabled by remember { mutableStateOf(uiState.hapticFeedbackEnabled) }
    LaunchedEffect(uiState.hapticFeedbackEnabled) {
        if (!previousHapticsEnabled && uiState.hapticFeedbackEnabled) {
            haptics?.toggleEnabled()
        }
        previousHapticsEnabled = uiState.hapticFeedbackEnabled
    }
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { it?.let(onExport) }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { it?.let(onImport) }
    val message = uiState.message
    if (message != null) {
        val text = stringResource(message.stringRes())
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(text)
            onConsumeMessage()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { SettingsTopBar(onBack) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 24.dp, top = padding.calculateTopPadding() + 20.dp, end = 24.dp, bottom = padding.calculateBottomPadding() + 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { SectionHeading(R.string.settings_general) }
            item {
                SettingsRow(R.string.default_currency, R.string.default_currency_supporting, uiState.defaultCurrencyCode, onClick = { showCurrencyPicker = true })
            }
            item {
                SettingsRow(
                    title = R.string.haptic_feedback,
                    supporting = R.string.haptic_feedback_supporting,
                    onClick = { onHapticFeedbackChanged(!uiState.hapticFeedbackEnabled) },
                    role = Role.Switch,
                    trailingContent = { Switch(checked = uiState.hapticFeedbackEnabled, onCheckedChange = null) },
                )
            }
            item {
                SettingsRow(
                    title = R.string.high_refresh_rate,
                    supporting = R.string.high_refresh_rate_supporting,
                    onClick = { onHighRefreshRateChanged(!uiState.highRefreshRateEnabled) },
                    role = Role.Switch,
                    trailingContent = { Switch(checked = uiState.highRefreshRateEnabled, onCheckedChange = null) },
                )
            }
            item {
                SettingsRow(R.string.app_language, R.string.app_language_supporting, stringResource(uiState.appLanguage.labelRes()), onClick = { showLanguagePicker = true })
            }
            item { SectionHeading(R.string.settings_data, Modifier.padding(top = 18.dp)) }
            item { SettingsRow(R.string.export_data, R.string.export_data_supporting, onClick = { exportLauncher.launch("worthy-backup.json") }, enabled = !uiState.isWorking) }
            item { SettingsRow(R.string.import_data, R.string.import_data_supporting, onClick = { importLauncher.launch(arrayOf("application/json", "text/json", "text/plain")) }, enabled = !uiState.isWorking) }
            item {
                SettingsRow(
                    R.string.delete_all_data,
                    R.string.delete_all_data_supporting,
                    onClick = onRequestDeleteAll,
                    contentColor = MaterialTheme.colorScheme.error,
                    enabled = !uiState.isWorking,
                )
            }
            item { SectionHeading(R.string.settings_about, Modifier.padding(top = 18.dp)) }
            item { SettingsRow(R.string.version, trailingValue = versionName) }
            item {
                SettingsRow(R.string.source_code, R.string.source_code_supporting, onClick = {
                    // No canonical repository URL exists yet; keep this a safe, explicit boundary.
                    onConsumeMessage()
                }, actionUnavailableMessage = stringResource(R.string.source_code_unavailable), snackbarHostState = snackbarHostState)
            }
            item {
                SettingsRow(R.string.open_source_licenses, onClick = {}, actionUnavailableMessage = stringResource(R.string.licenses_unavailable), snackbarHostState = snackbarHostState)
            }
        }
    }

    if (showCurrencyPicker) CurrencyPickerSheet(uiState.defaultCurrencyCode, { showCurrencyPicker = false }, { onDefaultCurrencyChanged(it); showCurrencyPicker = false })
    if (showLanguagePicker) LanguagePickerSheet(uiState.appLanguage, { showLanguagePicker = false }) { language ->
        onAppLanguageChanged(language)
        val locales = if (language == AppLanguage.English) LocaleListCompat.forLanguageTags("en") else LocaleListCompat.getEmptyLocaleList()
        AppCompatDelegate.setApplicationLocales(locales)
        showLanguagePicker = false
    }
    if (uiState.showDeleteConfirmation) ConfirmDialog(R.string.delete_all_title, R.string.delete_all_body, R.string.delete, true, onDismissDeleteAll, onConfirmDeleteAll, uiState.isWorking)
    if (uiState.pendingImport != null) ConfirmDialog(R.string.import_confirm_title, R.string.import_confirm_body, R.string.replace_data, false, onDismissImport, onConfirmImport, uiState.isWorking)
}

@Composable
private fun SettingsTopBar(onBack: () -> Unit) {
    Box(Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars).heightIn(min = 64.dp), contentAlignment = Alignment.Center) {
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart).padding(start = 12.dp)) {
            Icon(painterResource(R.drawable.ic_arrow_back_24), stringResource(R.string.back))
        }
        Text(stringResource(R.string.settings_title), style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 72.dp))
    }
}

@Composable
private fun SectionHeading(text: Int, modifier: Modifier = Modifier) {
    Text(stringResource(text), style = MaterialTheme.typography.displaySmall, modifier = modifier.padding(start = 8.dp, bottom = 12.dp).semantics { heading() })
}

@Composable
private fun SettingsRow(
    title: Int,
    supporting: Int? = null,
    trailingValue: String? = null,
    onClick: (() -> Unit)? = null,
    role: Role? = Role.Button,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    trailingContent: (@Composable () -> Unit)? = null,
    actionUnavailableMessage: String? = null,
    snackbarHostState: SnackbarHostState? = null,
    enabled: Boolean = true,
) {
    val unavailableScope = androidx.compose.runtime.rememberCoroutineScope()
    val action: (() -> Unit)? = if (actionUnavailableMessage != null && snackbarHostState != null) {
        { unavailableScope.launch { snackbarHostState.showSnackbar(actionUnavailableMessage) }; Unit }
    } else onClick
    Row(
        modifier = Modifier.fillMaxWidth().heightIn(min = 88.dp)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .semantics(mergeDescendants = true) { }
            .then(if (action != null) Modifier.worthyPressClickable(enabled = enabled, role = role, onClick = action) else Modifier)
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(stringResource(title), style = MaterialTheme.typography.bodyLarge, color = contentColor)
            supporting?.let { Text(stringResource(it), style = MaterialTheme.typography.bodyMedium, color = if (contentColor == MaterialTheme.colorScheme.error) contentColor else MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        trailingContent?.invoke()
        trailingValue?.let { Text(it, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis) }
        if (action != null && trailingContent == null) Icon(painterResource(R.drawable.ic_chevron_right_24), contentDescription = null, tint = if (contentColor == MaterialTheme.colorScheme.error) contentColor else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
    }
}

private data class CurrencyItem(val code: String, val name: String, val symbol: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencyPickerSheet(selected: String, onDismiss: () -> Unit, onSelect: (String) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    val locale = Locale.getDefault()
    val currencies = remember(locale) {
        Currency.getAvailableCurrencies().map { CurrencyItem(it.currencyCode, it.getDisplayName(locale), it.getSymbol(locale)) }.sortedBy { it.code }
    }
    val filtered = remember(currencies, query) { currencies.filter { it.code.contains(query, true) || it.name.contains(query, true) || it.symbol.contains(query, true) } }
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 24.dp)) {
            Text(stringResource(R.string.choose_currency), style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 16.dp))
            TextField(value = query, onValueChange = { query = it }, label = { Text(stringResource(R.string.search_currencies)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
            LazyColumn(Modifier.fillMaxWidth().heightIn(max = 520.dp), contentPadding = PaddingValues(vertical = 12.dp)) {
                items(filtered, key = { it.code }) { currency ->
                    Row(Modifier.fillMaxWidth().clickable { onSelect(currency.code) }.heightIn(min = 64.dp).padding(horizontal = 8.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) { Text(currency.code, style = MaterialTheme.typography.titleMedium); Text(currency.name, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        Text(currency.symbol, style = MaterialTheme.typography.titleMedium)
                        if (currency.code == selected) Text(stringResource(R.string.selected), color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguagePickerSheet(selected: AppLanguage, onDismiss: () -> Unit, onSelect: (AppLanguage) -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 24.dp, vertical = 8.dp)) {
            Text(stringResource(R.string.choose_language), style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 16.dp))
            AppLanguage.entries.forEach { language ->
                Row(Modifier.fillMaxWidth().clickable { onSelect(language) }.heightIn(min = 64.dp).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(language.labelRes()), Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                    if (language == selected) Text(stringResource(R.string.selected), color = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ConfirmDialog(title: Int, body: Int, confirmLabel: Int, destructive: Boolean, onDismiss: () -> Unit, onConfirm: () -> Unit, working: Boolean) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(title)) },
        text = { Text(stringResource(body)) },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !working) { Text(stringResource(R.string.cancel)) } },
        confirmButton = { TextButton(onClick = onConfirm, enabled = !working, colors = if (destructive) ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error) else ButtonDefaults.textButtonColors()) { Text(stringResource(confirmLabel)) } },
    )
}

private fun AppLanguage.labelRes() = if (this == AppLanguage.English) R.string.language_english else R.string.language_system_default
private fun SettingsMessage.stringRes() = when (this) {
    SettingsMessage.Exported -> R.string.export_success
    SettingsMessage.ExportFailed -> R.string.export_failed
    SettingsMessage.ImportInvalid -> R.string.import_invalid
    SettingsMessage.Imported -> R.string.import_success
    SettingsMessage.ImportFailed -> R.string.import_failed
    SettingsMessage.Deleted -> R.string.delete_all_success
    SettingsMessage.DeleteFailed -> R.string.delete_all_failed
}
