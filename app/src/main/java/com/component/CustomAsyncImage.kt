package com.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Precision
import coil.size.Scale
import com.example.unicdaevento.R


@Composable
fun CustomAsyncImage(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    width: Dp = 100.dp,
    height: Dp = 100.dp,
    imageUrl: String? = null,
    contentDescription: String = "Custom AsyncImage",
    contentScale: ContentScale = ContentScale.Fit
) {
    val ctx = LocalContext.current
    val density = LocalDensity.current
    val widthPx = remember(width, density) { with(density) { width.roundToPx() } }
    val heightPx = remember(height, density) { with(density) { height.roundToPx() } }

    if (LocalInspectionMode.current) {
        Box(modifier = modifier.size(width, height), contentAlignment = Alignment.Center) {
            ErrorImage(contentScale)
        }
        return
    }

    val request = remember(imageUrl, widthPx, heightPx) {
        ImageRequest.Builder(ctx)
            .data(imageUrl)
            .size(widthPx, heightPx)
            .scale(Scale.FILL)
            .precision(Precision.INEXACT)
            .crossfade(false)
            .build()
    }

    val painter = rememberAsyncImagePainter(model = request)

    Column (
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment
    ) {
        Image(
            painter = painter,
            contentDescription,
            contentScale = contentScale
        )

        when (painter.state) {
            is AsyncImagePainter.State.Loading -> {
                CustomCircularProgressIndicator()
            }
            is AsyncImagePainter.State.Error -> {
                ErrorImage(contentScale)
            }
            else -> Unit
        }
    }
}

@Composable
private fun ErrorImage(
    contentScale: ContentScale
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.image_break),
            contentDescription = "Default image",
            contentScale = contentScale,
            modifier = Modifier.fillMaxSize(),
        )
    }
}