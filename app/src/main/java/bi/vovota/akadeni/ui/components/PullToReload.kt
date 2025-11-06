package bi.vovota.akadeni.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bi.vovota.akadeni.ui.theme.Spacings
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch

@Composable
fun RefreshableContent(
  isRefreshing: Boolean,
  onRefresh: suspend () -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  val refreshState = rememberSwipeRefreshState(isRefreshing)
  val coroutineScope = rememberCoroutineScope() // ✅ For safe coroutine launch
  var isInternalRefreshing by remember { mutableStateOf(false) }

  SwipeRefresh(
    state = refreshState,
    onRefresh = {
      if (!isInternalRefreshing) {
        isInternalRefreshing = true
        coroutineScope.launch {
          onRefresh()
          isInternalRefreshing = false
        }
      }
    },
    indicator = { state, trigger ->
      SwipeRefreshIndicator(
        state = state,
        refreshTriggerDistance = trigger,
        scale = true,
        backgroundColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
      )
    },
    modifier = modifier
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      content()
      if (isRefreshing) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
          contentAlignment = Alignment.TopCenter
        ) {
          CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 3.dp
          )
        }
      }
    }
  }
}
@Composable
fun ErrorCard(message: String, onRetry: () -> Unit) {
  InfoCard(
    modifier = Modifier
      .padding(Spacings.md())
      .fillMaxWidth()
  ) {
    Column(Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally) {
      Text(message, color = MaterialTheme.colorScheme.error)
      Spacer(Modifier.height(Spacings.xs()))
      RetryButton(onClick = onRetry)
    }
  }
}

@Composable
fun RetryButton(onClick: () -> Unit) {
  Button(onClick = onClick) {
    Text("Retry")
  }
}