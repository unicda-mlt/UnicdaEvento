package com.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import java.util.UUID

private enum class DownloadState {
    LOADING, ERROR, SUCCESS
}

@Composable
fun RandomPicsumThumb(
    modifier: Modifier = Modifier,
    contentDescription: String = "Random Picsum Image",
    widthDp: Int = 96,
    heightDp: Int = 96,
    contentScale: ContentScale = ContentScale.Fit,
    @DrawableRes errorResId: Int
) {
    val ctx = LocalContext.current

    val cacheBuster = rememberSaveable { UUID.randomUUID().toString() }

    val url = remember(widthDp, heightDp, cacheBuster) {
        "https://picsum.photos/$widthDp/$heightDp?random=$cacheBuster"
    }

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(ctx)
            .data(url)
            .crossfade(true)
            .build()
    )

    Box(modifier = modifier) {
        Image(
            painter = painter,
            contentDescription,
            contentScale = contentScale
        )

        when (painter.state) {
            is AsyncImagePainter.State.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(strokeWidth = 2.dp)
                }
            }
            is AsyncImagePainter.State.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = errorResId),
                        contentDescription = "Default image",
                        contentScale = contentScale,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            else -> Unit
        }
    }
}