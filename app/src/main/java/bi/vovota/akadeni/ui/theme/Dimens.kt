package bi.vovota.akadeni.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object FontSizes {
  @Composable
  fun h1(): TextUnit = responsiveFontSize(small = 22.sp, medium = 26.sp, large = 30.sp)

  @Composable
  fun title(): TextUnit = responsiveFontSize(small = 18.sp, medium = 20.sp, large = 24.sp)

  @Composable
  fun subtitle(): TextUnit = responsiveFontSize(small = 16.sp, medium = 18.sp, large = 20.sp)

  @Composable
  fun body(): TextUnit = responsiveFontSize(small = 14.sp, medium = 16.sp, large = 18.sp)

  @Composable
  fun caption(): TextUnit = responsiveFontSize(small = 10.sp, medium = 12.sp, large = 14.sp)
}

object Spacings {
  @Composable
  fun xs(): Dp = responsiveSpacing(small = 4.dp, medium = 6.dp, large = 8.dp)

  @Composable
  fun sm(): Dp = responsiveSpacing(small = 8.dp, medium = 12.dp, large = 16.dp)

  @Composable
  fun md(): Dp = responsiveSpacing(small = 12.dp, medium = 16.dp, large = 20.dp)

  @Composable
  fun lg(): Dp = responsiveSpacing(small = 16.dp, medium = 20.dp, large = 24.dp)

  @Composable
  fun xl(): Dp = responsiveSpacing(small = 20.dp, medium = 28.dp, large = 36.dp)
}

// 🔧 Private helpers
@Composable
private fun responsiveFontSize(small: TextUnit, medium: TextUnit, large: TextUnit): TextUnit {
  val width = LocalConfiguration.current.screenWidthDp
  return when {
    width < 360 -> small
    width < 600 -> medium
    else -> large
  }
}

@Composable
private fun responsiveSpacing(small: Dp, medium: Dp, large: Dp): Dp {
  val width = LocalConfiguration.current.screenWidthDp
  return when {
    width < 360 -> small
    width < 600 -> medium
    else -> large
  }
}