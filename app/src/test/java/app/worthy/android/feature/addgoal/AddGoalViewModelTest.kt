package app.worthy.android.feature.addgoal

import app.worthy.android.MainDispatcherRule
import app.worthy.android.data.repository.FakeSavingsRepository
import app.worthy.android.data.settings.AppLanguage
import app.worthy.android.data.settings.AppSettings
import app.worthy.android.data.settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Locale
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AddGoalViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test fun `invalid submission exposes field errors`() {
        val viewModel = AddGoalViewModel(FakeSavingsRepository(), Locale.US)
        viewModel.saveGoal()
        assertTrue(viewModel.uiState.value.productNameError)
        assertTrue(viewModel.uiState.value.productUrlError)
        assertNotNull(viewModel.uiState.value.amountError)
    }

    @Test fun `valid submission creates goal and exposes id`() = runTest {
        val repository = FakeSavingsRepository()
        val viewModel = AddGoalViewModel(repository, Locale.US)
        viewModel.onProductNameChanged("Camera")
        viewModel.onProductUrlChanged("https://example.com/camera")
        viewModel.onTargetPriceChanged("250.00")
        viewModel.onCurrencyCodeChanged("EUR")
        viewModel.saveGoal()
        testScheduler.advanceUntilIdle()
        assertEquals("Camera", repository.lastCreatedName)
        assertEquals(1L, viewModel.uiState.value.savedGoalId)
    }

    @Test fun `stored default currency initializes a new goal`() = runTest {
        val settings = FakeSettingsRepository(AppSettings("GBP", true, true, AppLanguage.SystemDefault))
        val viewModel = AddGoalViewModel(FakeSavingsRepository(), Locale.US, settings)

        testScheduler.advanceUntilIdle()

        assertEquals("GBP", viewModel.uiState.value.currencyCode)
    }

    @Test fun `manual currency entry is not overwritten by delayed settings`() = runTest {
        val settings = FakeSettingsRepository(AppSettings("GBP", true, true, AppLanguage.SystemDefault))
        val viewModel = AddGoalViewModel(FakeSavingsRepository(), Locale.US, settings)
        viewModel.onCurrencyCodeChanged("JPY")

        testScheduler.advanceUntilIdle()

        assertEquals("JPY", viewModel.uiState.value.currencyCode)
    }
}

private class FakeSettingsRepository(initial: AppSettings) : SettingsRepository {
    override val settings = MutableStateFlow(initial)
    override suspend fun setDefaultCurrency(code: String) { settings.value = settings.value.copy(defaultCurrencyCode = code) }
    override suspend fun setHapticFeedbackEnabled(enabled: Boolean) { settings.value = settings.value.copy(hapticFeedbackEnabled = enabled) }
    override suspend fun setHighRefreshRateEnabled(enabled: Boolean) { settings.value = settings.value.copy(highRefreshRateEnabled = enabled) }
    override suspend fun setThemeMode(mode: app.worthy.android.data.settings.ThemeMode) { settings.value = settings.value.copy(themeMode = mode) }
    override suspend fun setDynamicColorEnabled(enabled: Boolean) { settings.value = settings.value.copy(dynamicColorEnabled = enabled) }
    override suspend fun setColorStyle(style: app.worthy.android.data.settings.ColorStyle) { settings.value = settings.value.copy(colorStyle = style) }
    override suspend fun setAppLanguage(language: AppLanguage) { settings.value = settings.value.copy(appLanguage = language) }
}
