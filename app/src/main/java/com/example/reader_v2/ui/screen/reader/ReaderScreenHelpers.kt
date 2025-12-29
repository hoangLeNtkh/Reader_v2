package com.example.reader_v2.ui.screen.reader

import android.webkit.WebView

/** Handling WebView's scrolling behavior */
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

/** Handling Webview settings */
data class ReaderSettings(
    val fontSize: Int = 18,
    val fontFamily: String = "serif",
    val lineHeight: Float = 1.5f,
    val theme: ReaderTheme = ReaderTheme.Light,
    val horizontalMargin: Int = 16,
)

enum class ReaderTheme(
    val backgroundColor: String,
    val textColor: String,
) {
    Light("#FFFFFF", "#121212"),
    Dark("#121212", "#E0E0E0"),
    Sepia("#F4ECD8", "#5B4636"),
}

fun WebView.applyReaderSettings(settings: ReaderSettings) {
    val css =
        """
        body {
            background-color: ${settings.theme.backgroundColor} !important;
            color: ${settings.theme.textColor} !important;
            font-size: ${settings.fontSize}px !important;
            font-family: ${settings.fontFamily} !important;
            line-height: ${settings.lineHeight} !important;
            padding: 0 ${settings.horizontalMargin}px !important;
        }
        /* Ensure images don't overflow */
        img { max-width: 100% !important; height: auto !important; }
        """.trimIndent()

    val js =
        """
        (function() {
            var style = document.getElementById('reader-settings-style');
            if (!style) {
                style = document.createElement('style');
                style.id = 'reader-settings-style';
                document.head.appendChild(style);
            }
            style.innerHTML = `$css`;
        })()
        """.trimIndent()

    this.evaluateJavascript(js, null)
}
