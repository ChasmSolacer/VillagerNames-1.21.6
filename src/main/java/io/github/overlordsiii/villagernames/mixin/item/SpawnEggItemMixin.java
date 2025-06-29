package io.github.overlordsiii.villagernames.mixin.item;

import java.util.Optional;

import io.github.overlordsiii.villagernames.api.DefaultNameManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

@Mixin(SpawnEggItem.class)
public abstract class SpawnEggItemMixin extends Item {
	public SpawnEggItemMixin(Settings settings) {
		super(settings);
	}

	@Inject(method = "spawnBaby", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrementUnlessCreative(ILnet/minecraft/entity/LivingEntity;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void spawnBabyInject(PlayerEntity user, MobEntity entity, EntityType<? extends MobEntity> entityType, ServerWorld world, Vec3d pos, ItemStack stack, CallbackInfoReturnable<Optional<MobEntity>> cir, MobEntity mobEntity) {
		String stackName = stack.getName().getString();

		if (mobEntity instanceof DefaultNameManager manager) {
			manager.setPlayerName(stackName);
		}
	}
}
