package aelsi2.compose.material3

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp


@Composable
fun rememberInlineIcons(
    icons: Map<Int, Pair<Int, Int>>,
    iconSize: TextUnit = 14.sp,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant
): Map<String, InlineTextContent> {
    return remember(icons, iconSize, iconTint) {
        val map: MutableMap<String, InlineTextContent> = mutableMapOf()
        for (entry in icons) {
            map[entry.key.toString()] = InlineTextContent(
                Placeholder(
                    width = iconSize,
                    height = iconSize,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                Icon(
                    painter = painterResource(entry.value.first),
                    contentDescription = stringResource(entry.value.second),
                    tint = iconTint
                )
            }
        }
        map
    }
}

@Composable
fun rememberStringWithInlineContent(id: Int): AnnotatedString {
    val rawText = stringResource(id)
    return remember(rawText) {
        getStringWithInlineContent(rawText)
    }
}

private val iconPlaceholderNumberRegex = Regex("(?<!\\\\)(?<=\\\$inline\\[)[0-9]{1,5}(?=])")
private val iconPlaceholderSplitRegex = Regex("((?<!\\\\)(?<=\\\$inline\\[[0-9]{1,5}]))|((?!\\\\)(?=\\\$inline\\[[0-9]{1,5}]))")
private val escapeRegex = Regex("\\\\(?=\\\$inline)")

fun getStringWithInlineContent(rawString: String): AnnotatedString = buildAnnotatedString {
    val strings = iconPlaceholderSplitRegex.split(rawString)
    for (string in strings) {
        val match = iconPlaceholderNumberRegex.find(string)
        if (match != null) {
            appendInlineContent(match.value)
        }
        else {
            append(escapeRegex.replace(string, ""))
        }
    }
}