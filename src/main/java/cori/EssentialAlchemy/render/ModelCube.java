package cori.EssentialAlchemy.render;

import net.minecraft.client.model.ModelBat;
import net.minecraft.client.model.ModelRenderer;

public class ModelCube extends ModelBat {
	public static ModelCube Instance = new ModelCube();
	private ModelRenderer cube;
	
	public ModelCube() {
		cube = new ModelRenderer(this,0,0);
		int size = 16;
		cube.addBox(-8,-8,-8,16,16,16);
		cube.setTextureSize(64, 32);
		cube.mirror = true;
	}
	
	public void render() {
		float f = 0.0625f;
		cube.render(f);
	}
}
