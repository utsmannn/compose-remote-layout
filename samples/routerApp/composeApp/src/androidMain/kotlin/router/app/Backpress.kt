package router.app

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun BackPress(
    enable: Boolean,
    onBack: () -> Unit,
) {
    BackHandler(enable, onBack)
}
