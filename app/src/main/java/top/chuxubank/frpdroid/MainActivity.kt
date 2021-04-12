package top.chuxubank.frpdroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import top.chuxubank.frpdroid.ConnectionState.*
import top.chuxubank.frpdroid.ui.theme.FrpdroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FrpdroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    HomeScreen()
                }
            }
        }
    }

    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }
}

@Composable
fun HomeScreen(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    mainViewModel: MainViewModel = viewModel()
) {
    val connectionState by mainViewModel.connectionState.observeAsState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            val title = stringResource(id = R.string.app_name)
            TopAppBar(
                title = { Text(title) },
            )
        },
        content = { ServerAddress() },
        floatingActionButton = {
            FAB(
                scaffoldState,
                connectionState ?: Disconnected,
                onToggleConnection = {
                    mainViewModel.onToggleConnection()
                }
            )
        }
    )
}

@Composable
fun ServerAddress() {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        singleLine = true,
        onValueChange = { text = it },
        label = { Text("Server Address") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
    )
}

@Composable
fun FAB(
    scaffoldState: ScaffoldState,
    connectionState: ConnectionState,
    onToggleConnection: () -> Unit
) {
    val scope = rememberCoroutineScope()

    FloatingActionButton(onToggleConnection) {
        Crossfade(targetState = connectionState) {
            when (it) {
                Connected -> Icon(Icons.Filled.Stop, contentDescription = "Stop")
                Connecting -> Icon(Icons.Filled.Stop, contentDescription = "Start")
                Disconnected -> Icon(Icons.Filled.PlayArrow, contentDescription = "Start")
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}