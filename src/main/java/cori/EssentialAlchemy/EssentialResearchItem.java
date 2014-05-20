package cori.EssentialAlchemy;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchItem;


public class EssentialResearchItem extends ResearchItem {

	public EssentialResearchItem(String key, String category, AspectList tags,
			int col, int row, int complex, ResourceLocation icon) {
		super(key, category, tags, col, row, complex, icon);
	}

	public EssentialResearchItem(String key, String category, AspectList tags,
			int col, int row, int complex, ItemStack icon) {
		super(key, category, tags, col, row, complex, icon);
	}
	
	public EssentialResearchItem(String key, String category) {
		super(key, category);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public String getName() {
		return StatCollector.translateToLocal("ES.name."+key);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public String getText() {
		return StatCollector.translateToLocal("ES.lore."+key);
	}
}
