Test server for Player Cart Boost

1. Run setup-test-server.bat. It copies the current mod jar and downloads the Fabric server launcher plus Fabric API for Minecraft 26.3 / Loader 0.19.5.
2. eula.txt is set to eula=true, as authorized by you.
3. Run start-test-server.bat. The first server launch downloads Minecraft files and creates a new world in this folder. This is a separate test world; it does not use your existing saves.
4. Install the same Player Cart Boost jar and Fabric API in the Minecraft Java 26.3 Fabric client, then connect to localhost. The mod enables improved minecart physics on both sides so their movement stays in sync.
5. Set the player-cart speed cap from chat or the server console, for example: `/gamerule player_cart_boost:player_cart_max_speed 24.0`. The value is blocks per second, applies immediately, and defaults to 16.0. Build a long track with powered rails and ride it in a minecart. Only player-occupied carts get the higher cap. Improved rail movement is enabled for all carts, so some redstone contraptions may behave differently.
6. While riding, press H to cycle the client actionbar through total 3D speed, per-axis X/Y/Z speeds, and off. The key can be rebound in Options > Controls. Each player's selection is saved locally.

The server creates config/player-cart-boost.properties on first start. Stop the server before changing settings, then start it again. World files, logs, and downloaded server files are kept in this test-server folder.

To rebuild the mod later, run ..\gradlew.bat build from the project folder, then rerun setup-test-server.bat so the updated mod jar is copied into mods.
