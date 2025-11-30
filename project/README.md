# CodeQuest

A 2D educational game where players learn programming by typing commands to control their character and solve puzzles through an interactive Java-based adventure.

## Game Concept

CodeQuest is an educational puzzle game that teaches programming fundamentals through engaging gameplay. Players control their character by writing Python-like commands in a text parser, seamlessly blending authentic programming education with adventure-style gameplay.

**Key Features:**
- Learn by doing: Type actual code commands to move and interact
- Progressive difficulty: Start with simple commands, advance to loops and conditionals
- Visual feedback: See your code execute in real-time

## Current Status

### Completed Features

**Game Engine Core**
- 60 FPS game loop with delta time calculations
- Smooth rendering with double buffering
- Efficient viewport culling (only visible tiles rendered)
- Y-sorting for depth rendering

**Player System**
- 8-directional sprite animation with walk/idle cycles
- Smooth camera-following movement
- Rectangle-based collision detection
- Sprite animation system with frame switching

**Map System**
- 50x50 tile world map with beach border tiles
- Tile-based collision system
- Map loading from text files
- World camera system (player centered)

**Object System**
- Map objects (walls, trees, bushes) with collision
- Directional beach tiles for borders
- Asset loading with AssetHandler
- Polymorphic drawing with Drawable interface

**NPC System**
- NPCs with sprite animation (idle/walk cycles)
- Stationary and moving AI behaviors
- Collision detection with player and objects
- Y-sorting rendering
- Dialogue support (framework in place)

**Collision Detection**
- Predictive collision checking
- Solid area rectangles for entities, objects, and NPCs
- Tile-based collision grid
- Direction-aware collision response
- Debug collision rectangles display

**UI System**
- Pause screen with overlay
- GUI framework for menus and dialogues


**Command Parser** (Primary Focus)
- Text input field for typing commands
- Python-style command execution system
- Command validation and error handling

### Planned Features

**Phase 2: Programming Concepts**
- Variables: 
- Loops: 

**Phase 3: Level Design**
- Progressive tutorial levels
- Puzzle-based challenges
- Multiple solution approaches
- Achievement system

**Phase 4: Content Expansion**
- NPCs and dialogue
- Quest system
- Interactive objects

## Graphics Upgrade Plan

The team is planning to enhance visual quality while maintaining the game's educational focus.

### Current Graphics
- Tile Size: 32x32 pixels
- Scale Factor: 2x
- Display Size: 64x64 pixels per tile
- Style: Pixel art with beach borders
- Trees: 256x256 scaled sprites with collision

### Implementation Process

**Current Configuration** (GamePanel.java)
```java
final int gameTiles = 32;  // Sprite size
final int scale = 2;       // Scale factor
// Result: 32 × 2 = 64 pixels per tile on screen
```

**Step 2: Replace Assets**
- Maintain existing file structure and naming
- Place new sprites in `/res/player/`
- Place new tiles in `/res/tiles/`
- Support for PNG format

**Step 3: Verify Compatibility**
- Maps stored as tile indices (resolution-independent)
- No map file modifications required

### Design Considerations
- Maintain readability at all resolutions
- Ensure consistent art style across all assets
- Keep file sizes reasonable for distribution
- Consider accessibility (color contrast, clarity)

## Map System

### File Format

Maps are stored as text files with space-separated tile indices:

```
0 0 0 0 0
0 0 0 0 0
0 0 0 0 0
0 0 0 0 0
0 0 0 0 0
```
(Objects and NPCs added separately for walls, trees, bushes, and NPCs)

### Tile Types

**Current Tiles:**
- `0` - Grass (walkable terrain)
- Beach borders: Directional wall tiles (up, down, left, right, corners)

### Creating Custom Maps

1. Create new text file in `/res/Maps/`
2. Format: 25 columns × 25 rows
3. Use space-separated integers (0 for grass)
4. Load in TileManager: `loadMap("/CodeQuest/res/Maps/YourMap.txt")`

Example map structure:
```
Border: Beach tiles (directional)
Interior: 0s (grass) with tree objects
Structures: Enclosed areas with walls
```

### Future Enhancements
- Multi-layer rendering (ground, objects, overlay)
- Animated tiles (water ripples, grass sway)
- Procedural generation for practice levels
- Tile metadata (properties, events, triggers)

## Technical Architecture

### Project Structure

