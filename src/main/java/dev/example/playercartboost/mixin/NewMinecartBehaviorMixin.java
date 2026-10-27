package dev.example.playercartboost.mixin;

import dev.example.playercartboost.PlayerCartBoost;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NewMinecartBehavior.class)
abstract class NewMinecartBehaviorMixin {
    @Inject(method = "getMaxSpeed", at = @At("HEAD"), cancellable = true)
    private void playerCartBoost$useAvailableSpeedRules(ServerLevel level,
                                                        CallbackInfoReturnable<Double> cir) {
        AbstractMinecart minecart = ((MinecartBehaviorAccessor) (Object) this).playerCartBoost$getMinecart();
        boolean hasPlayer = minecart.getPassengers().stream().anyMatch(passenger -> passenger instanceof Player);
        double vanillaCap = 8.0 / 20.0;
        if (!hasPlayer) {
            try {
                vanillaCap = level.getGameRules().get(GameRules.MAX_MINECART_SPEED) / 20.0;
            } catch (IllegalArgumentException ignored) {
                // The vanilla rule only exists in worlds with the Minecart Improvements experiment.
            }
        }

        double playerCap = level.getGameRules().get(PlayerCartBoost.PLAYER_CART_MAX_SPEED);
        double cap = PlayerCartBoost.maxSpeedFor(hasPlayer, vanillaCap, playerCap);
        if (minecart.isInWater()) cap *= 0.5;
        cir.setReturnValue(cap);
    }
}
