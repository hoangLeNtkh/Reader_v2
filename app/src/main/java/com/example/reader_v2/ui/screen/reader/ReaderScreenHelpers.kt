package com.example.reader_v2.ui.screen.reader

import android.webkit.WebView

sealed class ScrollTarget {
    object None : ScrollTarget()

    data class Anchor(
        val id: String,
    ) : ScrollTarget()

    data class Position(
        val percentage: Float,
    ) : ScrollTarget()
}

data class ChapterContent(
    val url: String,
    val target: ScrollTarget = ScrollTarget.None,
)

fun WebView.executeScroll(target: ScrollTarget) {
    when (target) {
        is ScrollTarget.Anchor -> {
            evaluateJavascript("document.getElementById('${target.id}')?.scrollIntoView({behavior: 'smooth'});", null)
        }
        is ScrollTarget.Position -> {
            val density = resources.displayMetrics.density
            val maxScrollY = (contentHeight * density) - height
            if (maxScrollY > 0) {
                scrollTo(0, (maxScrollY * target.percentage).toInt())
            }
        }
        is ScrollTarget.None -> { /* Stay at top */ }
    }
}
