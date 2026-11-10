package org.proven.jocparelles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Pantalla composable para el Juego de les Parelles.
 *
 * @param onBackToMenu Acción para regresar al menú principal.
 * @param viewModel ViewModel del juego obtenido de Compose.
 */
@Composable
fun ParellesScreen(
    onBackToMenu: () -> Unit,
    viewModel: ParellesViewModel = viewModel()
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Imagen de fondo (fondo2.jpg) adaptada al tamaño completo de la pantalla
        Image(
            painter = painterResource(id = R.drawable.fondo2),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Barra superior: Botón de regresar al menú y título del juego
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onBackToMenu,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0x88000000),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("← Menú")
                }

                Text(
                    text = "Joc de Parelles",
                    color = Color(0xFFFF9800),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Cuadrícula de 6 cartas organizada en 2 filas por 3 columnas
            val cards = viewModel.cards
            if (cards.size >= 6) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Fila 1 (Cartas 0, 1, 2)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CardItem(
                            card = cards[0],
                            onClick = { viewModel.onCardClick(cards[0].id) },
                            isProcessing = viewModel.isProcessing,
                            modifier = Modifier.weight(1f)
                        )
                        CardItem(
                            card = cards[1],
                            onClick = { viewModel.onCardClick(cards[1].id) },
                            isProcessing = viewModel.isProcessing,
                            modifier = Modifier.weight(1f)
                        )
                        CardItem(
                            card = cards[2],
                            onClick = { viewModel.onCardClick(cards[2].id) },
                            isProcessing = viewModel.isProcessing,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Fila 2 (Cartas 3, 4, 5)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CardItem(
                            card = cards[3],
                            onClick = { viewModel.onCardClick(cards[3].id) },
                            isProcessing = viewModel.isProcessing,
                            modifier = Modifier.weight(1f)
                        )
                        CardItem(
                            card = cards[4],
                            onClick = { viewModel.onCardClick(cards[4].id) },
                            isProcessing = viewModel.isProcessing,
                            modifier = Modifier.weight(1f)
                        )
                        CardItem(
                            card = cards[5],
                            onClick = { viewModel.onCardClick(cards[5].id) },
                            isProcessing = viewModel.isProcessing,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Botón inferior para reiniciar la partida
            Button(
                onClick = { viewModel.onRestartClick() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE0E0E0),
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .width(160.dp)
                    .height(44.dp)
            ) {
                Text(
                    text = "Reiniciar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Diálogo modal de victoria al completar todas las parejas
        if (viewModel.isGameOver) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text(text = "Felicitats!") },
                text = { Text(text = "Has trobat totes les parelles del joc!") },
                confirmButton = {
                    TextButton(onClick = { viewModel.onRestartClick() }) {
                        Text(text = "Tornar a jugar")
                    }
                }
            )
        }
    }
}

/**
 * Componente que renderiza una carta individual con reverso o imagen frontal según su estado.
 */
@Composable
fun CardItem(
    card: CardModel,
    onClick: () -> Unit,
    isProcessing: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(0.7f)
            .clickable(enabled = !isProcessing && !card.isFaceUp && !card.isMatched) { onClick() },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(2.5.dp, Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Image(
            painter = painterResource(
                id = if (card.isFaceUp || card.isMatched) card.imageResId else R.drawable.back
            ),
            contentDescription = "Card ${card.id}",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}
