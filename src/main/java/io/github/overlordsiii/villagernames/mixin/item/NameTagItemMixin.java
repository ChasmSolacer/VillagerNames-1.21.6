package io.github.overlordsiii.villagernames.mixin.item;

import io.github.overlordsiii.villagernames.api.DefaultNameManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NameTagItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

@Mixin(NameTagItem.class)
public abstract class NameTagItemMixin extends Item {
	public NameTagItemMixin(Settings settings) {
		super(settings);
	}

	@Inject(method = "useOnEntity", at = @At("HEAD"))
	private void checkPlayerName(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		if (stack.get(DataComponentTypes.CUSTOM_NAME) != null && !(entity instanceof PlayerEntity)) {
			if (!user.getWorld().isClient && entity.isAlive()) {
				String stackName = stack.getName().getString();

				if (entity instanceof DefaultNameManager manager) {
					manager.setPlayerName(stackName);
				}
			}
		}
	}
}
