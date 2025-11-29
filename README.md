# CW2025 Tetris Maintenance and Extension

## GitHub Repository

https://github.com/xc-student/DMS

## Compilation Instructions

To compile and run the application, ensure you have Maven and Java 17+ installed.

1. Open a terminal in the project root directory.
2. Run the following command to compile and start the game:
   ```bash
   mvn clean javafx:run
   ```
3. To build a jar file:
   ```bash
   mvn clean package
   ```

## Running Tests

To run all unit tests:

```bash
mvn test
```

To run tests with detailed output:

```bash
mvn clean test
```

## Implemented and Working Properly

* **Basic Gameplay**: Standard Tetris mechanics including moving left/right, rotating, and soft drop.
* **Difficulty Levels**: Added 'Easy' and 'Hard' modes with different falling speeds and score multipliers.
  - Easy Mode: 400ms fall interval, 1x score multiplier
  - Hard Mode: 200ms fall interval, 2x score multiplier
* **Versus Mode (2 Players)**: A local multiplayer mode where Player 1 uses Arrow Keys and Player 2 uses WASD keys.
* **Neon Visual Style**: Enhanced the UI with a modern neon aesthetic, including glowing effects and updated colors.
* **Score System**: Points are awarded for clearing lines and staying alive. Score increases automatically over time.
* **Pause/Resume**: Ability to pause the game using 'P' or Spacebar.
* **Game Over/Restart**: Clear game over screen with options to restart.

## Implemented but Not Working Properly

* *None currently known.*

## Features Not Implemented

* *Network Multiplayer*: Originally considered but decided to focus on local multiplayer for stability.

## Refactoring and Design Patterns

### Design Patterns Implemented

1. **Factory Pattern** (`RandomBrickGenerator.java`)

   - Creates different types of Tetris bricks (I, J, L, O, S, T, Z)
   - Encapsulates brick creation logic
   - Allows easy addition of new brick types
2. **Strategy Pattern** (`DifficultyStrategy.java`, `EasyDifficultyStrategy.java`, `HardDifficultyStrategy.java`)

   - Defines different difficulty levels with varying game speeds and score multipliers
   - Easy mode: 400ms fall interval, 1x score multiplier
   - Hard mode: 200ms fall interval, 2x score multiplier
   - Allows easy addition of new difficulty levels without modifying existing code
3. **Observer Pattern** (`Score.java`)

   - Uses JavaFX `IntegerProperty` for automatic UI updates
   - Score changes automatically reflect in the GUI without manual refresh
   - Decouples score logic from UI rendering
4. **Singleton Pattern** (`MatrixOperations.java`)

   - Utility class with private constructor
   - Prevents instantiation of utility methods
   - Provides static methods for matrix operations

### Code Organization Improvements

- **Separated UI Components**: Created dedicated classes for `GameOverPanel`, `PausePanel`, and `NotificationPanel`
- **Extracted Data Classes**: Created `Score`, `ViewData`, `DownData`, `ClearRow`, `MoveEvent`, `NextShapeInfo` for better data encapsulation
- **Package Structure**: Organized brick logic into `com.comp2042.logic.bricks` package
- **Constant Extraction**: Moved magic numbers to named constants (e.g., `BRICK_SIZE`, layout positions)
- **Single Responsibility**: Each class has a clear, focused purpose

### Testing

Unit tests have been added for core game logic using JUnit 5:

- **`ScoreTest.java`** (7 tests): Tests score addition, reset, and property binding
- **`MatrixOperationsTest.java`** (9 tests): Tests matrix copying, row clearing, and score calculation
- **`RandomBrickGeneratorTest.java`** (7 tests): Tests brick generation and type validation
- **`DifficultyStrategyTest.java`** (9 tests): Tests strategy pattern implementation

**Total: 32 unit tests covering core functionality**

To run tests:

```bash
mvn test
```

## New Java Classes

### Main Classes

* `GameOverPanel.java`: Encapsulated the Game Over UI logic.
* `PausePanel.java`: Encapsulated the Pause UI logic.
* `NotificationPanel.java`: Handles floating score text animations.
* `DifficultyStrategy.java`: Interface for difficulty strategy pattern.
* `EasyDifficultyStrategy.java`: Implementation of easy difficulty.
* `HardDifficultyStrategy.java`: Implementation of hard difficulty.

