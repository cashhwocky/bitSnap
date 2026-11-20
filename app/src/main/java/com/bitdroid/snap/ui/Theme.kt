package com.bitdroid.snap.ui

import android.content.Context
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.bitdroid.snap.R

/**
 * bitSnap theme. Same Bloom Home / Material You system as bitMessages, trimmed to what a wrapper
 * needs. Lilac on a purple Bloom backdrop by default; a true-black lilac option and a Material You
 * (wallpaper) option are available and swap the whole app live.
 */
object Bit {
    var palette by mutableStateOf(Themes.BLOOM)

    fun apply(context: Context) {
        palette = Themes.palette(context, Themes.current(context))
    }

    val Bg: Color get() = palette.bg
    val Accent: Color get() = palette.accent
    val OnAccent: Color get() = palette.onAccent
    val Surface: Color get() = palette.surface
    val SurfaceHigh: Color get() = palette.surfaceHigh
    val Raised: Color get() = palette.raised
    val Outline: Color get() = palette.outline
    val Hairline: Color get() = palette.hairline
    val Text = Color(0xFFFFFFFF)
    val TextDim: Color get() = palette.textDim
    val Error = Color(0xFFFFB4AB)

    val expressive: Boolean get() = palette.expressive
    val Lav: Color get() = palette.lav
    val Lilac get() = Accent

    val bouncy = spring<Float>(dampingRatio = 0.62f, stiffness = Spring.StiffnessMediumLow)
}

@OptIn(ExperimentalTextApi::class)
private fun variable(res: Int, vararg weights: Int): FontFamily = FontFamily(
    weights.map { w ->
        Font(res, FontWeight(w), variationSettings = FontVariation.Settings(FontVariation.weight(w)))
    },
)

val Display: FontFamily = variable(R.font.unbounded, 600, 700, 800, 900)
val Mono: FontFamily = variable(R.font.jetbrains_mono, 500, 700)
val Body: FontFamily = variable(R.font.inter, 300, 400, 500, 600, 700, 800)

data class Palette(
    val bg: Color,
    val accent: Color,
    val onAccent: Color,
    val surface: Color,
    val surfaceHigh: Color,
    val raised: Color,
    val outline: Color,
    val hairline: Color,
    val textDim: Color,
    val lav: Color,
    val expressive: Boolean = false,
)

object Themes {
    data class Choice(val key: String, val label: String)

    val all = listOf(
        Choice("bloom", "BLOOM HOME"),
        Choice("lilac", "LILAC AMOLED"),
        Choice("you", "MATERIAL YOU"),
    )

    /** Matches the bitDroid Bloom home screen: lavender on purple tiles over a purple-to-teal glow. */
    val BLOOM = Palette(
        bg = Color(0xFF120F1C), accent = Color(0xFFCBC2EB), onAccent = Color(0xFF241C4A),
        surface = Color(0xFF2E2842), surfaceHigh = Color(0xFF3A3352), raised = Color(0xFF463F5E),
        outline = Color(0xFF6A6188), hairline = Color(0xFF2A2540), textDim = Color(0xFFB9B0D6),
        lav = Color(0xFFCBC2EB), expressive = true,
    )

    /** True-black AMOLED with the Material 3 lilac accent. */
    val LILAC = Palette(
        bg = Color(0xFF000000), accent = Color(0xFFEADDFF), onAccent = Color(0xFF21005D),
        surface = Color(0xFF0C0B10), surfaceHigh = Color(0xFF16151C), raised = Color(0xFF1A1920),
        outline = Color(0xFF2E2C38), hairline = Color(0xFF1A1920), textDim = Color(0xFFA9A6B4),
        lav = Color(0xFFEADDFF),
    )

    private const val PREFS = "bitsnap"

    fun current(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("theme", "bloom") ?: "bloom"

    fun set(context: Context, key: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("theme", key).apply()
        Bit.palette = palette(context, key)
    }

    fun palette(context: Context, key: String): Palette = when (key) {
        "lilac" -> LILAC
        "you" -> {
            val d = dynamicDarkColorScheme(context)
            Palette(
                bg = d.surfaceContainer, accent = d.primary, onAccent = d.onPrimary,
                surface = d.surfaceContainerHigh, surfaceHigh = d.surfaceContainerHighest, raised = d.surfaceContainerHighest,
                outline = d.outlineVariant, hairline = d.surfaceContainerHighest, textDim = d.onSurfaceVariant,
                lav = d.primary,
            )
        }
        else -> BLOOM
    }
}

private fun scheme() = darkColorScheme(
    primary = Bit.Accent,
    onPrimary = Bit.OnAccent,
    primaryContainer = Bit.Accent,
    onPrimaryContainer = Bit.OnAccent,
    secondary = Bit.Accent,
    onSecondary = Bit.OnAccent,
    background = Bit.Bg,
    onBackground = Bit.Text,
    surface = Bit.Bg,
    onSurface = Bit.Text,
    surfaceVariant = Bit.SurfaceHigh,
    onSurfaceVariant = Bit.TextDim,
    surfaceContainer = Bit.Surface,
    surfaceContainerHigh = Bit.SurfaceHigh,
    surfaceContainerHighest = Bit.SurfaceHigh,
    outline = Bit.TextDim,
    outlineVariant = Bit.Hairline,
    error = Bit.Error,
)

private val base = Typography()
private val typography = Typography(
    displaySmall = base.displaySmall.copy(fontFamily = Display, fontWeight = FontWeight.Black),
    headlineMedium = base.headlineMedium.copy(fontFamily = Display, fontWeight = FontWeight.ExtraBold),
    titleLarge = base.titleLarge.copy(fontFamily = Display, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp),
    titleMedium = base.titleMedium.copy(fontFamily = Body, fontWeight = FontWeight.SemiBold),
    bodyLarge = base.bodyLarge.copy(fontFamily = Body, fontSize = 16.sp, lineHeight = 22.sp),
    bodyMedium = base.bodyMedium.copy(fontFamily = Body),
    labelLarge = base.labelLarge.copy(fontFamily = Body, fontWeight = FontWeight.Bold),
    labelSmall = base.labelSmall.copy(fontFamily = Mono),
)

@Composable
fun BitTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = scheme(), typography = typography) {
        CompositionLocalProvider(
            LocalTextStyle provides TextStyle(fontFamily = Body, color = Bit.Text),
            content = content,
        )
    }
}

fun mono(size: Float, weight: FontWeight = FontWeight.Bold, color: Color = Bit.TextDim, spacing: Float = 0.8f) =
    TextStyle(fontFamily = Mono, fontSize = size.sp, fontWeight = weight, color = color, letterSpacing = spacing.sp)

val BloomGradient: Brush
    get() = Brush.linearGradient(listOf(Color(0xFF6E5AFA), Color(0xFFC85ADC)))
