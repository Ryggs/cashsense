package ru.resodostudios.cashsense.feature.wallet.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.component.CsModalBottomSheet
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.core.ui.formatAmount
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogEvent.UpdatePrimary
import java.math.BigDecimal
import ru.resodostudios.cashsense.core.ui.R as uiR

@Composable
fun WalletBottomSheet(
    onDismiss: () -> Unit,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit,
    viewModel: WalletDialogViewModel = hiltViewModel(),
) {
    val walletDialogState by viewModel.walletDialogUiState.collectAsStateWithLifecycle()

    WalletBottomSheet(
        walletDialogState = walletDialogState,
        onDismiss = onDismiss,
        onEdit = onEdit,
        onDelete = onDelete,
        onWalletDialogEvent = viewModel::onWalletDialogEvent,
        updatePrimaryWalletId = viewModel::updatePrimaryWalletId,
    )
}

@Composable
fun WalletBottomSheet(
    walletDialogState: WalletDialogUiState,
    onDismiss: () -> Unit,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit,
    onWalletDialogEvent: (WalletDialogEvent) -> Unit,
    updatePrimaryWalletId: () -> Unit,
) {
    CsModalBottomSheet(onDismiss) {
        AnimatedVisibility(walletDialogState.isLoading) {
            LoadingState(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth(),
            )
        }
        AnimatedVisibility(!walletDialogState.isLoading) {
            Column {
                CsListItem(
                    headlineContent = { Text(walletDialogState.title) },
                    leadingContent = {
                        Icon(
                            imageVector = ImageVector.vectorResource(CsIcons.Wallet),
                            contentDescription = null,
                        )
                    },
                    supportingContent = {
                        Text(
                            text = walletDialogState.currentBalance.ifEmpty {
                                BigDecimal(0).formatAmount(walletDialogState.currency)
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                )
                HorizontalDivider(Modifier.padding(16.dp))
                CsListItem(
                    headlineContent = { Text(stringResource(R.string.feature_wallet_dialog_primary)) },
                    leadingContent = {
                        Icon(
                            imageVector = ImageVector.vectorResource(CsIcons.Star),
                            contentDescription = null,
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = walletDialogState.isPrimary,
                            onCheckedChange = {
                                onWalletDialogEvent(UpdatePrimary(it))
                                updatePrimaryWalletId()
                            },
                        )
                    }
                )
                CsListItem(
                    headlineContent = { Text(stringResource(uiR.string.edit)) },
                    leadingContent = {
                        Icon(
                            imageVector = ImageVector.vectorResource(CsIcons.Edit),
                            contentDescription = null,
                        )
                    },
                    onClick = {
                        onEdit(walletDialogState.id)
                        onDismiss()
                    },
                )
                CsListItem(
                    headlineContent = { Text(stringResource(uiR.string.delete)) },
                    leadingContent = {
                        Icon(
                            imageVector = ImageVector.vectorResource(CsIcons.Delete),
                            contentDescription = null,
                        )
                    },
                    onClick = {
                        onDelete(walletDialogState.id)
                        onDismiss()
                    },
                )
            }
        }
    }
}