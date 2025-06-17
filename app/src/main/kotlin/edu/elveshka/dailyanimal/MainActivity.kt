package edu.elveshka.dailyanimal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun CustomTooltip() {
    val backgroundColor = MaterialTheme.colorScheme.primaryContainer
    val textColor = MaterialTheme.colorScheme.onPrimaryContainer

    Box(
        modifier = Modifier
            .width(300.dp)
            .height(100.dp)
            .padding(end = 56.dp, top = 8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = Path().apply {
                moveTo(0f, 20f)
                lineTo(size.width - 20f, 20f)
                lineTo(size.width - 20f, 0f)
                lineTo(size.width, 20f)
                lineTo(size.width - 20f, 40f)
                lineTo(size.width - 20f, size.height)
                lineTo(0f, size.height)
                close()
            }

            drawPath(
                path = path,
                color = backgroundColor,
                style = Fill
            )
        }

        Text(
            text = "Смени собаку на кота",
            color = textColor,
            fontSize = 28.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.Center)
        )
    }
}