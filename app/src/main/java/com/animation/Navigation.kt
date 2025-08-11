package com.animation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally


sealed class NavigationAnimation {
    companion object {
        // PUSH: new screen slides in from right → left
        val SlideInHorizontalEnter = slideInHorizontally(tween(400)) { fullWidth ->  fullWidth }
        val SlideInHorizontalExit = slideOutHorizontally(tween(400)) { fullWidth -> -fullWidth }

        // POP: previous screen slides in from left → right (opposite)
        val SlideInHorizontalPopEnter = slideInHorizontally(animationSpec = tween(400)) { fullWidth -> -fullWidth }
        val SlideInHorizontalPopExit = slideOutHorizontally(animationSpec = tween(400)) { fullWidth ->  fullWidth }
    }
}
