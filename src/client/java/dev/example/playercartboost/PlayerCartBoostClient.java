package dev.example.playercartboost;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.phys.Vec3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;

public final class PlayerCartBoostClient implements ClientModInitializer {
    private enum DisplayMode { TOTAL, AXES, OFF }

    private static final String MODE_PROPERTY_PREFIX = "player.";
    private static final Properties PLAYER_MODES = new Properties();
    private static final Path SETTINGS_FILE = FabricLoader.getInstance().getConfigDir()
            .resolve("player-cart-boost-client.properties");
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(PlayerCartBoost.MOD_ID, "controls"));
    private static KeyMapping cycleDisplayKey;
    private static UUID activePlayer;
    private static DisplayMode displayMode = DisplayMode.TOTAL;
    private static boolean showingSpeed;

    @Override
    public void onInitializeClient() {
        loadPlayerModes();
        cycleDisplayKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.player_cart_boost.cycle_speed_display",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_H,
                CATEGORY));
        ClientTickEvents.END_CLIENT_TICK.register(PlayerCartBoostClient::tick);
    }

    private static void tick(Minecraft client) {
        if (cycleDisplayKey.consumeClick() && client.player != null) {
            activatePlayer(client.player.getUUID());
            displayMode = switch (displayMode) {
                case TOTAL -> DisplayMode.AXES;
                case AXES -> DisplayMode.OFF;
                case OFF -> DisplayMode.TOTAL;
            };
            savePlayerMode(activePlayer, displayMode);
            if (displayMode == DisplayMode.OFF) clearActionbar(client);
        }

        if (client.player == null) {
            activePlayer = null;
            clearActionbar(client);
            return;
        }

        activatePlayer(client.player.getUUID());
        if (displayMode == DisplayMode.OFF) {
            clearActionbar(client);
            return;
        }

        if (client.player.getVehicle() instanceof AbstractMinecart cart) {
            Vec3 movement = cart.getDeltaMovement();
            Component message = displayMode == DisplayMode.TOTAL
                    ? Component.translatable("hud.player_cart_boost.speed_total", formatSpeed(movement.length() * 20.0))
                    : Component.translatable("hud.player_cart_boost.speed_axes",
                            formatSpeed(movement.x * 20.0),
                            formatSpeed(movement.y * 20.0),
                            formatSpeed(movement.z * 20.0));
            client.gui.hud.setOverlayMessage(message, false);
            showingSpeed = true;
        } else {
            clearActionbar(client);
        }
    }

    private static void activatePlayer(UUID playerId) {
        if (!playerId.equals(activePlayer)) {
            activePlayer = playerId;
            displayMode = readPlayerMode(playerId);
        }
    }

    private static String formatSpeed(double blocksPerSecond) {
        return Math.abs(blocksPerSecond) >= 10_000_000.0
                ? String.format(Locale.ROOT, "%.2e", blocksPerSecond)
                : String.format(Locale.ROOT, "%.2f", blocksPerSecond);
    }

    private static void clearActionbar(Minecraft client) {
        if (showingSpeed && client.gui != null) {
            client.gui.hud.setOverlayMessage(Component.empty(), false);
            showingSpeed = false;
        }
    }

    private static void loadPlayerModes() {
        if (!Files.exists(SETTINGS_FILE)) return;
        try (InputStream in = Files.newInputStream(SETTINGS_FILE)) {
            PLAYER_MODES.load(in);
        } catch (IOException e) {
            PlayerCartBoost.LOGGER.warn("Could not read client display settings {}.", SETTINGS_FILE, e);
        }
    }

    private static DisplayMode readPlayerMode(UUID playerId) {
        try {
            return DisplayMode.valueOf(PLAYER_MODES.getProperty(MODE_PROPERTY_PREFIX + playerId, "TOTAL"));
        } catch (IllegalArgumentException ignored) {
            return DisplayMode.TOTAL;
        }
    }

    private static void savePlayerMode(UUID playerId, DisplayMode mode) {
        PLAYER_MODES.setProperty(MODE_PROPERTY_PREFIX + playerId, mode.name());
        try {
            Files.createDirectories(SETTINGS_FILE.getParent());
            try (OutputStream out = Files.newOutputStream(SETTINGS_FILE)) {
                PLAYER_MODES.store(out, "Player Cart Boost per-player speed display modes");
            }
        } catch (IOException e) {
            PlayerCartBoost.LOGGER.warn("Could not save client display settings {}.", SETTINGS_FILE, e);
        }
    }
}
