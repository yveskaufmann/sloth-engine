package renderer.font;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.util.Map;

public class FontSprite {

	/**
	 * Logger for this class
	 */
	private static final Logger Log = LoggerFactory.getLogger(FontSprite.class);

	/**
	 * This BufferedImage is only used in order to obtain
	 * the metrics of a renderer.font.
	 */
	private final static BufferedImage dummyImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

	private class GridPos {
		public final float x;
		public final float y;
		private final float endX;
		private final float endY;

		public GridPos(float x, float y) {
			this.x = x /  width;
			this.y = y / height;
			this.endX = this.x + cellWidth;
			this.endY = this.y + cellHeight;
		}

	}

	/**
	 * Count of Characters per line
	 */
	public float cols;

	/**
	 * Count of lines with characters
	 */
	public float rows;

	/**
	 * Width of a character cell in ndc pixels
	 */
	public float cellWidth;

	/**
	* Height of a character cell in ndc pixels
	*/
	public float cellHeight;

	/**
	 * The width of the sprite image in
	 * pixels
	 */
	public float width;

	/**
	 * The height of the sprite image in pixels.
	 */
	public float height;

	/**
	 * The image which contains the font sprite
	 */
	private BufferedImage fontSpriteImage;

	/**
	 * Contains the position in side the renderer.font sheet texture of each character.
	 */
	private Map<Character, GridPos> gridPosMap;


	@Override
	public String toString() {
		return String.format(
			"Grid Info = (rows = %f, cols = %f, cellWidth = %f, cellHeight = %f)",
			rows , cols, cellWidth, cellHeight);
	}

}
