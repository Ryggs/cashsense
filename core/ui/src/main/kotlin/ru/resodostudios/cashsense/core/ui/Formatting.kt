package ru.resodostudios.cashsense.core.ui

import androidx.compose.runtime.Composable
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toJavaZoneId
import kotlinx.datetime.toLocalDateTime
import ru.resodostudios.cashsense.core.ui.FormatDateType.DATE
import ru.resodostudios.cashsense.core.ui.FormatDateType.DATE_TIME
import ru.resodostudios.cashsense.core.ui.FormatDateType.TIME
import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Currency
import java.util.Locale

fun BigDecimal.formatAmount(currency: String, withPlus: Boolean = false): String {
    val currencyFormat = DecimalFormat.getCurrencyInstance(Locale.getDefault())
    val customCurrency = Currency.getInstance(currency)
    currencyFormat.currency = customCurrency

    val formattedAmount = currencyFormat.format(this)

    return if (withPlus && this > BigDecimal.ZERO) "+$formattedAmount" else formattedAmount
}

@Composable
fun Instant.formatDate(formatDateType: FormatDateType = DATE): String = when (formatDateType) {
    DATE_TIME -> DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
    DATE -> DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    TIME -> DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)
}
    .withLocale(Locale.getDefault())
    .withZone(LocalTimeZone.current.toJavaZoneId())
    .format(toJavaInstant())

fun Instant.getZonedDateTime() =
    toLocalDateTime(TimeZone.currentSystemDefault()).toJavaLocalDateTime()

fun getCurrentZonedDateTime() =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toJavaLocalDateTime()

enum class FormatDateType {
    DATE_TIME,
    DATE,
    TIME,
}