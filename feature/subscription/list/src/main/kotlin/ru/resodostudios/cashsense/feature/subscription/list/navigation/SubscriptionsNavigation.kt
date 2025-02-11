package ru.resodostudios.cashsense.feature.subscription.list.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.core.notifications.DEEP_LINK_SCHEME_AND_HOST
import ru.resodostudios.cashsense.core.notifications.SUBSCRIPTIONS_PATH
import ru.resodostudios.cashsense.feature.subscription.list.SubscriptionsScreen

@Serializable
object SubscriptionsDestination

private const val DEEP_LINK_BASE_PATH = "$DEEP_LINK_SCHEME_AND_HOST/$SUBSCRIPTIONS_PATH"

fun NavController.navigateToSubscriptions(navOptions: NavOptions) =
    navigate(route = SubscriptionsDestination, navOptions)

fun NavGraphBuilder.subscriptionsScreen(
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable<SubscriptionsDestination>(
        deepLinks = listOf(
            navDeepLink<SubscriptionsDestination>(basePath = DEEP_LINK_BASE_PATH),
        )
    ) {
        SubscriptionsScreen(onShowSnackbar)
    }
}