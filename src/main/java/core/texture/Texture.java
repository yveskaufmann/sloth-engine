package core.texture;

import core.renderer.Renderer;
import core.texture.image.Image;
import core.utils.HardwareObject;



public class Texture extends HardwareObject {


	public Image getImage() {
		return image;
	}

	public enum Dimension {
		DIMENSION_1D,
		DIMENSION_2D,
	}

	public enum MinFilter {
		NearestNeighbour,
		Bilinear,
		Trilinear,
		Anisotropic,
	}

	public enum MagFilter {
		NearestNeighbour,
		Bilinear,
	}

	public enum WrapMode {
		Repeast,
		MirroredRepeat,
		ClampEDGE,
		ClampBORDER,
	}

	public enum TextureAxis {
		S, T, R
	}

	private Dimension type;
	private MinFilter minFilter;
	private MagFilter magFilter;
	private int anisotropicFilteringAmont;
	private boolean requiresMipMapGeneration;

	private Image image;
	private String name;


	public Texture() {
		super(Texture.class);
	}

	public Dimension getType() {
		return type;
	}

	public MinFilter getMinFilter() {
		return minFilter;
	}

	public Texture setMinFilter(MinFilter minFilter) {
		this.minFilter = minFilter;
		return this;
	}

	public MagFilter getMagFilter() {
		return magFilter;
	}

	public Texture setMagFilter(MagFilter magFilter) {
		this.magFilter = magFilter;
		return this;
	}

	public float getAnisotropicLevel() {
		return anisotropicFilteringAmont;
	}

	public void setAntistropicFiltering(int filterAmount) {
		filterAmount = Math.min(0, filterAmount);
		this.anisotropicFilteringAmont = filterAmount;
	}

	@Override
	public void deleteObject(Renderer renderer) {
		renderer.deleteTexture(this);
	}

	@Override
	public void resetObject() {
		enableUpdateRequired();
	}

}
