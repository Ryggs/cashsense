package ru.resodostudios.cashsense.feature.wallet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.model.data.WalletWithTransactionsAndCategories
import ru.resodostudios.cashsense.feature.wallet.navigation.WalletArgs
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val walletsRepository: WalletsRepository
) : ViewModel() {

    private val walletArgs: WalletArgs = WalletArgs(savedStateHandle)

    val walletUiState: StateFlow<WalletUiState> =
        walletsRepository.getWalletWithTransactions(walletArgs.walletId)
            .map<WalletWithTransactionsAndCategories, WalletUiState>(WalletUiState::Success)
            .onStart { emit(WalletUiState.Loading) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = WalletUiState.Loading
            )

    fun upsertWallet(wallet: Wallet) {
        viewModelScope.launch {
            walletsRepository.upsertWallet(wallet)
        }
    }

    fun deleteWallet(id: String) {
        viewModelScope.launch {
            walletsRepository.deleteWallet(id)
        }
    }
}

sealed interface WalletUiState {

    data object Loading : WalletUiState

    data class Success(
        val walletWithTransactionsAndCategories: WalletWithTransactionsAndCategories
    ) : WalletUiState
}