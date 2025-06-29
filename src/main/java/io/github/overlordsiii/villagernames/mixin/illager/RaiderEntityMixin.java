package io.github.overlordsiii.villagernames.mixin.illager;

import static io.github.overlordsiii.villagernames.VillagerNames.CONFIG;

import io.github.overlordsiii.villagernames.api.RaiderNameManager;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.raid.RaiderEntity;

@Mixin(RaiderEntity.class)
public abstract class RaiderEntityMixin implements RaiderNameManager {
	private String firstName = null;

	private String fullName = null;

	private String lastName = null;

	private String playerName = null;

	private String title = getDefaultTitle();

	/**
	 * Set pillager's first name
	 *
	 * @param firstName first name of illager entity
	 */
	@Override
	public void setFirstName(String firstName) {
		this.firstName = firstName;
		updateFullName();
	}

	@Override
	public String getPlayerName() {
		return playerName;
	}

	@Override
	public String getDefaultTitle() {
		// easier than having to use mixins on all non-abstract subclasses of RaiderEntity
		RaiderEntity entity = (RaiderEntity) (Object) this;
		if (entity instanceof EvokerEntity) {
			return "Evoker";
		} else if (entity instanceof IllusionerEntity) {
			return "Illusioner";
		} else if (entity instanceof PillagerEntity) {
			return "Pillager";
		} else if (entity instanceof VindicatorEntity) {
			return "Vindicator";
		} else if (entity instanceof WitchEntity) {
			return "Witch";
		}

		return null;
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

	/**
	 * Get first name of illager entity
	 *
	 * @return first name
	 */
	@Override
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * Set illager entity's last name
	 *
	 * @param lastNames illager entity last name
	 */
	@Override
	public void setLastName(String lastNames) {
		this.lastName = lastNames;
		updateFullName();
	}

	/**
	 * Get last name currently used
	 *
	 * @return illager entity's last name
	 */
	@Override
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * Gets the illager entity's title
	 *
	 * @return the title, or null if the title is toggled off
	 */
	@Override
	public String getTitle() {
		return title;
	}

	/**
	 * Removes the title of the entity if present
	 */
	@Override
	public void removeTitle() {
		this.title = null;
		updateFullName();
	}

	/**
	 * Sets the title for the illager
	 *
	 * @param title the name of the title
	 */
	@Override
	public void setTitle(String title) {
		this.title = title;
		updateFullName();
	}

	/**
	 * Returns the full name of the illager entity
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
	 * It is by default called internally by the method implementation*
	 */
	@Override
	public void updateFullName() {
		StringBuilder builder = new StringBuilder();
		if (this.firstName == null) { //have to do null check here instead of throwing NPE bc sometimes updateFullName is called before firstName is initialized by NBT, causing error when there isn't one
			return;
		}
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

		if (CONFIG.villagerGeneralConfig.professionNames && this.title != null && !this.title.equals("None")) {
			builder.append(" the ")
				.append(this.title);
		}

		this.fullName = builder.toString();
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
		if (title != null) {
			view.putString("title", title);
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
		view.getOptionalString("title").ifPresent(value -> this.title = value);
		view.getOptionalString("playerName").ifPresent(value -> this.playerName = value);
	}
}
