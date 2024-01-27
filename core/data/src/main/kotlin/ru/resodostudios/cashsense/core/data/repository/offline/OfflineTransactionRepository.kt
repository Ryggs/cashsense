package ru.resodostudios.cashsense.core.data.repository.offline

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.resodostudios.cashsense.core.data.model.asEntity
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.database.dao.TransactionDao
import ru.resodostudios.cashsense.core.database.model.asExternalModel
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.TransactionCategoryCrossRef
import javax.inject.Inject

class OfflineTransactionRepository @Inject constructor(
    private val dao: TransactionDao
) : TransactionsRepository {
    override fun getTransaction(id: String): Flow<Transaction> =
        dao.getTransactionEntity(id).map { it.asExternalModel() }

    override suspend fun upsertTransaction(transaction: Transaction) =
        dao.upsertTransaction(transaction.asEntity())

    override suspend fun deleteTransaction(transaction: Transaction) =
        dao.deleteTransaction(transaction.asEntity())

    override suspend fun upsertTransactionCategoryCrossRef(crossRef: TransactionCategoryCrossRef) =
        dao.upsertTransactionCategoryCrossRef(crossRef.asEntity())
}