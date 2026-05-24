# The DOPO Hardest Game

## Descripción general

**The DOPO Hardest Game** es un proyecto académico desarrollado para la materia de **Desarrollo Orientado a Objetos**. El objetivo principal del proyecto es aplicar los conceptos trabajados durante el curso, como programación orientada a objetos, separación por capas, patrones de diseño, manejo de excepciones, pruebas unitarias, persistencia y construcción de interfaces gráficas.

El juego está inspirado en *The World's Hardest Game* y cumple los requerimientos principales definidos en el documento formal del proyecto, incluyendo carga de niveles desde archivos `.txt`, movimiento de jugadores, recolección de monedas, enemigos con comportamientos, elementos especiales, modo jugador y modo PvP.

> Documento recomendado para revisar primero:  
> `/Requerimientos_DOPOsHardestGame.pdf`

---

## Estado actual del proyecto

El proyecto cuenta actualmente con:

- Modo **Player**.
- Modo **Player vs Player (PvP)**.
- Selección de skin para jugadores: `Blinky`, `Inky` y `Clyde`.
- Selección de color de borde para diferenciar jugadores.
- Sistema de monedas normales y monedas skin.
- Enemigos con diferentes estrategias de movimiento.
- Elementos especiales: fuente de vida y bomba.
- Zonas seguras iniciales, finales e intermedias tipo checkpoint.
- Carga dinámica de niveles desde archivos `.txt`.
- Guardado y carga de partidas.
- Temporizador configurable por nivel.
- Mensaje de victoria con opción de continuar o terminar.
- Pruebas unitarias sobre la fachada `DOPOsHardestGame`.
- Prueba de aceptación visual usando `Robot`.

---

## Estructura general del proyecto

```text
src/
 ├── domain/
 ├── persistence/
 ├── presentation/
 └── test/

res/
 ├── blinky.png
 ├── inky.png
 ├── clyde.png
 ├── clyde_damaged.png
 ├── coin.png
 ├── red_coin.png
 ├── blue_coin.png
 ├── green_coin.png
 ├── linear_enemy.png
 ├── patrol_enemy.png
 ├── search_enemy.png
 ├── ambush_enemy.png
 ├── life_source.png
 ├── bomb.png
 ├── wall.png
 ├── lightFloor.png
 ├── darkFloor.png
 ├── safeZone.png
 └── checkpointZone.png

docs/
 ├── Requerimientos_DOPOsHardestGame.pdf
 ├── Guia_Formato_Niveles.txt
 ├── Retrospectivas/
 └── Diagramas/
```

La carpeta `res/` debe estar en la raíz del proyecto para que las imágenes puedan cargarse correctamente desde la interfaz.

---

## Capas del sistema

### `domain`

Contiene la lógica principal del juego.

Incluye clases como:

- `DOPOsHardestGame`
- `Level`
- `Player`
- `Enemy`
- `Coin`
- `SafeZone`
- `Wall`
- `SpecialElement`
- `LifeSource`
- `Bomb`

También contiene interfaces y patrones de diseño:

- `PlayerState`
- `EnemyStrategy`
- `MovableElement`
- `Collidable`

Y clases auxiliares como:

- `RenderData`
- `DamageResult`
- `ElementUpdateResult`
- `DOPOsHardestGameException`

---

### `persistence`

Contiene la lógica para leer y guardar información.

Incluye:

- `LevelLoader`: carga niveles desde archivos `.txt`.
- `GameSaver`: guarda el estado actual de una partida.
- `GameLoader`: carga una partida guardada.

---

### `presentation`

Contiene la interfaz gráfica del juego construida con Swing.

Incluye:

- `GameFrame`
- `GamePanel`
- `MainMenuPanel`
- `CharacterSelectionPanel`
- `Tile`
- `TileManager`

La presentación se comunica con el dominio principalmente a través de la fachada `DOPOsHardestGame`.

---

### `test`

Contiene las pruebas del proyecto.

Incluye:

- `DOPOsHardestGameTest`: pruebas unitarias de la fachada.
- `GameAcceptanceTestEI`: prueba de aceptación visual.

---

## Patrones de diseño aplicados

### Facade

La clase `DOPOsHardestGame` funciona como fachada principal.  
Permite que la interfaz gráfica se comunique con el dominio sin acceder directamente a todas las clases internas del juego.

---

### State

Se usa para representar el comportamiento de las skins del jugador.

```text
PlayerState
 ├── BlinkyState
 ├── InkyState
 └── ClydeState
```

Cada estado modifica aspectos como velocidad, tamaño y reacción al daño.

---

### Strategy

Se usa para definir comportamientos diferentes en los enemigos.

```text
EnemyStrategy
 ├── LinearEnemyStrategy
 ├── PatrolEnemyStrategy
 ├── SearchEnemyStrategy
 └── AmbushEnemyStrategy
```

Cada estrategia define una forma distinta de movimiento.

---

### Factory

Se usa para crear objetos a partir de archivos de configuración.

```text
EnemyStrategyFactory
CoinFactory
SpecialElementFactory
```

Esto permite cargar enemigos, monedas y elementos especiales sin acoplar directamente el `LevelLoader` a todas las clases concretas.

---

## Configuración de niveles

Los niveles se crean mediante archivos `.txt`.

Cada archivo define:

- Tamaño del mapa.
- Matriz del escenario.
- Tiempo límite.
- Monedas.
- Enemigos.
- Elementos especiales.

Ejemplo básico:

