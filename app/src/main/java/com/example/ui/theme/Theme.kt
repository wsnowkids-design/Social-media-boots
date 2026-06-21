package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val SocialDarkColorScheme = darkColorScheme(
  primary = ElectricBlue,
  onPrimary = MidnightBlack,
  secondary = NeonBlue,
  onSecondary = BrightWhite,
  tertiary = BrightWhite,
  onTertiary = MidnightBlack,
  background = MidnightBlack,
  onBackground = SoftIcyBlueText,
  surface = SlateBlueCard,
  onSurface = SoftIcyBlueText,
  surfaceVariant = LightSlateBlue,
  onSurfaceVariant = SecondaryIcyBlue
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force our high-contrast modern dark-theme by default
  dynamicColor: Boolean = false, // Disable dynamic colors so our intentional branding colors shine!
  content: @Composable () -> Unit,
) {
  val colorScheme = SocialDarkColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
