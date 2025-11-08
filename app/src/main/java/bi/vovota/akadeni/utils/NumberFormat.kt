package bi.vovota.akadeni.utils

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
fun Float.clean(): String =
  if (this % 1f == 0f)
    this.toInt().toString()
  else
    String.format("%.2f", this)

@SuppressLint("DefaultLocale")
fun Double.clean(): String =
  if (this % 1.0 == 0.0)
    this.toInt().toString()
  else
    String.format("%.2f", this)


