package sample.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.utsman.composeremote.CustomNodes

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        CustomNodes.register("banner") { param ->
            Column(
                modifier = param.modifier,
            ) {
                Text(
                    text = param.data["title"] ?: "unknown",
                    style = MaterialTheme.typography.h2,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = param.data["message"] ?: "unknown",
                )
            }
        }

        setContent {
            App()
        }
    }
}
