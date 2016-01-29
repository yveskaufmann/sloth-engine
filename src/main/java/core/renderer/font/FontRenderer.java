package core.renderer.font;

import core.math.Color;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import core.renderer.RendererExpception;
import core.Engine;
import core.shader.Shader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.*;

/**
 * Class which is responsible to Render
 * text.
 */
public class FontRenderer {

	private class GridInfo {
		public float rows;
		public float cols;
		public float cellWidth;
		public float cellHeight;
		public float width;
		public float height;

		@Override
		public String toString() {
			return String.format(
				"Grid Info = (rows = %f, cols = %f, cellWidth = %f, cellHeight = %f)",
				rows , cols, cellWidth, cellHeight);
		}
	}

	private class GridPos {
		public final float x;
		public final float y;
		private final float endX;
		private final float endY;

		public GridPos(float x, float y, GridInfo info) {
			this.x = x / info.width;
			this.y = y / info.height;
			this.endX = this.x + info.cellWidth;
			this.endY = this.y + info.cellHeight;
		}
	}

	/**
	 * Name of the core.shader which is responsible to
	 * render the text quads with the given font sprite core.texture.
	 */
	private static final String FONT_SHADER = "FontRender";

	/**
	 * Logger for this class
	 */
	private static final Logger Log = LoggerFactory.getLogger(FontRenderer.class);

	/**
	 * This BufferedImage is only used in order to obtain
	 * the metrics of a core.renderer.font.
	 */
	private final static BufferedImage dummyImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

	/**
	 * The list of chars which should be supported by this core.renderer.
	 */
	private final char[] SUPPORTED_CHARS ="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789öäü!§.$:%,&;-_#''\"*+?ß=<>(){} ".toCharArray();
	private static final char NEW_LINE = '\n';
	private static final char CARRIAGE_RETURN = '\r';
	private static final char UNKNOWN_CHAR = '?';

	/**
	 * Contains the position in side the core.renderer.font sheet core.texture of each character.
	 */
	private Map<Character, GridPos> gridPosMap;

	/**
	 * The core.renderer.font which should be used for this core.renderer
	 */
	private Font font;

	/**
	 * The metrics of this core.renderer.font.
	 */
	private FontMetrics metrics;


	/**
	 * The Charset image
	 */
	private BufferedImage charsetImage;

	/**
	 * Stores info about the created charset image
	 * in order to easily read out these images.
	 */
	private GridInfo gridInfo;

	/**
	 * The core.shader which is responsible to draw text.
	 */
	private Shader fontShader;

	/**
	 * The id of the core.texture core.renderer.font.
	 */
	private int textureId;

	/**
	 * VAO id
	 */
	private int vaoId;

	public FontRenderer(Font font) {
		this.font = font;
		this.metrics = getFontMetrics(font);
		this.textureId = -1;
		this.gridPosMap = new HashMap<>();
		prepareLetterBitmap();
	}

	private void prepareLetterBitmap() {
		gridInfo = new GridInfo();
		/**
		 * 	Find the size of the widest letters
		 * 	in order to use this size as the
		 * 	with for the letter grid.
 		 */
		int widthPerLetter = -1;
		int heightPerLetter = metrics.getHeight();
		for (char chr : SUPPORTED_CHARS) {
			int w = metrics.charWidth(chr);

			if (w > widthPerLetter) {
				widthPerLetter = w;
			}
		}

		// Calculates the required size for char set core.texture.
		gridInfo.width = 32;
		gridInfo.height = 32;
		do {
			do {
				gridInfo.width = gridInfo.height = gridInfo.width * 2;
				gridInfo.cols = gridInfo.width / widthPerLetter;
			} while( gridInfo.cols < 1.0f);

			gridInfo.cols = (float) Math.floor(gridInfo.cols);
			gridInfo.rows = (int)Math.ceil(SUPPORTED_CHARS.length / gridInfo.cols) + 1;

		} while( gridInfo.rows * heightPerLetter > gridInfo.height);




		gridInfo.cellWidth = widthPerLetter / gridInfo.width;
		gridInfo.cellHeight = heightPerLetter / gridInfo.height;
		Log.debug("Grids size of font sprite image (rows = {}, cols = {})", gridInfo.rows, gridInfo.cols);

		charsetImage = new BufferedImage((int)gridInfo.width, (int)gridInfo.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) charsetImage.getGraphics();


		for (int i = 0; i < SUPPORTED_CHARS.length; i++) {
			char chr = SUPPORTED_CHARS[i];
			BufferedImage charImage = writeLetterAsImage(chr);

			int row = (int) Math.floor(i / gridInfo.cols);
			int col = i - row * (int) gridInfo.cols;
			int x = col * widthPerLetter;
			int y = row * heightPerLetter;
			gridPosMap.put(chr, new GridPos(x, y, gridInfo));

			g.drawImage(charImage, x, y, charImage.getWidth(), charImage.getHeight(), null);
		}

		Log.debug("Created core.renderer.font {} core.texture sheet with the size ({},{}).",font.toString().replaceAll("java.awt.Font", "") , gridInfo.width, gridInfo.height);
		Log.debug("{}.", gridInfo);
	}

	/**
	 * Draw a given letter to an <code>BufferedImage</code> in white color
	 * on a transparent background.
	 *
	 * The white color serves as key color which can be replaced
	 * by a fragment core.shader.
	 *
	 * @param letter the letter which should be draw
     *
	 * @return an image with the rendered letter.
     */
	public BufferedImage writeLetterAsImage(char letter) {

		FontMetrics fontMetrics = getFontMetrics(font);

		int imageWidth = fontMetrics.charWidth(letter);
		int imageHeight = fontMetrics.getHeight();

		// Ensure that with and height are >= 1
		imageWidth = Math.max(1, imageWidth);
		imageHeight = Math.max(1, imageHeight);

		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) image.getGraphics();

		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(java.awt.Color.white);
		g.setFont(font);

