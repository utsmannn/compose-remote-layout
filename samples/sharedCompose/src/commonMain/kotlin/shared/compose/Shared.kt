package shared.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberImagePainter
import com.utsman.composeremote.CustomNodes

object Shared {
    fun registerCustomNode() {
        CustomNodes.register("image") { param ->
            val url = param.data["url"] ?: ""
            if (url.isNotEmpty()) {
                Image(
                    modifier = param.modifier,
                    painter = rememberImagePainter(url),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )
            }
        }

        CustomNodes.register("banner") { param ->
            Row(
                modifier = param.modifier.then(
                    Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = Color.Black.copy(alpha = 0.3f)),
                ),
            ) {
                val url = param.data["url"] ?: ""
                if (url.isNotEmpty()) {
                    Image(
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f)
                            .background(color = Color.Red),
                        painter = rememberImagePainter(url),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.padding(8.dp),
                ) {
                    Text(
                        text = param.data["title"] ?: "unknown",
                        style = MaterialTheme.typography.h4,
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp),
                    )

                    Text(
                        text = param.data["message"] ?: "unknown",
                    )
                }
            }
        }
    }
}
