package com.util

import java.text.Normalizer
import java.util.Locale


fun normalizeTextKeepEnye(input: String): String {
    var text = input.replace("ñ", "__enye__").replace("Ñ", "__ENYE__")

    // Normalize (decomposes accented characters)
    text = Normalizer.normalize(text, Normalizer.Form.NFD)

    // Remove diacritics (accents)
    text = text.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")

    text = text.replace("__enye__", "ñ").replace("__ENYE__", "Ñ")

    return text.lowercase(Locale.getDefault())
}