package app.worthy.android.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private data class Family(
    val primaryLight: Long, val onPrimaryLight: Long, val primaryContainerLight: Long, val onPrimaryContainerLight: Long,
    val secondaryLight: Long, val secondaryContainerLight: Long, val tertiaryLight: Long, val tertiaryContainerLight: Long,
    val backgroundLight: Long, val onSurfaceLight: Long, val surfaceVariantLight: Long, val onSurfaceVariantLight: Long,
    val outlineLight: Long, val outlineVariantLight: Long, val inverseSurfaceLight: Long,
    val primaryDark: Long, val onPrimaryDark: Long, val primaryContainerDark: Long, val secondaryDark: Long,
    val secondaryContainerDark: Long, val tertiaryDark: Long, val tertiaryContainerDark: Long,
    val backgroundDark: Long, val onSurfaceDark: Long, val surfaceVariantDark: Long, val onSurfaceVariantDark: Long,
    val outlineDark: Long,
)

private fun c(value: Long) = Color(value)

private fun Family.lightScheme(): ColorScheme = lightColorScheme(
    primary = c(primaryLight), onPrimary = c(onPrimaryLight), primaryContainer = c(primaryContainerLight), onPrimaryContainer = c(onPrimaryContainerLight),
    secondary = c(secondaryLight), onSecondary = Color.White, secondaryContainer = c(secondaryContainerLight), onSecondaryContainer = c(onPrimaryContainerLight),
    tertiary = c(tertiaryLight), onTertiary = Color.White, tertiaryContainer = c(tertiaryContainerLight), onTertiaryContainer = c(onPrimaryContainerLight),
    background = c(backgroundLight), onBackground = c(onSurfaceLight), surface = c(backgroundLight), onSurface = c(onSurfaceLight),
    surfaceVariant = c(surfaceVariantLight), onSurfaceVariant = c(onSurfaceVariantLight), outline = c(outlineLight), outlineVariant = c(outlineVariantLight),
    inverseSurface = c(inverseSurfaceLight), inverseOnSurface = c(backgroundLight), inversePrimary = c(primaryDark),
    error = c(0xFFBA1A1A), onError = Color.White, errorContainer = c(0xFFFFDAD6), onErrorContainer = c(0xFF410002),
    surfaceDim = c(surfaceVariantLight), surfaceBright = c(backgroundLight), surfaceContainerLowest = Color.White,
    surfaceContainerLow = c(backgroundLight), surfaceContainer = c(surfaceVariantLight), surfaceContainerHigh = c(surfaceVariantLight), surfaceContainerHighest = c(outlineVariantLight),
)

private fun Family.darkScheme(): ColorScheme = darkColorScheme(
    primary = c(primaryDark), onPrimary = c(onPrimaryDark), primaryContainer = c(primaryContainerDark), onPrimaryContainer = c(primaryContainerLight),
    secondary = c(secondaryDark), onSecondary = c(backgroundDark), secondaryContainer = c(secondaryContainerDark), onSecondaryContainer = c(secondaryContainerLight),
    tertiary = c(tertiaryDark), onTertiary = c(backgroundDark), tertiaryContainer = c(tertiaryContainerDark), onTertiaryContainer = c(tertiaryContainerLight),
    background = c(backgroundDark), onBackground = c(onSurfaceDark), surface = c(backgroundDark), onSurface = c(onSurfaceDark),
    surfaceVariant = c(surfaceVariantDark), onSurfaceVariant = c(onSurfaceVariantDark), outline = c(outlineDark), outlineVariant = c(surfaceVariantDark),
    inverseSurface = c(onSurfaceDark), inverseOnSurface = c(inverseSurfaceLight), inversePrimary = c(primaryLight),
    error = c(0xFFFFB4AB), onError = c(0xFF690005), errorContainer = c(0xFF93000A), onErrorContainer = c(0xFFFFDAD6),
    surfaceDim = c(backgroundDark), surfaceBright = c(surfaceVariantDark), surfaceContainerLowest = c(backgroundDark),
    surfaceContainerLow = c(backgroundDark), surfaceContainer = c(inverseSurfaceLight), surfaceContainerHigh = c(surfaceVariantDark), surfaceContainerHighest = c(surfaceVariantDark),
)