### Data Classes

* `Score.java`: Manages game score with JavaFX property binding.
* `ViewData.java`: Contains view-related information for rendering.
* `DownData.java`: Data class for downward movement events.
* `ClearRow.java`: Information about cleared rows.
* `MoveEvent.java`: Represents player input events.
* `NextShapeInfo.java`: Information about next brick rotation.

### Test Classes

* `ScoreTest.java`: Unit tests for Score class.
* `MatrixOperationsTest.java`: Unit tests for matrix operations.
* `RandomBrickGeneratorTest.java`: Unit tests for brick generation.
* `DifficultyStrategyTest.java`: Unit tests for difficulty strategies.

## Modified Java Classes

* `GuiController.java`:

  * Refactored to support two game boards for Versus mode.
  * Added logic for handling input for two players (Arrow keys for Player 1, WASD for Player 2).
  * Integrated difficulty selection with strategy pattern.
  * Improved code organization with extracted constants.
* `GameController.java`:

  * Extended to manage state for two simultaneous games in Versus mode.
  * Implements InputEventListener for handling player input.
  * Added methods for player 2 controls.
* `SimpleBoard.java`:

  * Implements Board interface for game logic.
  * Manages brick movement, collision detection, and row clearing.
* `MatrixOperations.java`:

  * Utility class for matrix operations.
  * Added comprehensive Javadoc documentation.

## Bug Fixes

* **Performance in VS Mode**: Initially, rendering two boards caused lag. Optimized by reducing the frequency of full board refreshes and using efficient JavaFX updates.
* **Input Conflicts**: Handling simultaneous key presses for two players required careful management of the Event Handler to ensure no inputs were lost.
* **Difficulty Switching**: Fixed issues with timeline updates when switching between difficulty levels.

## Controls

### Single Player Mode

- **Arrow Left/Right**: Move brick horizontally
- **Arrow Up**: Rotate brick
- **Arrow Down**: Soft drop (faster descent)
- **P or Spacebar**: Pause/Resume game
- **N**: Start new game

### Versus Mode (2 Players)

**Player 1 (Right side):**

- **Arrow Keys**: Control movement and rotation

**Player 2 (Left side):**

- **W**: Rotate brick
- **A**: Move left
- **S**: Soft drop
- **D**: Move right

## Project Structure

```
src/
├── main/
│   ├── java/com/comp2042/
│   │   ├── logic/bricks/          # Brick implementations
│   │   │   ├── Brick.java          # Brick interface
│   │   │   ├── BrickGenerator.java # Generator interface
│   │   │   ├── RandomBrickGenerator.java # Factory pattern
│   │   │   ├── IBrick.java, JBrick.java, LBrick.java
│   │   │   ├── OBrick.java, SBrick.java, TBrick.java, ZBrick.java
│   │   ├── Board.java              # Board interface
│   │   ├── SimpleBoard.java        # Board implementation
│   │   ├── GameController.java     # Game logic controller
│   │   ├── GuiController.java      # UI controller
│   │   ├── Main.java               # Application entry point
│   │   ├── Score.java              # Score management
│   │   ├── MatrixOperations.java   # Utility class
│   │   ├── DifficultyStrategy.java # Strategy pattern
│   │   ├── EasyDifficultyStrategy.java
│   │   ├── HardDifficultyStrategy.java
│   │   ├── GameOverPanel.java      # UI component
│   │   ├── PausePanel.java         # UI component
│   │   └── NotificationPanel.java  # UI component
│   └── resources/
│       ├── window.fxml             # Main UI layout
│       ├── window_style.css        # Neon styling
│       └── digital.ttf             # Score font
└── test/
    └── java/com/comp2042/
        ├── ScoreTest.java
        ├── MatrixOperationsTest.java
        ├── DifficultyStrategyTest.java
        └── logic/bricks/
            └── RandomBrickGeneratorTest.java
```

## Dependencies

- **JavaFX 21.0.6**: For GUI rendering
- **JUnit 5.12.1**: For unit testing
- **Maven**: Build and dependency management
