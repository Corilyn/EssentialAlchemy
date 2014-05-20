package cori.EssentialAlchemy.block.paving;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

// Dummy block that is used as an ItemStack to for research pages

public class RainbowDummy extends Block {

	public RainbowDummy() {
		super(Material.rock);
		
		// Paving spectrum
		setBlockTextureName("essentialalchemy:pavingspectrum");
		setBlockName("RainbowPavingDummy");
	}

}
