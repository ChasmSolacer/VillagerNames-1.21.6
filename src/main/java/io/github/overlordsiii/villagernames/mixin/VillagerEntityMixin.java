package io.github.overlordsiii.villagernames.mixin;

import io.github.overlordsiii.villagernames.VillagerNames;
import io.github.overlordsiii.villagernames.api.RaiderNameManager;
import io.github.overlordsiii.villagernames.api.VillagerNameManager;
import io.github.overlordsiii.villagernames.util.VillagerUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InteractionObserver;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static io.github.overlordsiii.villagernames.VillagerNames.CONFIG;

import java.util.Objects;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity implements InteractionObserver, VillagerDataContainer, VillagerNameManager {
	private String firstName = null;

	private String fullName = null;

	private String lastName = null;

	private String profession = null;

	private String playerName = null;

	public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "setVillagerData", at = @At("TAIL"))
	private void changeText(VillagerData villagerData, CallbackInfo ci) {
		if (!this.hasCustomName()) {
			VillagerUtil.createVillagerNames((VillagerEntity) (Object) this);
		}
		if (!VillagerUtil.getProfIdFromEntry(villagerData.profession()).equals(VillagerProfession.NONE.getValue().toString()) && this.hasCustomName()) {
			VillagerUtil.addProfessionName((VillagerEntity) (Object) this);
		}
		if (this.hasCustomName() && VillagerUtil.getProfIdFromEntry(villagerData.profession()).equals(VillagerProfession.NONE.getValue().toString())) {
			VillagerUtil.updateLostVillagerProfessionName((VillagerEntity) (Object) this);
		}
	}

	@Inject(method = "method_63666", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/WitchEntity;setPersistent()V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void redirectWitchConversion(ServerWorld serverWorld, WitchEntity witchEntity, CallbackInfo ci) {
		RaiderNameManager.setFirstName(witchEntity, getFirstName());

		if (CONFIG.villagerGeneralConfig.surNames) {
			RaiderNameManager.setLastName(witchEntity, getLastName());
		}

		if (this.playerName != null) {
			RaiderNameManager.setPlayerName(witchEntity, this.playerName);
		}
		//return null;
	}

	@Redirect(method = "onDeath", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"))
	private void redirectLogCallOnDeath(org.slf4j.Logger logger, String s, Object o, Object o1) {
		if (VillagerNames.CONFIG.villagerGeneralConfig.turnOffVillagerConsoleSpam) {
			String lol = "ha lol";
			// fall through
		} else {
			logger.info(s, o, o1);
		}
	}


	@SuppressWarnings("ALL")
	@Inject(method = "onGrowUp", at = @At("TAIL"))
	private void updateBabyText(CallbackInfo ci) {
		VillagerUtil.updateGrownUpVillagerName((VillagerEntity) (Object) (this));
	}

	@Inject(method = "writeCustomData", at = @At("TAIL"))
	private void serializeData(WriteView view, CallbackInfo ci) {
		if (firstName != null) {
			view.putString("firstName", firstName);
		}
		if (fullName != null) {
			view.putString("fullName", fullName);
		}
		if (lastName != null) {
			view.putString("lastName", lastName);
		}
		if (profession != null) {
			view.putString("profession", profession);
		}
		if (playerName != null) {
			view.putString("playerName", playerName);
		}
	}

	@Inject(method = "readCustomData", at = @At("TAIL"))
	private void deserializeData(ReadView view, CallbackInfo ci) {
		view.getOptionalString("firstName").ifPresent(value -> this.firstName = value);
		view.getOptionalString("fullName").ifPresent(value -> this.fullName = value);
		view.getOptionalString("lastName").ifPresent(value -> this.lastName = value);
		view.getOptionalString("profession").ifPresent(value -> this.profession = value);
		view.getOptionalString("playerName").ifPresent(value -> this.playerName = value);
	}

	/**
	 * Allows for the player to set a manual override for the full name.
	 * <p>
	 * Whatever the player name is set to, it will supercede any other name
	 *
	 * @param name
	 */
	@Override
	public void setPlayerName(String name) {
		this.playerName = name;
	}

	@Override
	public String getPlayerName() {
		return playerName;
	}

	/**
	 * Set villager's first name
	 *
	 * @param firstName first name of villager
	 */
	@Override
	public void setFirstName(String firstName) {
		this.firstName = firstName;
		updateFullName();
	}

	/**
	 * Get first name of villager
	 *
	 * @return first name
	 */
	@Override
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * Set Villager's last name
	 *
	 * @param lastName villager last name
	 */
	@Override
	public void setLastName(String lastName) {
		this.lastName = lastName;
		updateFullName();
	}

	/**
	 * Get last name currently used
	 *
	 * @return villager's last name
	 */
	@Override
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * Gets the villager's profession name
	 *
	 * @return the profession name, or null if the profession name is toggled off
	 */
	@Override
	public String getProfessionName() {
		return this.profession;
	}

	/**
	 * Removes the profession name if present
	 */
	@Override
	public void removeProfessionName() {
		this.profession = null;
		updateFullName();
	}

	/**
	 * Adds a new profession name to the villagers name
	 *
	 * @param appendedProfession the name of the profession
	 */
	@Override
	public void setProfessionName(String appendedProfession) {
		this.profession = appendedProfession;
		updateFullName();
	}


	/**
	 * Returns the full name of the villager
	 *
	 * @return the fullName
	 */
	@Override
	public String getFullName() {
		if (playerName != null) {
			return playerName;
		}
		return fullName;
	}

	/**
	 * Updates the full name. This should be called when any other method in this interface is referenced
	 * <p>
	 * It is by default called internally by the method implementation
	 *
	 * @return the new full name that is calculated based on other names
	 * @see VillagerEntityMixin for more information
	 */
	@Override
	@SuppressWarnings("RedundantCast")
	public void updateFullName() {
		StringBuilder builder = new StringBuilder();
		Objects.requireNonNull(this.firstName);
		if (CONFIG.villagerGeneralConfig.reverseLastNames && CONFIG.villagerGeneralConfig.surNames && this.lastName != null) {
			builder.append(this.lastName)
				.append(" ")
				.append(this.firstName);
		} else if (CONFIG.villagerGeneralConfig.surNames && this.lastName != null) {
			builder.append(this.firstName)
				.append(" ")
				.append(this.lastName);
		} else {
			builder.append(this.firstName);
		}

		if (((VillagerEntity) (Object) this).isBaby() && CONFIG.villagerGeneralConfig.childNames) {
			builder.append(" the Child");
		} else if (CONFIG.villagerGeneralConfig.professionNames && this.profession != null && !this.profession.equals("None")) {
			builder.append(" the ")
				.append(this.profession);
		}

		this.fullName = builder.toString();
	}

    /*
    @Inject(method = "createChild",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/passive/VillagerEntity;initialize(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/world/LocalDifficulty;Lnet/minecraft/entity/SpawnReason;Lnet/minecraft/entity/EntityData;Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/entity/EntityData;")
            , locals = LocalCapture.CAPTURE_FAILHARD)
    private void onCreateChild(ServerWorld serverWorld, PassiveEntity passiveEntity, CallbackInfoReturnable<VillagerEntity> cir, VillagerType villagerType3, VillagerEntity villagerEntity) {
        VillagerUtil.addLastNameFromBreeding(villagerEntity, (VillagerEntity)(Object)this);
    }
     */
}


