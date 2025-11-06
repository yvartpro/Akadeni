package bi.vovota.akadeni.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bi.vovota.akadeni.R
import bi.vovota.akadeni.ui.theme.FontSizes
import bi.vovota.akadeni.ui.theme.Spacings


/*A Search OutlinedTextField*/
@Composable
fun SearchTextField(
  modifier: Modifier = Modifier,
  value: String,
  onValueChange: (String) -> Unit,
  placeholder: String,
) {
  OutlinedTextField(
    modifier = modifier,
    value = value,
    onValueChange = onValueChange,
    placeholder = { Text(placeholder)},
    trailingIcon = {
      IconButton(onClick = {}) {
        Icon(painter = painterResource(R.drawable.search), contentDescription = "Search")
      }
    },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
    shape = RoundedCornerShape(percent = 30),
    singleLine = true
  )
}

@Composable
fun InputField(
  modifier: Modifier = Modifier,
  value: String,
  onValueChange: (String) -> Unit,
  label: String,
  placeholder: String = "",
  leading: Any? = null,
  trailing: Any? = null,
  readOnly: Boolean = false,
  keyboardType: KeyboardType = KeyboardType.Text,
  imeAction: ImeAction = ImeAction.Next,
  onImeAction: (() -> Unit)? = null,
  visualTransformation: VisualTransformation = VisualTransformation.None,
  enabled: Boolean = true,
  isText: Boolean = false,
  isPhone: Boolean = false,
  code: String? = null,
  singleLine: Boolean = true,
) {
  val keyBoardController = LocalSoftwareKeyboardController.current
  OutlinedTextField(
    modifier = modifier.fillMaxWidth(),
    value = value,
    onValueChange = onValueChange,
    label = { Text(label, fontSize = FontSizes.caption()) },
    readOnly = readOnly,
    placeholder = { if (placeholder.isNotEmpty()) Text(placeholder) },
    leadingIcon = {
      when {
        isPhone && code != null -> {
          Row(
            modifier = Modifier.padding(horizontal = Spacings.sm()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            (leading as? Painter)?.let {
              Icon(
                painter = it,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
            }
            Text(
              text = code,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        isText && leading is String -> {
          Text(
            text = leading,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontSize = FontSizes.caption()
          )
        }

        leading is Painter -> {
          Icon(
            painter = leading,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
          )
        }

        else -> {} // no icon
      }
    },
    trailingIcon = {
      trailing?.let {
        Icon(
          painter = trailing as VectorPainter,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(24.dp)
        )
      }
    },
    keyboardOptions = KeyboardOptions(
      keyboardType = keyboardType,
      imeAction = imeAction
    ),
    keyboardActions = KeyboardActions(onDone = {
      onImeAction?.invoke()
      keyBoardController?.hide()
    }),
    visualTransformation = visualTransformation,
    shape = RoundedCornerShape(percent = 25),
    singleLine = singleLine,
    enabled = enabled
  )
}
