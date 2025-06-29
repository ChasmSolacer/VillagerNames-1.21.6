package io.github.overlordsiii.villagernames.mixin;

import static io.github.overlordsiii.villagernames.VillagerNames.CONFIG;

import java.util.Objects;

import io.github.overlordsiii.villagernames.api.PiglinNameManager;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;

@Mixin(AbstractPiglinEntity.class)
public abstract class AbstractPiglinEntityMixin extends HostileEntity implements PiglinNameManager {
	private String firstName;
	private String lastName;
	private String fullName;
	private String playerName;

	public AbstractPiglinEntityMixin(EntityType<? extends AbstractPiglinEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public String getPlayerName() {
		return playerName;
	}

	@Override
	public void setFirstName(String name) {
		this.firstName = name;
		updateFullName();
	}

	@Override
	public void setLastName(String name) {
		this.lastName = name;
		updateFullName();
	}

	@Override
	public void setPlayerName(String name) {
		this.playerName = name;
	}

	@Override
	public String getFirstName() {
		return this.firstName;
	}

	@Override
	public String getLastName() {
		return this.lastName;
	}

	@Override
	public void updateFullName() {
		StringBuilder builder = new StringBuilder();
		Objects.requireNonNull(this.firstName);
		if (CONFIG.villagerGeneralConfig.piglinSurnames && this.lastName != null) {
			builder.append(this.firstName)
				.append(" ")
				.append(this.lastName);
		} else {
			builder.append(this.firstName);
		}

		this.fullName = builder.toString();
	}

	@Override
	public String getFullName() {
		if (playerName != null) {
			return playerName;
		}
		return fullName;
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
		if (playerName != null) {
			view.putString("playerName", playerName);
		}
	}

	@Inject(method = "readCustomData", at = @At("TAIL"))
	private void deserializeData(ReadView view, CallbackInfo ci) {
		view.getOptionalString("firstName").ifPresent(value -> this.firstName = value);
		view.getOptionalString("fullName").ifPresent(value -> this.fullName = value);
		view.getOptionalString("lastName").ifPresent(value -> this.lastName = value);
		view.getOptionalString("playerName").ifPresent(value -> this.playerName = value);
	}
}