```
CodeQuest_finalVErsion/
  │
  ├── Entity/                          # Game entities and systems
  │   ├── entity.java                     # Base class for all game entities
  │   ├── Player.java                     # Player character with command-based movement
  │   ├── NPC.java                        # Non-Player Character with AI behavior
  │   ├── NPCManager.java                 # Manages all NPCs in the game world
  │   ├── HealthSystem.java               # Player health management (Observer pattern)
  │   ├── KeySystem.java                  # Key collection tracking (Observer pattern)
  │   ├── ChestSystem.java                # Chest counter system (Observer pattern)
  │   └── MessageSystem.java              # NPC dialogue and print() message display
  │
  ├── Main/                            # Core game engine and UI
  │   ├── Main.java                       # Application entry point
  │   ├── GamePanel.java                  # Main game loop and rendering
  │   ├── KeyHandler.java                 # Keyboard input handling
  │   ├── CollisionChecker.java           # Collision detection system
  │   │
  │   ├── CommandParser.java              # Python-like command parser (loops, conditions)
  │   ├── CommandAdapter.java             # Command queue with smooth movement
  │   ├── CommandInputPanel.java          # Command input UI panel
  │   │
  │   ├── MenuManager.java                # Menu state and music management
  │   ├── TitleScreen.java                # Animated title screen
  │   ├── MainMenu.java                   # Main menu interface
  │   ├── PauseMenu.java                  # Pause menu overlay
  │   ├── OptionsMenu.java                # Game options menu
  │   │
  │   ├── GameUI.java                     # HUD display (health, keys, chests)
  │   ├── SoundManager.java               # Audio system (music and SFX)
  │   │
  │   ├── Drawable.java                   # Interface for renderable entities
  │   ├── Observer.java                   # Observer pattern interface
  │   └── Subject.java                    # Subject pattern interface
  │
  ├── Tiles/                           # Map and object management
  │   ├── Tile.java                       # Single tile definition
  │   ├── TileManager.java                # Tile rendering and map loading
  │   ├── MapObject.java                  # Interactive game objects
  │   ├── ObjectManager.java              # Manages all map objects
  │   ├── GameObjectFactory.java          # Factory pattern for object creation
  │   └── AssetHandler.java               # Singleton for asset loading and caching
  │
  ├── res/                             # Game resources
  │   │
  │   ├── 🎵 audio/                       # Sound files
  │   │   ├── menu.wav                    # Menu background music
  │   │   ├── town.wav                    # Gameplay background music
  │   │   └── life_reg.wav                # Navigation sound effect
  │   │
  │   ├── Maps/                        # Game world data
  │   │   ├── WorldMap2.txt               # Active tile map (50x50 grid)
  │   │   ├── Objects.txt                 # Object placement data
  │   │   ├── NPCs.txt                    # NPC spawn positions and dialogue
  │   │   ├── WorldMap.txt                # Legacy map (unused)
  │   │   └── Map1.txt                    # Legacy map (unused)
  │   │
  │   ├── player/                      # Player sprite animations
  │   │   ├── idle_down/                  # Idle animation (8 frames)
  │   │   ├── run_up/                     # Run up animation (8 frames)
  │   │   ├── run_down/                   # Run down animation (8 frames)
  │   │   ├── run_left/                   # Run left animation (8 frames)
  │   │   └── run_right/                  # Run right animation (8 frames)
  │   │
  │   └── tiles/                       # Game world graphics
  │       │
  │       ├── Beach/                      # Beach border tiles
  │       │   ├── beach_up.png
  │       │   ├── beach_down.png
  │       │   ├── beach_left.png
  │       │   ├── beach_right.png
  │       │   ├── beach_top_left.png
  │       │   ├── beach_top_right.png
  │       │   ├── beach_bottom_left.png
  │       │   └── beach_bottom_right.png
  │       │
  │       ├── Nature/                     # Terrain and vegetation
  │       │   ├── terrain1.png            # Grass variant 1
  │       │   ├── terrain2.png            # Grass variant 2
  │       │   ├── terrain3.png            # Grass variant 3
  │       │   ├── Path.png                # Path tile
  │       │   ├── stone_tile.png          # Stone ground
  │       │   ├── tree1.png               # Tree variant 1 (256x256)
  │       │   ├── tree2.png               # Tree variant 2 (256x256)
  │       │   ├── bush1.png               # Bush variant 1
  │       │   └── bush2.png               # Bush variant 2
  │       │
  │       ├── Props/                      # Interactive objects
  │       │   ├── chest1.png              # Closed chest variant 1
  │       │   ├── chest1_open.png         # Open chest variant 1
  │       │   ├── chest2.png              # Closed chest variant 2
  │       │   ├── chest2_open.png         # Open chest variant 2
  │       │   ├── chest3.png              # Closed chest variant 3
  │       │   ├── chest3_open.png         # Open chest variant 3
  │       │   ├── chest4.png              # Closed chest variant 4
  │       │   ├── chest4_open.png         # Open chest variant 4
  │       │   ├── Key1.png                # Key animation frame 1
  │       │   ├── key2.png                # Key animation frame 2
  │       │   ├── Key3.png                # Key animation frame 3
  │       │   ├── Key4.png                # Key animation frame 4
  │       │   ├── Wall_Tiles.png          # Standard wall
  │       │   ├── Wall_Tiles_side.png     # Side wall
  │       │   ├── top_corner.png          # Top corner wall
  │       │   ├── bottom_corner.png       # Bottom corner wall
  │       │   └── Deco_skeleto_sitdown.png # Skeleton decoration
  │       │
  │       ├── Icons/                      # UI elements
  │       │   ├── full_heart.png          # Full health heart
  │       │   ├── empty_heart.png         # Empty health heart
  │       │   ├── full_key.png            # Key counter icon
  │       │   ├── chest_counter.png       # Chest counter icon
  │       │   ├── MainMenue.png           # Menu background
  │       │   ├── Game_over.png           # Game over screen
  │       │   └── win.png                 # Victory screen
  │       │
  │       └── NPC/                        # NPC sprites
  │           └── NPC1/                   # NPC variant 1
  │               ├── NPC_idle1.png       # Idle frame 1
  │               ├── NPC_idle2.png       # Idle frame 2
  │               ├── NPC_idle3.png       # Idle frame 3
  │               └── NPC_idle4.png       # Idle frame 4


### Core Systems

**Camera System**
- Player remains at fixed screen position
- World translates relative to player movement
- Viewport culling for performance optimization
- Smooth scrolling with pixel-perfect alignment

**Collision Detection**
- Entity solid areas defined as rectangles
- Predictive collision (checks before movement)
- Tile-grid based collision lookup
- Direction-aware collision response
- Prevents overlap with solid tiles

**Animation System**
- Frame-based sprite animation
- 2 frames per direction (8 sprites total)
- Timer-based frame switching
- Direction changes update sprite immediately

**Rendering Pipeline**
1. Clear screen
2. Render visible tiles (with culling)
3. Render objects with Y-sorting (trees, walls, bushes)
4. Render entities with Y-sorting (player, NPCs)
5. Render UI elements (pause screen, future: parser)
6. Swap buffers (double buffering)

## Development Roadmap

### Phase 1: Core Mechanics (COMPLETED)
- [x] Game loop implementation
- [x] Player movement system
- [x] Tile-based rendering
- [x] Collision detection
- [x] Camera/viewport system
- [x] World map loading
- [x] Object system with Y-sorting
- [x] Asset management
- [x] NPC system with collision and AI
- [x] Pause functionality

### Phase 2: Command System (IN PROGRESS)
- [x] Text input field UI
- [x] Command parser implementation
- [x] String-based command matching
- [x] Basic command set (move, interact)
- [x] Error handling

### Phase 3: Graphics Enhancement (COMPLETED)
- [x] Source or create 32x32+ sprites
- [x] Design detailed tile textures
- [x] Implement sprite scaling system
- [x] Polish animations

### Phase 4: Educational Content (PLANNED)
- [ ] Tutorial level sequence
- [ ] Progressive command introduction
- [ ] Puzzle design and implementation

## Running the Game

### Development Setup

1. **Clone Repository**
   ```bash
   git clone https://github.com/elbacha2005/software-engineering/tree/main/project/CodeQuest_final
   cd CodeQuest_final
   ```

2. **Open in IDE**


3. **Configure Resources**
   - Mark `/res/` folder as resources root
   - Ensure all image files are accessible
   - Verify map files are in correct format

4. **Run Application**
   - Execute `Main.java`
   - Game window should appear
   - Use arrow keys for temporary controls

### Current Controls (Temporary)
- Arrow Keys: Move player
- Escape: Pause/unpause game

Note: Keyboard controls are temporary and will be replaced by the command parser system.

## Project Team

**Developers:**
- Mohamed Amine El Bacha - UM6P Cyber Security
-   command parser integration
-   Sound effect intergation
-   Gui integration
- Younes Menfalouti - UM6P Cyber Security
-   Game engine developpement
-   Game disgn
  

