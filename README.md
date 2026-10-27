# Player Cart Boost

Server-side Fabric mod for Minecraft Java 26.3. Only minecarts carrying a player receive the configurable boosted speed cap.

## Build

Requirements: JDK 25. The included `gradlew.bat` uses the installed Gradle 9.8.2 at `C:\Program Files\Gradle\gradle-9.8.2` when available; otherwise it downloads that version. Gradle's project cache is stored in the hidden `.gradle` folder.

Open PowerShell in the project folder (`C:\Users\LötGurke\Documents\Codex\2026-10-27\referenced-chatgpt-conversation-this-is-an`) and run:

```powershell
.\gradlew.bat build
```

The installable mod jar is written to `build/libs/player-cart-boost-1.0.0.jar`. Put it and Fabric API in the server's `mods` folder. Because the mod activates vanilla's improved minecart movement algorithm, install the same mod jar and Fabric API in each player's Fabric client as well so client and server use matching minecart physics. The project targets Fabric Loader 0.19.5.

## Local test server

A ready-to-set-up test server folder is included at `test-server`. Run `test-server\setup-test-server.bat` to copy the built mod and download the matching Fabric server launcher and Fabric API. The EULA is set to `eula=true` as authorized. Then run `test-server\start-test-server.bat`. See `test-server\README-test-server.txt` for the test steps.

## Configuration

Set a player-cart speed in game with the custom gamerule:

```text
/gamerule player_cart_boost:player_cart_max_speed 24.0
```

The value is blocks per second, defaults to `16.0`, applies immediately, and is saved with the world. Use `0` to stop player-occupied carts. Negative values are treated as zero. The mod does not impose an upper speed limit on this gamerule. It affects only carts carrying a player. `config/player-cart-boost.properties` retains the independent `otherCartMaxSpeed` setting for other cart types; `vanilla` leaves their speed unchanged.

## Speed display

While riding a minecart, the client shows total 3D speed in the actionbar by default. In Options → Controls, press the `Player Cart Boost` key `H` (rebindable) to cycle through total speed, signed X/Y/Z speeds, and off. Each player's selection is saved locally by player UUID in `config/player-cart-boost-client.properties`. Speeds are shown in blocks per second; values at or above 10 million use scientific notation.

## Rail and derailment limits

The mod forces vanilla's improved minecart movement algorithm in code, without enabling world experiments or changing feature flags. That algorithm keeps minecarts on the track more reliably at speed and turns them along rails, as described in the [official Minecraft 1.21.2 release notes](https://feedback.minecraft.net/hc/en-us/articles/31261174284557-Minecraft-Java-Edition-1-21-2-Bundles-of-Bravery). This movement algorithm applies to all minecarts, while the increased speed cap still applies only to player-occupied carts. Minecart redstone contraptions may behave differently. Extreme speeds, collisions, and unusual rail geometry can still cause problems; this is improved rail following, not a guarantee against every derailment.
