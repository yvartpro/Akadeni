package bi.vovota.akadeni.utils

object InputValidator {
  private val emailRegx = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9._]+\\.[A-Za-z]{2,6}")
  fun isValidEmail(email: String): Boolean = emailRegx.matches(email.trim())
}