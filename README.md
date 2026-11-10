# Documentación y Análisis de la Arquitectura MVVM

Este documento detalla el análisis de la arquitectura **Model-View-ViewModel (MVVM)**, la explicación del desarrollo del juego **Tres en Raya (Tic-Tac-Toe)** y el resumen de **Conceptos y Métodos Aprendidos**.

---

# Sección 1: Análisis y documentación de la arquitectura MVVM

## 1.1. Documentación de los componentes (basado en MVVMSimple)

La arquitectura MVVM organiza la aplicación en tres capas principales con las siguientes responsabilidades:

### 1. Model (`BoardModel` / `CellModel` / `ParellesModel`)
* **Qué datos guarda:** Almacena los datos del estado del juego, como la lista de casillas (`CellModel`), la posición, el símbolo de cada casilla (`"X"`, `"O"` o `null`), el turno actual (`currentTurn`), el ganador (`winner`) y si el juego ha finalizado (`isGameOver`).
* **Función principal:** Representar la fuente de la verdad (*Single Source of Truth*) del dominio de la aplicación. Contiene la lógica pura y las reglas internas del tablero sin depender de la UI ni del framework de Android.

### 2. ViewModel (`TicTacToeViewModel` / `ParellesViewModel`)
* **Qué trabajo hace exactamente:** Actúa como intermediario entre el Modelo y la Vista. Mantiene y expone el estado de la UI utilizando flujos reactivos (`StateFlow` / `MutableStateFlow` o `mutableStateOf`), gestiona tareas asíncronas (como retardos con corrutinas) y procesa los eventos del usuario.
* **Cómo se comunica con el Model:** Invoca los métodos puros de la clase del modelo (`makeMove()`, `checkWinner()`, etc.) para actualizar el estado del juego.
* **Cómo expone los datos a la interfaz:** Publica el estado como un flujo inmutable (`asStateFlow()`), permitiendo que la interfaz en Jetpack Compose se recomponga automáticamente ante cualquier cambio.

### 3. View (UI / Compose) (`TicTacToeScreen` / `GameScreen`)
* **Cuál es su papel:** Renderizar el estado visual de la aplicación en la pantalla (tablero de casillas, mensaje de turno, diálogo de victoria/empate y botón de reinicio). Es una vista pasiva (*dumb UI*).
* **Cómo recibe los cambios de estado:** Se suscribe al estado expuesto por el ViewModel mediante `collectAsState()`. Cada vez que el estado cambia, Compose ejecuta una recomposición automática de los componentes afectados.
* **Cómo notifica las acciones del usuario al ViewModel:** Captura los eventos de interacción (como pulsar en una casilla con `clickable`) y llama a las funciones del ViewModel (`onCellClick(position)`).

---

## 1.2. Análisis comparativo (MVVMSimple vs MVVM-separatModel)

### Estructura y Separación de Responsabilidades
* **Diferencia de organización:**
  * En **MVVMSimple**, todo el estado y la lógica del juego suelen estar dentro del propio ViewModel o en un modelo integrado simple, mezclando eventos de UI con la lógica de negocio.
  * En **MVVM-separatModel**, la lógica de negocio y el estado se aíslan por completo en clases independientes (`BoardModel`, `CellModel`). El ViewModel solo se encarga de emitir el estado hacia la UI.
* **Dónde se ejecuta la lógica de negocio:**
  * En **MVVMSimple**: Dentro de los métodos del ViewModel.
  * En **MVVM-separatModel**: Encriptada y encapsulada en la clase `BoardModel` (`makeMove()`, `checkWinner()`), que retorna nuevas instancias inmutables.

### Inmutabilidad y Gestión del Estado
* **Modificación de datos en MVVM-separatModel:**
  * En lugar de modificar las propiedades de un objeto existente, se utilizan **`data class`** y el método **`.copy()`** para retornar nuevas instancias del tablero con los valores actualizados.
* **Papel del método `.copy()` e importancia reactiva:**
  * `.copy()` permite duplicar un objeto cambiando solo atributos concretos (ej. `cell.copy(symbol = currentTurn)`).
  * Esto asegura la **inmutabilidad**: al cambiar la referencia del objeto, los mecanismos reactivos (`StateFlow` / Compose) detectan instantáneamente el cambio de referencia y desencadenan la recomposición de la UI de forma segura sin mutaciones concurrentes.

### Ventajas de la separación
1. **Testabilidad (Unit Testing):** Permite realizar pruebas unitarias rápidas en JVM sobre `BoardModel` sin necesidad de emulador ni dependencias de Android.
2. **Reutilización de código:** La lógica de negocio es totalmente independiente y reutilizable en otras interfaces (XML Views, Wear OS, KMP).
3. **Mantenibilidad:** Cumple el principio de responsabilidad única (*Single Responsibility Principle*).

---

## 1.3. Análisis comparativo de Estado: StateFlow vs mutableStateOf

