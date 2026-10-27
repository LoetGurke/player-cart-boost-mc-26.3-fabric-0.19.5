package dev.example.playercartboost;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class PlayerCartBoost implements ModInitializer {
    public static final String MOD_ID = "player_cart_boost";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final GameRule<Double> PLAYER_CART_MAX_SPEED = GameRuleBuilder
            .forDouble(16.0)
            .category(GameRuleCategory.MISC)
            .buildAndRegister(Identifier.fromNamespaceAndPath(MOD_ID, "player_cart_max_speed"));
    private static volatile double otherCartMaxSpeed = -1.0;

    @Override public void onInitialize() {
        loadConfig();
        LOGGER.info("Player Cart Boost loaded (default player cap: 16 blocks/s; other carts: {}).",
                otherCartMaxSpeed < 0 ? "vanilla" : otherCartMaxSpeed + " blocks/s");
    }

    public static double maxSpeedFor(boolean hasPlayerPassenger, double vanillaSpeed, double playerSpeedBlocksPerSecond) {
        // AbstractMinecart's hook is expressed per game tick; config values are blocks per second.
        double boundedPlayerSpeed = Double.isFinite(playerSpeedBlocksPerSecond)
                ? Math.max(0.0, playerSpeedBlocksPerSecond) : 16.0;
        return hasPlayerPassenger ? boundedPlayerSpeed / 20.0
                : (otherCartMaxSpeed < 0 ? vanillaSpeed : otherCartMaxSpeed / 20.0);
    }

    private static void loadConfig() {
        Path file = FabricLoader.getInstance().getConfigDir().resolve("player-cart-boost.properties");
        Properties values = new Properties();
        if (Files.exists(file)) {
            try (InputStream in = Files.newInputStream(file)) { values.load(in); }
            catch (IOException e) { LOGGER.error("Could not read {}. Using defaults.", file, e); }
        }
        otherCartMaxSpeed = readOtherSpeed(values, file);
        if (!Files.exists(file)) {
            values.setProperty("otherCartMaxSpeed", "vanilla");
            try {
                Files.createDirectories(file.getParent());
                try (OutputStream out = Files.newOutputStream(file)) {
                    values.store(out, "Player Cart Boost configuration; speeds are blocks per second");
                }
            } catch (IOException e) { LOGGER.error("Could not create {}.", file, e); }
        }
    }
    private static double readOtherSpeed(Properties values, Path file) {
        String raw = values.getProperty("otherCartMaxSpeed", "vanilla").trim();
        if (raw.equalsIgnoreCase("vanilla")) return -1.0;
        try {
            double value = Double.parseDouble(raw);
            if (Double.isFinite(value) && value > 0 && value <= 1000) return value;
        } catch (RuntimeException ignored) { }
        LOGGER.warn("Invalid otherCartMaxSpeed in {}; using vanilla.", file);
        return -1.0;
    }
}
