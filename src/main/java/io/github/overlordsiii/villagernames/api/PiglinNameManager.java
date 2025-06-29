package io.github.overlordsiii.villagernames.api;

import io.github.overlordsiii.villagernames.VillagerNames;

import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.text.Text;

public interface PiglinNameManager extends DefaultNameManager {
	static String getPlayerName(AbstractPiglinEntity entity) {
		return ((PiglinNameManager) entity).getPlayerName();
	}

	static void setFirstName(String name, AbstractPiglinEntity entity) {
		((PiglinNameManager) entity).setFirstName(name);
	}

	static void setLastName(String name, AbstractPiglinEntity entity) {
		((PiglinNameManager) entity).setLastName(name);
	}

	static String getFirstName(AbstractPiglinEntity entity) {
		return ((PiglinNameManager) entity).getFirstName();
	}

	static String getLastName(AbstractPiglinEntity entity) {
		return ((PiglinNameManager) entity).getLastName();
	}

	static void updateLastName(AbstractPiglinEntity entity) {
		((PiglinNameManager) entity).updateFullName();
	}

	static String getFullName(AbstractPiglinEntity entity) {
		return ((PiglinNameManager) entity).getFullName();
	}

	static void setPlayerName(AbstractPiglinEntity entity, String name) {
		((PiglinNameManager) entity).setPlayerName(name);
	}

	static Text getFullNameAsText(AbstractPiglinEntity entity, boolean configFormatting) {
		return configFormatting ? Text.literal(getFullName(entity)).formatted(VillagerNames.CONFIG.villagerGeneralConfig.villagerTextFormatting.getFormatting()) : Text.literal(getFullName(entity));
	}

	default void debug() {
		//updateFullName();
		System.out.println("Piglin debug info");
		System.out.println("First name: " + getFirstName());
		System.out.println("Last Name: " + getLastName());
		System.out.println("Full Name: " + getFullName());
		System.out.println("Player Name: " + getPlayerName());
	}
}
