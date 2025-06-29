package io.github.overlordsiii.villagernames.integration.cca;

import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

public class RavagerCounterComponent implements IntComponent {
	//random starting point
	private int value = (int) (Math.random() * 100);

	@Override
	public int getValue() {
		return value;
	}

	@Override
	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public void readData(ReadView view) {
		view.getOptionalInt("ravagerValue").ifPresent(value -> this.value = value);
	}

	@Override
	public void writeData(WriteView view) {
		view.putInt("ravagerValue", this.value);
	}
}
