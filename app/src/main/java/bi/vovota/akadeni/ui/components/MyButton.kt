package bi.vovota.akadeni.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bi.clubtechlac.moxcred.ui.app.components.LoadingDots

@Composable
fun MyButton(
  modifier: Modifier = Modifier,
  text: String,
  isLoading: Boolean = false,
  color: Color = MaterialTheme.colorScheme.primary,
  textColor: Color = MaterialTheme.colorScheme.onPrimary,
  enabled: Boolean = true,
  onClick: () -> Unit
) {
  Button(
    onClick = onClick,
    enabled = enabled && !isLoading,
    colors = ButtonDefaults.buttonColors(
      containerColor = color,
      contentColor = textColor,
      disabledContainerColor = color.copy(alpha = 0.4f),
      disabledContentColor = textColor.copy(alpha = 0.6f)
    ),
    modifier = modifier
      .height(48.dp),
    shape = RoundedCornerShape(10.dp)
  ) {
    if (isLoading) {
      LoadingDots(color = textColor)
    } else {
      Text(
        text = text,
        color = textColor,
        fontWeight = FontWeight.Bold
      )
    }
  }
}