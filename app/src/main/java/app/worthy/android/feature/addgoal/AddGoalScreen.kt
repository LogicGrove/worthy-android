package app.worthy.android.feature.addgoal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.worthy.android.R
import app.worthy.android.core.money.MoneyInputResult

@Composable
fun AddGoalScreen(
    uiState: AddGoalUiState,
    onProductNameChanged: (String) -> Unit,
    onProductUrlChanged: (String) -> Unit,
    onTargetPriceChanged: (String) -> Unit,
    onCurrencyCodeChanged: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TextButton(onClick = onBack) { Text(stringResource(R.string.back)) }
            Text(
                stringResource(R.string.add_goal_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.semantics { heading() },
            )
            Text(stringResource(R.string.add_goal_supporting), color = MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedTextField(
                value = uiState.productName,
                onValueChange = onProductNameChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.product_name)) },
                placeholder = { Text(stringResource(R.string.product_name_example)) },
                supportingText = if (uiState.productNameError) {
                    { Text(stringResource(R.string.error_product_name)) }
                } else null,
                isError = uiState.productNameError,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            )
            OutlinedTextField(
                value = uiState.productUrl,
                onValueChange = onProductUrlChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.product_url)) },
                placeholder = { Text(stringResource(R.string.product_url_example)) },
                supportingText = if (uiState.productUrlError) {
                    { Text(stringResource(R.string.error_product_url)) }
                } else null,
                isError = uiState.productUrlError,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Next),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = uiState.targetPriceText,
                    onValueChange = onTargetPriceChanged,
                    modifier = Modifier.weight(1f),
                    label = { Text(stringResource(R.string.target_price)) },
                    supportingText = uiState.amountError?.let { error ->
                        { Text(stringResource(amountErrorResource(error))) }
                    },
                    isError = uiState.amountError != null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                )
                OutlinedTextField(
                    value = uiState.currencyCode,
                    onValueChange = onCurrencyCodeChanged,
                    modifier = Modifier.weight(0.65f),
                    label = { Text(stringResource(R.string.currency_code)) },
                    placeholder = { Text(stringResource(R.string.currency_example)) },
                    supportingText = if (uiState.currencyError) {
                        { Text(stringResource(R.string.error_currency)) }
                    } else null,
                    isError = uiState.currencyError,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = androidx.compose.ui.text.input.KeyboardCapitalization.Characters),
                )
            }
            if (uiState.saveFailed) {
                Text(stringResource(R.string.error_save_goal), color = MaterialTheme.colorScheme.error)
            }
            Button(
                onClick = onSave,
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(if (uiState.isSaving) R.string.saving_goal else R.string.save_goal))
            }
        }
    }
}

fun amountErrorResource(error: MoneyInputResult): Int = when (error) {
    MoneyInputResult.Empty -> R.string.error_amount_empty
    MoneyInputResult.Invalid -> R.string.error_amount_invalid
    MoneyInputResult.NotPositive -> R.string.error_amount_positive
    MoneyInputResult.TooManyFractionDigits -> R.string.error_amount_precision
    MoneyInputResult.Overflow -> R.string.error_amount_too_large
    is MoneyInputResult.Valid -> R.string.error_amount_invalid
}
