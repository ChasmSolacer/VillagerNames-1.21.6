package io.github.overlordsiii.villagernames.mixin;

import io.github.overlordsiii.villagernames.util.VillagerUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = ZombieEntity.class, priority = 9999)
public abstract class ZombieEntityMixin extends HostileEntity {
	protected ZombieEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
	}

	// infectVillager
	@Inject(method = "method_63655", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void addZombieNameTag(ServerWorld serverWorld, VillagerEntity villagerEntity, ZombieVillagerEntity zombieVillagerEntity, CallbackInfo ci) {
		VillagerUtil.addZombieVillagerName(villagerEntity, zombieVillagerEntity);
	}
}
