package core.texture;

import core.renderer.Renderer;
import core.texture.image.Image;
import core.utils.HardwareObject;



public class Texture extends HardwareObject {




	public enum Dimension {
		DIMENSION_1D,
		DIMENSION_2D, DIMENSION_3D,
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

	private int width;
	private int height;
	private int depth;

	private Dimension dimension;
	private MinFilter minFilter;
	private MagFilter magFilter;

	private int anisotropicFilteringAmont;
	private boolean requiresMipMapGeneration;

	private Image image;
	private String name;


	Texture() {
		super(Texture.class);
	}

	public Dimension getType() {
		return dimension;
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

	public Image getImage() {
		return image;
	}

	void setImage(Image image) {
		this.image = image;
	}

	void setWidth(int width) {
		this.width = width;
	}

	void setHeight(int height) {
		this.height = height;
	}

	void setDepth(int depth) {
		this.depth = depth;
	}

	void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}

	void setName(String name) {
		this.name = name;
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
