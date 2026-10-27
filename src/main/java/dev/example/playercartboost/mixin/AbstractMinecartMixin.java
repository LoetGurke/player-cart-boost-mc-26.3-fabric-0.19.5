package dev.example.playercartboost.mixin;

import dev.example.playercartboost.PlayerCartBoost;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractMinecart.class)
abstract class AbstractMinecartMixin {
    @Inject(method = "useExperimentalMovement", at = @At("HEAD"), cancellable = true)
    private static void playerCartBoost$useImprovedRailMovement(Level level,
                                                                 CallbackInfoReturnable<Boolean> cir) {
        // Reuse vanilla's improved rail-following algorithm without changing world feature flags.
        cir.setReturnValue(true);
    }

}
