package uz.gita.notesapp.navigation

import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

object AppNavigationDispatcher : AppNavigator, AppNavigationHandler {

    // A channel keeps commands until the Navigator collects them. A SharedFlow without
    // subscribers drops them, which lost the first navigation when the splash finished
    // before the UI started collecting.
    private val commands = Channel<AppNavigationParam>(Channel.BUFFERED)

    override val backStack: Flow<AppNavigationParam> = commands.receiveAsFlow()

    private suspend fun navigate(param: AppNavigationParam) {
        commands.send(param)
    }

    override suspend fun navigateTo(screen: Screen) = navigate { push(screen) }

    override suspend fun replaceTo(screen: Screen) = navigate { replace(screen) }

    override suspend fun replaceAll(screen: Screen) = navigate { replaceAll(screen) }

    override suspend fun back() = navigate { pop() }
}
