package ru.resodostudios.cashsense.core.notifications

import android.Manifest.permission
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaZoneId
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.Subscription
import ru.resodostudios.cashsense.core.ui.formatAmount
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

private const val TARGET_ACTIVITY_NAME = "ru.resodostudios.cashsense.MainActivity"
private const val SUBSCRIPTIONS_NOTIFICATION_REQUEST_CODE = 0
private const val SUBSCRIPTIONS_NOTIFICATION_CHANNEL_ID = ""
private const val SUBSCRIPTIONS_NOTIFICATION_GROUP = "SUBSCRIPTIONS_NOTIFICATIONS"
const val DEEP_LINK_SCHEME_AND_HOST = "https://www.resodostudios.ru/cashsense"
const val SUBSCRIPTIONS_PATH = "subscriptions"

/**
 * Implementation of [Notifier] that displays notifications in the system tray.
 */
@Singleton
internal class SystemTrayNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) : Notifier {

    override fun postSubscriptionNotification(
        subscription: Subscription,
    ) = with(context) {
        if (checkSelfPermission(this, permission.POST_NOTIFICATIONS) != PERMISSION_GRANTED) return

        val subscriptionNotification = createSubscriptionNotification {
            val price = subscription.amount.formatAmount(subscription.currency)
            val date = subscription.paymentDate.formatDate()
            val contentText = getString(R.string.core_notifications_subscriptions_content_text, price, date)
            setSmallIcon(CsIcons.Payments)
                .setContentTitle(subscription.title)
                .setContentText(contentText)
                .setContentIntent(subscriptionPendingIntent())
                .setGroup(SUBSCRIPTIONS_NOTIFICATION_GROUP)
                .setAutoCancel(true)
        }

        // Send the notification
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(
            subscription.id.hashCode(),
            subscriptionNotification,
        )
    }
}

/**
 * Creates a notification for configured for subscription
 */
private fun Context.createSubscriptionNotification(
    block: NotificationCompat.Builder.() -> Unit,
): Notification {
    ensureNotificationChannelExists()
    return NotificationCompat.Builder(
        this,
        SUBSCRIPTIONS_NOTIFICATION_CHANNEL_ID,
    )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .apply(block)
        .build()
}

/**
 * Ensures that a notification channel is present if applicable
 */
private fun Context.ensureNotificationChannelExists() {
    if (VERSION.SDK_INT < VERSION_CODES.O) return

    val channel = NotificationChannel(
        SUBSCRIPTIONS_NOTIFICATION_CHANNEL_ID,
        getString(R.string.core_notifications_subscriptions_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(R.string.core_notifications_subscriptions_notification_channel_description)
    }
    // Register the channel with the system
    NotificationManagerCompat.from(this).createNotificationChannel(channel)
}

private fun Context.subscriptionPendingIntent(): PendingIntent? = PendingIntent.getActivity(
    this,
    SUBSCRIPTIONS_NOTIFICATION_REQUEST_CODE,
    Intent().apply {
        action = Intent.ACTION_VIEW
        data = "$DEEP_LINK_SCHEME_AND_HOST/$SUBSCRIPTIONS_PATH".toUri()
        component = ComponentName(
            packageName,
            TARGET_ACTIVITY_NAME,
        )
    },
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
)

private fun Instant.formatDate(): String =
    DateTimeFormatter
        .ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(Locale.getDefault())
        .withZone(TimeZone.currentSystemDefault().toJavaZoneId())
        .format(toJavaInstant())