		g.drawString(String.valueOf(letter), 0, fontMetrics.getAscent());
		g.dispose();

		return image;

	}

	/**
	 * Creates a core.texture object for a given buffered image.
	 *
	 * @param image The image which should be loaded into a core.texture
	 * @return the <code>id</code> of the created core.texture.
     */
	private int createTexture(BufferedImage image) {
		int id = glGenTextures();
		if (id < 0) {
			throw new RendererExpception("The creation of a core.texture object failed. Error Code:" + glGetError());
		}

		glBindTexture(GL_TEXTURE_2D, id);
		/**
		 * Defines what should happen if the core.texture coordinates exceed the size of this
		 * core.texture. In this case the core.texture should be clamped to the edge.
		 */
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR_MIPMAP_LINEAR);


		int width = image.getWidth();
		int height = image.getHeight();


		int pixels[] = new int[width * height];
		image.getRGB(0, 0, width, height, pixels, 0, width);

		IntBuffer buffer = core.utils.BufferUtils.createBuffer(pixels);
		buffer.flip();

		// We need only the alpha channel of the font  sprite
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		glGenerateMipmap(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, 0);

		return id;
	}



	private void prepare() {
		vaoId  = glGenVertexArrays();
		glBindVertexArray(vaoId);
		bindFontTexture();

		glEnable(GL11.GL_BLEND);
		glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL11.GL_DEPTH_TEST);
		glDepthMask(false);

	}

	private void bindFontTexture() {

		if (textureId < 0) {
			textureId = createTexture(this.charsetImage);
		}
		fontShader = Engine.getShader(FONT_SHADER);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, textureId);
	}

	private void prepareFontShader(char letter, float xStartQuad, float yStartQuad, float xEndQuad, float yEndQuad, Color color) {

		if (!gridPosMap.containsKey(letter)) {
			letter = UNKNOWN_CHAR;
		}
		GridPos pos = gridPosMap.get(letter);

		// Texture Coords
		fontShader.getUniform("fontSprite").setValue(0);
		fontShader.getUniform("xOffset").setValue(pos.x);
		fontShader.getUniform("yOffset").setValue(pos.y);
		fontShader.getUniform("xEnd").setValue(pos.endX);
		fontShader.getUniform("yEnd").setValue(pos.endY);

		// Quad Position and Size
		fontShader.getUniform("xStartQuad").setValue(xStartQuad);
		fontShader.getUniform("yStartQuad").setValue(yStartQuad);
		fontShader.getUniform("xEndQuad").setValue(xEndQuad);
		fontShader.getUniform("yEndQuad").setValue(yEndQuad);
		fontShader.getUniform("color").setValue(color);

		try {
			Engine.getCurrentRenderer().setShader(fontShader);
		} catch (IOException e) {
			Log.error("Font core.shader error, ", e);
		}

	}

	public void drawString(String text, int x, int y, float fontSize, Color color) {

		prepare();
		float windowWidth = Engine.getPrimaryWindow().getWidth();
		float windowHeight = Engine.getPrimaryWindow().getHeight();

		float ndc_x_start;
		float ndc_x = x / windowWidth;
		float ndc_y = y / windowHeight;
		ndc_x = ndc_x_start = (ndc_x * 2.0f) - 1.0f;
		ndc_y = -(ndc_y * 2.0f) + 1.0f;

		for(int i = 0; i < text.length(); i++) {
			char letter = text.charAt(i);
			if (letter == CARRIAGE_RETURN) continue;
			if (letter == NEW_LINE) {
				ndc_y -= (float ) metrics.getHeight() / windowHeight;
				ndc_x = ndc_x_start;
				continue;
			}

			float fontWidth = (metrics.charWidth(letter) / windowWidth) * fontSize;
			float fontHeight = (metrics.getHeight() / windowHeight) * fontSize;

			prepareFontShader(letter, ndc_x, ndc_y, ndc_x + fontWidth, ndc_y - fontHeight, color);
			glDrawArrays(GL_TRIANGLES, 0, 6);
			ndc_x += fontWidth;

		}
		endRendering();
	}

	private void endRendering() {
		unBindFontTexture();
		glDisable(GL11.GL_BLEND);
		glEnable(GL11.GL_DEPTH_TEST);
		glDepthMask(true);
		glBindVertexArray(0);
		glDeleteVertexArrays(vaoId);


	}

	private void unBindFontTexture() {

		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	/**
	 * Retrieves the created char set image which contains
	 * the core.renderer.font as image.
	 *
	 * @return the created char set image.
     */
	public BufferedImage getCharsetImage() {
		return charsetImage;
	}

	/**
	 * Retrieves the metrics of a specified core.renderer.font.
	 *
	 * @param font the core.renderer.font for which the metrics are requested
	 * @return the metrics of the specified core.renderer.font.
     */
	private FontMetrics getFontMetrics(Font font) {
		Graphics2D g = (Graphics2D) dummyImage.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		return g.getFontMetrics(font);
	}


	public static void main(String[] args) throws IOException {

		FontRenderer renderer = new FontRenderer(new Font("Courier", Font.PLAIN, 64));
		BufferedImage image = renderer.getCharsetImage();

		JFrame frame = new JFrame();
		JPanel imageView = new JPanel();
		imageView.setBackground(java.awt.Color.BLACK);
		imageView.add(new JLabel(new ImageIcon(image)));
		frame.add(imageView);
		frame.setTitle("Font Sprite Viewer");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(image.getWidth(), image.getHeight());
		frame.setVisible(true);
		frame.repaint();
	}

}
