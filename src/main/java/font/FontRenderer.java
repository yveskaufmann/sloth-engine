package font;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import renderer.RendererExpception;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;

import javax.swing.ImageIcon;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class FontRenderer {



	private class GridInfo {
		public int rows;
		public int cols;
		public int cellWidth;
		public int cellHeight;
	}

	private class GridPos {
		public GridPos(int x, int y, int width) {
			this.x = x;
			this.y = y;
			this.offset = y * width + x;
		}
		public int x;
		public int y;
		public int offset;
	}

	/**
	 * Logger for this class
	 */
	Logger Log = LoggerFactory.getLogger(FontRenderer.class);

	/**
	 * This BufferedImage is only used in order to obtain
	 * the metrics of a font.
	 */
	private final static BufferedImage dummyImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

	/**
	 * The list of chars which should be supported by this renderer.
	 */
	private final char[] SUPPORTED_CHARS ="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789öäü.:,;-_#'\"*+?ß=<> ".toCharArray();

	/**
	 * Contains the position in side the font sheet texture of each character.
	 */
	private Map<Character, GridPos> gridPosMap;

	/**
	 * The font which should be used for this renderer
	 */
	private Font font;

	/**
	 * The metrics of this font.
	 */
	private FontMetrics metrics;

	/**
	 * The id of the texture font.
	 */
	private int textureId;

	/**
	 * The Charset image
	 */
	private BufferedImage charsetImage;

	/**
	 * Stores info about the created charset image
	 * in order to easily read out these images.
	 */
	private GridInfo gridInfo;

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

		gridInfo.cellWidth = widthPerLetter;
		gridInfo.cellHeight = heightPerLetter;

		// Calculates the required size for char set texture.
		int width = 32;
		int height = 32;
		do {
			width = height = width * 2;
			gridInfo.cols = width / widthPerLetter;
			gridInfo.rows = (int)Math.ceil(SUPPORTED_CHARS.length / gridInfo.cols) + 1;

		} while(gridInfo.rows * heightPerLetter > height);



		charsetImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) charsetImage.getGraphics();


		for (int i = 0; i < SUPPORTED_CHARS.length; i++) {
			char chr = SUPPORTED_CHARS[i];
			BufferedImage charImage = writeLetterAsImage(chr);

			int row = (int) Math.floor(i / gridInfo.cols);
			int col = i - row * gridInfo.cols;
			int x = col * widthPerLetter;
			int y = row * heightPerLetter;
			gridPosMap.put(chr, new GridPos(x, y, width));

			g.drawImage(charImage, x, y, charImage.getWidth(), charImage.getHeight(), null);
		}

		Log.info("Created font {} texture sheet with the size ({},{}).",font.toString().replaceAll("java.awt.Font", "") , width, height);
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
		g.setColor(Color.BLACK);
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


		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		int width = image.getWidth();
		int height = image.getHeight();


		int pixels[] = new int[width * height];
		image.getData().getPixels(0, 0, width, height, pixels);

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, IntBuffer.wrap(pixels));
		glBindTexture(GL_TEXTURE_2D, 0);

		return id;
	}

	public void drawString(String text, int x, int y) {

		bindFontTexture();

		for(int i = 0; i < text.length(); i++) {
			char chr = text.charAt(i);
			GridPos pos = gridPosMap.get(chr);
			if (pos == null) {
				pos = gridPosMap.get('?');
			}
		}
	}

	private void bindFontTexture() {

		if (this.textureId < 0) {
			this.textureId = createTexture(this.charsetImage);
		}
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, textureId);
	}

	/**
	 * Retrieves the created char set image which contains
	 * the font as image.
	 *
	 * @return the created char set image.
     */
	public BufferedImage getCharsetImage() {
		return charsetImage;
	}

	/**
	 * Retrieves the metrics of a specified font.
	 *
	 * @param font the font for which the metrics are requested
	 * @return the metrics of the specified font.
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
		imageView.add(new JLabel(new ImageIcon(image)));
		frame.add(imageView);

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		frame.setSize(image.getWidth(), image.getHeight());
		frame.setVisible(true);
		frame.repaint();
	}

}
