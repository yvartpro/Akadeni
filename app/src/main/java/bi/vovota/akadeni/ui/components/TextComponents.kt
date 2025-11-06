package bi.vovota.akadeni.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import bi.vovota.akadeni.ui.theme.FontSizes

/*HeaderText Components*/
@Composable
fun TextHeadComponent(value: String){
  Text(
    text = value,
    fontWeight = FontWeight.Bold,
    style = MaterialTheme.typography.headlineSmall
  )
}

/*NormalText Components*/
@Composable
fun TextNormComponent(value: String){
  Text(
    text = value,
    fontWeight = FontWeight.Normal,
    style = MaterialTheme.typography.bodyLarge
  )
}

@Composable
fun TitleText(value: String, color: Color? = null, fontSize: TextUnit = FontSizes.body(), fontWeight: FontWeight = FontWeight.Normal) {
  Text(
    text = value,
    color = color ?: MaterialTheme.colorScheme.onSurface,
    style = MaterialTheme.typography.bodyLarge,
    fontWeight = fontWeight,
    fontSize = fontSize
  )
}

@Composable
fun DetailText(value: String, color: Color? = null) {
  Text(
    text = value,
    color = color ?: MaterialTheme.colorScheme.onSurface,
    style = MaterialTheme.typography.bodyMedium,
    fontWeight = FontWeight.Light
  )
}

/*MediumText Components*/
@Composable
fun TextMediumComponent(value: String){
  Text(
    text = value,
    fontWeight = FontWeight.Normal,
    style = MaterialTheme.typography.headlineSmall
  )
}

/*NormalText Components Link*/
@Composable
fun TextNormComponentLink(value: String){
  Text(
    text = value,
    fontWeight = FontWeight.Normal,
    style = MaterialTheme.typography.bodyLarge,
    color = MaterialTheme.colorScheme.primary
  )
}

@Composable
fun SmallText(
  text: String,
  modifier: Modifier = Modifier,
  color: Color = Color.Unspecified,
  fontWeight: FontWeight? = null,
  textDecoration: TextDecoration? = null
) {
  Text(
    text = text,
    fontSize = 12.sp,
    color = color,
    fontWeight = fontWeight,
    textDecoration = textDecoration,
    modifier = modifier
  )
}