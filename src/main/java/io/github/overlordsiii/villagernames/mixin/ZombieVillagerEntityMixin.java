package io.github.overlordsiii.villagernames.mixin;

import static io.github.overlordsiii.villagernames.VillagerNames.CONFIG;

import java.util.Objects;

import io.github.overlordsiii.villagernames.api.ZombieVillagerNameManager;
import io.github.overlordsiii.villagernames.util.VillagerUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ZombieVillagerEntity.class)
public abstract class ZombieVillagerEntityMixin extends ZombieEntity implements VillagerDataContainer, ZombieVillagerNameManager {

    private String firstName;
    private String lastName;
    private String fullName;
    private String playerName;

    public ZombieVillagerEntityMixin(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    // finishConversion
    @Inject(method = "method_63659", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/VillagerEntity;initialize(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/world/LocalDifficulty;Lnet/minecraft/entity/SpawnReason;Lnet/minecraft/entity/EntityData;)Lnet/minecraft/entity/EntityData;"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void returnOriginalVillagerName(ServerWorld serverWorld, VillagerEntity villagerEntity, CallbackInfo ci) {
        VillagerUtil.removeZombieVillagerName(villagerEntity, (ZombieVillagerEntity)(Object)this);
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
    public String getPlayerName() {
        return playerName;
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