private val RedFamily = Family(
    0xFF9C4145, 0xFFFFFFFF, 0xFFFFDADB, 0xFF40000A, 0xFF765657, 0xFFFFDADB, 0xFF755A2F, 0xFFFFDEA6,
    0xFFFFF8F8, 0xFF211A1B, 0xFFF3DDDE, 0xFF524344, 0xFF857374, 0xFFD7C1C2, 0xFF362F30,
    0xFFFFB3B5, 0xFF5F131B, 0xFF7E2A2F, 0xFFE6BDBE, 0xFF5D3F40, 0xFFE5C18D, 0xFF5B421A,
    0xFF191113, 0xFFF0DEE0, 0xFF524344, 0xFFD7C1C2, 0xFFA08C8D,
)
private val BlueFamily = Family(
    0xFF49618A, 0xFFFFFFFF, 0xFFD6E3FF, 0xFF001B3E, 0xFF565F71, 0xFFDAE2F9, 0xFF705574, 0xFFFAD8FD,
    0xFFF9F9FF, 0xFF191C20, 0xFFE1E2EC, 0xFF44474F, 0xFF74777F, 0xFFC4C6D0, 0xFF2E3036,
    0xFFB0C6F9, 0xFF17325B, 0xFF31496F, 0xFFBEC6DC, 0xFF3E4759, 0xFFDDB9DD, 0xFF573E5C,
    0xFF111318, 0xFFE1E2E9, 0xFF44474F, 0xFFC4C6D0, 0xFF8E9099,
)
private val GreenFamily = Family(
    0xFF356A4F, 0xFFFFFFFF, 0xFFB8F1D0, 0xFF002114, 0xFF4E6356, 0xFFD1E8D8, 0xFF3D6471, 0xFFC1E9F9,
    0xFFF6FBF6, 0xFF171D19, 0xFFDCE5DD, 0xFF404942, 0xFF707972, 0xFFC0C9C1, 0xFF2C322E,
    0xFF9CD4B5, 0xFF043824, 0xFF1C5139, 0xFFB5CCBD, 0xFF374B3F, 0xFFA5CDDC, 0xFF244D59,
    0xFF0F1511, 0xFFDEE3DE, 0xFF404942, 0xFFC0C9C1, 0xFF8A938B,
)
private val OrangeFamily = Family(
    0xFF8B4F18, 0xFFFFFFFF, 0xFFFFDCC2, 0xFF2D1600, 0xFF735944, 0xFFFFDCC2, 0xFF5B6238, 0xFFDFE7AF,
    0xFFFFF8F4, 0xFF201A17, 0xFFF3DFD2, 0xFF51443B, 0xFF837469, 0xFFD6C3B6, 0xFF362F2B,
    0xFFFFB77C, 0xFF4B2800, 0xFF6B3A05, 0xFFE2C0A7, 0xFF594331, 0xFFC3CB91, 0xFF444A22,
    0xFF18120E, 0xFFEDE0D9, 0xFF51443B, 0xFFD6C3B6, 0xFF9E8E83,
)
private val PinkFamily = Family(
    0xFF94416B, 0xFFFFFFFF, 0xFFFFD8E7, 0xFF3D0025, 0xFF745663, 0xFFFFD8E7, 0xFF7C5734, 0xFFFFDCC0,
    0xFFFFF8FA, 0xFF211A1D, 0xFFF2DDE4, 0xFF514349, 0xFF83737A, 0xFFD5C2C8, 0xFF362F31,
    0xFFFFAFD0, 0xFF5B113D, 0xFF772854, 0xFFE3BDCB, 0xFF5B3F4B, 0xFFEEC18F, 0xFF623F1F,
    0xFF191114, 0xFFEDDFE3, 0xFF514349, 0xFFD5C2C8, 0xFF9E8D94,
)

internal val RedLightScheme = RedFamily.lightScheme()
internal val RedDarkScheme = RedFamily.darkScheme()
internal val BlueLightScheme = BlueFamily.lightScheme()
internal val BlueDarkScheme = BlueFamily.darkScheme()
internal val GreenLightScheme = GreenFamily.lightScheme()
internal val GreenDarkScheme = GreenFamily.darkScheme()
internal val OrangeLightScheme = OrangeFamily.lightScheme()
internal val OrangeDarkScheme = OrangeFamily.darkScheme()
internal val PinkLightScheme = PinkFamily.lightScheme()
internal val PinkDarkScheme = PinkFamily.darkScheme()
