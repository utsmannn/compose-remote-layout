package router.app

import androidx.compose.runtime.Composable

@Composable
expect fun BackPress(
    enable: Boolean = true,
    onBack: () -> Unit,
)
