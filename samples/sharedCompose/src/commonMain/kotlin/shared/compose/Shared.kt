package shared.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberImagePainter
import com.utsman.composeremote.CustomNodes
import com.utsman.composeremote.DynamicLayout

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

        CustomNodes.register("grid") { param ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = param.modifier,
                horizontalArrangement = Arrangement.spacedBy((param.data["spacing"]?.toInt() ?: 0).dp),
            ) {
                param.children?.let { wrapper ->
                    wrapper.forEach { child ->
                        val component = child.component
                        item {
                            DynamicLayout(
                                component = component,
                                onClickHandler = param.onClickHandler,
                                bindValue = param.bindsValue,
                            )
                        }
                    }
                }
            }
        }

        CustomNodes.register("card_item") { param ->
            Card(
                modifier = param.modifier.then(
                    if (param.data["clickId"] != null) {
                        Modifier.clickable {
                            param.onClickHandler(param.data["clickId"] ?: "")
                        }
                    } else {
                        Modifier
                    },
                ),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                        .padding(12.dp),
                ) {
                    Text(
                        text = param.data["title"] ?: "unknown",
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = param.data["message"] ?: "unknown",
                    )
                }
            }
        }
    }
}
