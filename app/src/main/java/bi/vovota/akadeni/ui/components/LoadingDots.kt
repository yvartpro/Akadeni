package bi.vovota.akadeni.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.animation.core.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color



@Composable
fun LoadingDots(color: Color? = MaterialTheme.colorScheme.primary) {
    val infiniteTransition = rememberInfiniteTransition()
    val dot1 = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val dot2 = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, delayMillis = 150, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val dot3 = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, delayMillis = 300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.size(dot1.value.dp))
        CircleDot(dot1.value, color)
        CircleDot(dot2.value, color)
        CircleDot(dot3.value,color)
    }
}

@Composable
fun CircleDot(size: Float, color: Color?) {
    Spacer(
        modifier = Modifier
            .size(size.dp)
            .background(color = color ?: MaterialTheme.colorScheme.primary, shape = CircleShape)
    )
}