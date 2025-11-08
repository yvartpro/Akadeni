package bi.vovota.akadeni.utils

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import bi.vovota.akadeni.LocalLocalizedContext
import java.util.Locale

object LocaleHelper {
  fun setLocale(context: Context, lang: String? = null) : Context {
    val locale = Locale(lang)
    Locale.setDefault(locale)

    val config = context.resources.configuration
    config.setLocale(locale)
    config.setLayoutDirection(locale)

    return context.createConfigurationContext(config)
  }
}

@Composable
fun localizedString(@StringRes id: Int, vararg args: Any): String {
  val context = LocalLocalizedContext.current
  val formattedArgs = args.map {
    when (it) {
      is Float -> it.clean()
      is Double -> it.clean()
      else -> it
    }
  }.toTypedArray()
  return context.getString(id, *formattedArgs)
}