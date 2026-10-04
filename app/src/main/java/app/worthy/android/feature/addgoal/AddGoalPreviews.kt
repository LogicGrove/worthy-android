package app.worthy.android.feature.addgoal

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.worthy.android.ui.theme.WorthyTheme

@Preview(showBackground = true)
@Composable
private fun AddGoalPreview() {
    WorthyTheme(dynamicColor = false) {
        AddGoalScreen(
            uiState = AddGoalUiState(
                productName = "Mechanical keyboard",
                productUrl = "https://example.com/keyboard",
                targetPriceText = "189.00",
                currencyCode = "EUR",
            ),
            onProductNameChanged = {},
            onProductUrlChanged = {},
            onTargetPriceChanged = {},
            onCurrencyCodeChanged = {},
            onSave = {},
            onBack = {},
        )
    }
}
