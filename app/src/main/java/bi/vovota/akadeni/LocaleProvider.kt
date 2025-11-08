package bi.vovota.akadeni

import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf

val LocalLocalizedContext = staticCompositionLocalOf<Context> {
  error("No LocalLocalizedContext provided")
}