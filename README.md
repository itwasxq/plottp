# Garden Plot Buttons

A small standalone Fabric client mod for Minecraft 26.1.2.

It extracts only the Garden plot teleport widget idea from Skyblocker, without requiring the full Skyblocker mod.

## Features

- Adds a 5x5 Garden plot panel when the player opens the inventory.
- Left-click a learned plot to send `/plottp <plot name>`.
- The Barn sends `/plottp barn`.
- Bottom buttons send `/desk`, `/warp garden`, and `/setspawn`.
- Drag the top bar to move the UI.
- Right-click the top bar to reset the UI position.
- Position is saved in `.minecraft/config/gardenplotbuttons.json`.

## How to learn plot names

The mod does not know your plot names until it sees Hypixel's Configure Plots menu.

1. Join SkyBlock Garden.
2. Run `/desk`.
3. Open Configure Plots.
4. Close that screen.
5. Reopen inventory.

The mod will save the plot names to `gardenplotbuttons.json`.

## Build

Use the included GitHub Actions workflow or run:

```bash
gradle build
```

Use Java 25.

## Credit

Inspired by Skyblocker's Garden Plots Widget. Skyblocker is LGPL-3.0 licensed.
