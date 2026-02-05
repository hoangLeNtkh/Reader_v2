package com.example.reader_v2.ui.screen.reader

import android.webkit.WebView

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
