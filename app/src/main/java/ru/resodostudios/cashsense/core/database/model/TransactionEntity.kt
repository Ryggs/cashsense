package ru.resodostudios.cashsense.core.database.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.resodostudios.cashsense.feature.wallet.domain.model.Transaction
import java.time.LocalDateTime

@Entity(
    tableName = "transactions",
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val category: String,
    val value: Int,
    @Embedded
    val timestamp: LocalDateTime = LocalDateTime.now()
)

fun TransactionEntity.asExternalModel() = Transaction(
    id = id,
    name = name,
    category = category,
    value = value,
    timestamp = timestamp
)