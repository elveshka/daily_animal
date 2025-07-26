package edu.elveshka.dailyanimal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import edu.elveshka.dailyanimal.presentation.ui.DailyContentScreen
import edu.elveshka.dailyanimal.presentation.viewmodel.DailyContentViewModel
import edu.elveshka.dailyanimal.ui.theme.DailyAnimalTheme

class MainActivity : ComponentActivity() {
    private val viewModel: DailyContentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DailyAnimalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DailyContentScreen(viewModel)
                }
            }
        }
    }
}