```text
10 6
0 0 0 0 0 0 0 0 0 0
3 1 1 1 1 1 1 1 1 4
3 1 0 0 1 1 0 0 1 4
3 1 1 1 1 1 1 1 1 4
0 0 0 0 5 5 0 0 0 0
0 0 0 0 0 0 0 0 0 0
TIME 75
COIN 4 1 Yellow
COIN 5 1 Inky
COIN 6 3 Clyde
ENEMY LINEAR 4 2 2 1 0
SPECIAL LifeSource 2 3
SPECIAL Bomb 8 2
```

Valores de la matriz:

```text
0 = Pared
1 = Piso claro
2 = Piso oscuro
3 = Zona segura inicial
4 = Zona segura final
5 = Zona segura intermedia / checkpoint
```

Tipos de monedas:

```text
Yellow = Moneda normal
Blinky = Moneda skin Blinky
Inky = Moneda skin Inky
Clyde = Moneda skin Clyde
```

Tipos de enemigos:

```text
LINEAR = Movimiento lineal
PATROL = Movimiento por ruta
SEARCH = Persecución al jugador más cercano
AMBUSH = Emboscada en rango
```

Elementos especiales:

```text
LifeSource = Fuente de vida
Bomb = Bomba
```

Para más detalle revisar:

```text
docs/Guia_Formato_Niveles.txt
```

---

## Requisitos para ejecutar el proyecto

Se recomienda tener instalado:

- Java JDK 8 o superior.
- Eclipse, IntelliJ IDEA, BlueJ o cualquier IDE compatible con Java.
- JUnit 4 para pruebas unitarias.
- JUnit 5 si se desea ejecutar la prueba de aceptación visual.

La carpeta `res/` debe permanecer en la raíz del proyecto.

---

## Ejecución del juego

1. Clonar o descargar el repositorio.
2. Abrir el proyecto en el IDE.
3. Verificar que la carpeta `res/` esté en la raíz.
4. Verificar que los archivos de nivel estén disponibles.
5. Ejecutar la clase principal del proyecto, normalmente:

```text
Main.java
```

Desde el menú inicial se puede:

- Seleccionar el modo de juego.
- Seleccionar la configuración del nivel.
- Elegir skins de jugadores.
- Elegir colores de borde.
- Iniciar la partida.

---

## Controles

### Jugador 1

```text
Flecha arriba    = mover arriba
Flecha abajo     = mover abajo
Flecha izquierda = mover izquierda
Flecha derecha   = mover derecha
```

### Jugador 2 en PvP

```text
W = mover arriba
S = mover abajo
A = mover izquierda
D = mover derecha
```

---

## Guardar y cargar partida

Desde el menú dentro de la partida se puede:

- Guardar partida.
- Cargar partida.
- Terminar el juego.

La persistencia guarda información importante como:

- Posición de jugadores.
- Monedas recolectadas.
- Tiempo restante.
- Muertes.
- Vidas extra.
- Estado de elementos especiales.
- Puntaje individual en PvP.

---

## Pruebas

### Pruebas unitarias

Las pruebas unitarias están en:

```text
src/test/DOPOsHardestGameTest.java
```

Estas pruebas validan los métodos públicos de la fachada `DOPOsHardestGame`, usando casos tipo `should` y `should not`.

Cubren comportamientos como:

- Carga de niveles válidos e inválidos.
- Cambio de modo de juego.
- Selección de nivel.
- Skins y bordes.
- Movimiento de jugadores.
- Actualización de enemigos y elementos.
- Victoria.
- Reinicio.
- Temporizador.
- Guardado y carga.
- Reglas de monedas en PvP.

---

### Prueba de aceptación

La prueba de aceptación visual está en:

```text
src/test/GameAcceptanceTestEI.java
```

Esta prueba usa `java.awt.Robot` para simular interacción real con la interfaz.

Nota: esta prueba depende del entorno gráfico del sistema operativo, por lo que puede requerir que la ventana tenga foco durante la ejecución.

---

## Documentos recomendados

Dentro del proyecto se recomienda revisar:

```text
docs/Requerimientos_DOPOsHardestGame.pdf
docs/Guia_Formato_Niveles.txt
docs/Diagramas/
docs/Retrospectivas/
```

Estos documentos explican:

- Requisitos originales.
- Formato de niveles.
- Diagrama de clases.
- Diagramas de secuencia.
- Retrospectivas de desarrollo.
- Decisiones de diseño.

---

## Notas de diseño

El proyecto busca mantener una arquitectura extensible. Por eso, muchas responsabilidades se separaron en clases específicas.

Ejemplos:

- Las skins no se manejan con condicionales grandes, sino con `PlayerState`.
- Los enemigos no concentran todos los movimientos, sino que usan `EnemyStrategy`.
- Las monedas y elementos especiales se crean mediante fábricas.
- La presentación no accede directamente a toda la lógica interna, sino a través de `DOPOsHardestGame`.
- Los datos para dibujar se entregan mediante `RenderData`, evitando mezclar lógica visual con reglas del dominio.

---

## Autoría

Proyecto académico desarrollado por:

```text
Yeray Guacheta
DOPO 2026-1
Desarrollo Orientado a Objetos
```

---

## Estado final

El proyecto se encuentra en una versión funcional que cumple los requerimientos principales del documento inicial.  
Está preparado para ser revisado, probado y documentado mediante diagramas de clases, diagramas de secuencia, pruebas unitarias y pruebas de aceptación.