| Característica | `StateFlow` / `MutableStateFlow` | `mutableStateOf` / `State<T>` |
| :--- | :--- | :--- |
| **Framework / Librería** | Kotlin Coroutines (`kotlinx.coroutines.flow`) | Jetpack Compose Runtime (`androidx.compose.runtime`) |
| **Integración con Compose** | Requiere `.collectAsState()` o `.collectAsStateWithLifecycle()` | Integración nativa directa con Compose |
| **Aislamiento de Arquitectura** | Totalmente independiente de la UI y del framework Android | Ligado a la capa de UI de Compose |
| **Operadores** | Soporta operadores de flujo (`map`, `combine`, `filter`, `debounce`) | Valores de lectura/escritura directos |

* **Elección para el Tres en Raya:**
  * Se ha elegido **`StateFlow`** (`MutableStateFlow` privado y `asStateFlow()` público) porque proporciona un desacoplamiento perfecto entre la lógica del ViewModel y la UI, cumpliendo con los estándares de producción recomendados por Google para Android.

---

# Sección 2: Explicación de la Arquitectura del Tres en Raya (Tic-Tac-Toe)

La implementación del juego **Tres en Raya** sigue estrictamente el patrón **MVVM con modelo separado e inmutable**:

```
 ┌─────────────────────────────────────────────────────────────┐
 │                       GameScreen (UI)                       │
 │  - Recibe estado con collectAsState()                       │
 │  - Muestra BoardGrid (3x3), CellItemView, indicador y botón │
 └──────────────────────────────┬──────────────────────────────┘
                                │ Acciones (onCellClick, restartGame)
                                ▼
 ┌─────────────────────────────────────────────────────────────┐
 │                     TicTacToeViewModel                      │
 │  - _boardState: MutableStateFlow<BoardModel>                │
 │  - boardState: StateFlow<BoardModel> (asStateFlow)          │
 └──────────────────────────────┬──────────────────────────────┘
                                │ Llama a makeMove(position)
                                ▼
 ┌─────────────────────────────────────────────────────────────┐
 │                      BoardModel & CellModel                 │
 │  - Estado inmutable: cells, currentTurn, winner, isGameOver │
 │  - Muestra el tablero con createInitialBoard()              │
 │  - Retorna nuevas instancias inmutables con .copy()         │
 └─────────────────────────────────────────────────────────────┘
```

### Funcionamiento de la Aplicación
1. Al iniciar la app, `HomeScreen.kt` muestra un menú con los dos juegos disponibles.
2. Al pulsar **TRES EN RATLLA**, se carga `TicTacToeScreen.kt`, el cual lee `boardState` mediante `collectAsState()`.
3. Al pulsar una casilla libre, se ejecuta `viewModel.onCellClick(position)`, que llama a `boardState.value.makeMove(position)`.
4. `BoardModel` calcula las 8 combinaciones ganadoras (3 filas, 3 columnas y 2 diagonales) o comprueba el empate, y retorna una nueva copia inmutable del tablero con `.copy()`.
5. La UI en Compose detecta la actualización del `StateFlow` y recompone las casillas afectadas mostrando `"X"` (en azul) u `"O"` (en rosa).

---

# Sección 3: Conceptos y Métodos Aprendidos

Durante el desarrollo se han aplicado los siguientes conceptos clave de Kotlin y Jetpack Compose:

1. **`StateFlow` y `MutableStateFlow`**:
   - `MutableStateFlow` es un contenedor de estado reactivo y mutable de Kotlin Coroutines. Se utiliza dentro del ViewModel de forma privada para modificar el estado.
   - `asStateFlow()` expone este flujo en versión de solo lectura (`StateFlow`) hacia la UI para proteger la encapsulación.

2. **`collectAsState()`**:
   - Función de extensión de Compose que transforma un `StateFlow` en un `State<T>` de Compose. Permite que cualquier composable observe los cambios del flujo y se recomponga automáticamente al recibir un nuevo valor.

3. **`.copy()` (Método de `data class` de Kotlin)**:
   - Permite crear una nueva instancia de un objeto `data class` modificando solo los parámetros deseados manteniendo el resto intactos. Es fundamental para garantizar la **inmutabilidad** del modelo.

4. **`viewModelScope` y `delay()`**:
   - `viewModelScope` es un alcance de corrutina vinculado al ciclo de vida del ViewModel.
   - `delay(1000L)` pausa la ejecución de la corrutina sin bloquear el hilo principal de la UI, utilizado en el juego de parejas para mostrar brevemente las cartas antes de voltearlas si no coinciden.

5. **`by` (Delegado de propiedades en Kotlin)**:
   - Permite simplificar la lectura/escritura de propiedades de estado en Compose (`val boardState by viewModel.boardState.collectAsState()`) sin necesidad de escribir `.value`.

6. **`LazyVerticalGrid` vs Layouts con `Row` y `Column`**:
   - Aprendimos que `LazyVerticalGrid` puede causar problemas de medición de altura infinita dentro de un `Column` sin restricciones fijas. Para tableros pequeños de dimensión fija (como 3x3), organizar los componentes con `Column` y `Row` ofrece un rendimiento perfecto y evita cuelgues de layout.
