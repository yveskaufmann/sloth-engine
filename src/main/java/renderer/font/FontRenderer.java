package renderer.font;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import renderer.RendererExpception;
import sandbox.EngineContext;
import shader.Shader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;

import javax.swing.ImageIcon;
import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

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
	 * Name of the shader which is responsible to
	 * render the text quads with the given font sprite texture.
	 */
	private static final String FONT_SHADER = "FontRender";

	/**
	 * Logger for this class
	 */
	private static final Logger Log = LoggerFactory.getLogger(FontRenderer.class);

	/**
	 * This BufferedImage is only used in order to obtain
	 * the metrics of a renderer.font.
	 */
	private final static BufferedImage dummyImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

	/**
	 * The list of chars which should be supported by this renderer.
	 */
	private final char[] SUPPORTED_CHARS ="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789öäü!§.$:%,&;-_#''\"*+?ß=<> ".toCharArray();
	private static final char NEW_LINE = '\n';
	private static final char CARRIAGE_RETURN = '\r';
	private static final char UNKNOWN_CHAR = '?';

	/**
	 * Contains the position in side the renderer.font sheet texture of each character.
	 */
	private Map<Character, GridPos> gridPosMap;

	/**
	 * The renderer.font which should be used for this renderer
	 */
	private Font font;

	/**
	 * The metrics of this renderer.font.
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
	 * The shader which is responsible to draw text.
	 */
	private Shader fontShader;

	/**
	 * The id of the texture renderer.font.
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

		// Calculates the required size for char set texture.
		gridInfo.width = 32;
		gridInfo.height = 32;
		do {
			gridInfo.width = gridInfo.height = gridInfo.width * 2;
			gridInfo.cols = (float) Math.floor(gridInfo.width / widthPerLetter);
			gridInfo.rows = (int)Math.ceil(SUPPORTED_CHARS.length / gridInfo.cols) + 1;

		} while(gridInfo.rows * heightPerLetter > gridInfo.height);
		gridInfo.cellWidth = widthPerLetter / gridInfo.width;
		gridInfo.cellHeight = heightPerLetter / gridInfo.width;
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

		Log.debug("Created renderer.font {} texture sheet with the size ({},{}).",font.toString().replaceAll("java.awt.Font", "") , gridInfo.width, gridInfo.height);
		Log.debug("{}.", gridInfo);
	}

	/**
	 * Draw a given letter to an <code>BufferedImage</code> in white color
	 * on a transparent background.
	 *
	 * The white color serves as key color which can be replaced
	 * by a fragment shader.
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
		g.setColor(Color.white);
		g.setFont(font);

		g.drawString(String.valueOf(letter), 0, fontMetrics.getAscent());
		g.dispose();

		return image;

	}

	/**
	 * Creates a texture object for a given buffered image.
	 *
	 * @param image The image which should be loaded into a texture
	 * @return the <code>id</code> of the created texture.
     */
	private int createTexture(BufferedImage image) {
		int id = glGenTextures();
		if (id < 0) {
			throw new RendererExpception("The creation of a texture object failed. Error Code:" + glGetError());
		}

		glBindTexture(GL_TEXTURE_2D, id);
		/**
		 * Defines what should happen if the texture coordinates exceed the size of this
		 * texture. In this case the texture should be clamped to the edge.
		 */
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR_MIPMAP_LINEAR);


		int width = image.getWidth();
		int height = image.getHeight();


		int pixels[] = new int[width * height];
		// image.getData().getPixels(0, 0, width, height, pixels);
		image.getRGB(0, 0, width, height, pixels, 0, width);

		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4); //4 for RGBA, 3 for RGB

		for(int y = 0; y < image.getHeight(); y++) {
			for(int x = 0; x < image.getWidth(); x++){
				int pixel = pixels[y * image.getWidth() + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF));     	// Red component
				buffer.put((byte) ((pixel >> 8) & 0xFF));      	// Green component
				buffer.put((byte) (pixel & 0xFF));             	// Blue component
				buffer.put((byte) ((pixel >> 24) & 0xFF));    	// Alpha component. Only for RGBA
			}
		}

		buffer.flip();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
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
		fontShader = EngineContext.getShader(FONT_SHADER);
		fontShader.getUniform("fontSprite").setValue(0);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, textureId);
	}

	private void prepareFontShader(char letter, float xStartQuad, float yStartQuad, float xEndQuad, float yEndQuad) {

		if (!gridPosMap.containsKey(letter)) {
			letter = UNKNOWN_CHAR;
		}
		GridPos pos = gridPosMap.get(letter);
		// Texture Coords
		fontShader.getUniform("xOffset").setValue(pos.x);
		fontShader.getUniform("yOffset").setValue(pos.y);
		//fontShader.getUniform("xEnd").setValue(pos.x + gridInfo.cellWidth);
		fontShader.getUniform("xEnd").setValue(pos.endX);
		fontShader.getUniform("yEnd").setValue(pos.endY);
		//fontShader.getUniform("yEnd").setValue(pos.y + gridInfo.cellHeight);

		// Quad Position and Size
		fontShader.getUniform("xStartQuad").setValue(xStartQuad);
		fontShader.getUniform("yStartQuad").setValue(yStartQuad);
		fontShader.getUniform("xEndQuad").setValue(xEndQuad);
		fontShader.getUniform("yEndQuad").setValue(yEndQuad);

		try {
			EngineContext.getCurrentRenderer().setShader(fontShader);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void drawString(String text, int x, int y) {
		prepare();

		float windowWidth = EngineContext.getPrimaryWindow().getWidth();
		float windowHeight = EngineContext.getPrimaryWindow().getHeight();
		float fontSize = windowWidth / windowHeight;

		float ndc_x_start;
		float ndc_x = ((float)x / windowWidth);
		float ndc_y = y / windowHeight;
		ndc_x = ndc_x_start = (ndc_x * 2.0f) - 1.0f;
		ndc_y = -(ndc_y * 2.0f) + 1.0f;

		for(int i = 0; i < text.length(); i++) {
			char letter = text.charAt(i);

			if (letter == CARRIAGE_RETURN) continue;
			if (letter == NEW_LINE) {
				ndc_y -= (float ) metrics.getHeight() / windowHeight;
				ndc_x = ndc_x_start;
			}

			float fontWidth = (metrics.charWidth(letter) / windowWidth) * fontSize;
			float fontHeight = metrics.getHeight() / windowHeight * fontSize;
			prepareFontShader(letter, ndc_x, ndc_y, ndc_x + fontWidth, ndc_y - fontHeight);
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
	 * the renderer.font as image.
	 *
	 * @return the created char set image.
     */
	public BufferedImage getCharsetImage() {
		return charsetImage;
	}

	/**
	 * Retrieves the metrics of a specified renderer.font.
	 *
	 * @param font the renderer.font for which the metrics are requested
	 * @return the metrics of the specified renderer.font.
     */
	private FontMetrics getFontMetrics(Font font) {
		Graphics2D g = (Graphics2D) dummyImage.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		return g.getFontMetrics(font);
	}


	public static void main(String[] args) {
		FontRenderer renderer = new FontRenderer(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
		BufferedImage image = renderer.getCharsetImage();

		JFrame frame = new JFrame();
		JPanel imageView = new JPanel();
		imageView.setBackground(Color.BLACK);
		imageView.add(new JLabel(new ImageIcon(image)));
		frame.add(imageView);
		frame.setTitle("Font Sprite Viewer");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(image.getWidth(), image.getHeight());
		frame.setVisible(true);
		frame.repaint();
	}

}
