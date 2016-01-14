package font;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import renderer.RendererExpception;
import static org.lwjgl.opengl.GL11.*;

import javax.swing.ImageIcon;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.IntBuffer;

public class FontRenderer {

	class GridInfo {
		public int rows;
		public int cols;
		public int cellWidth;
		public int cellHeight;
	}

	/**
	 * This BufferedImage is only used in order to obtain
	 * the metrics of a font.
	 */
	private final static BufferedImage dummyImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

	/**
	 * The list of chars which should be supported by this renderer.
	 */
	private final char[] SUPPORTED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789öäü.:,;-_#'\"*+?ß=<>".toCharArray();

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

		// Should be calculate the best suited size
		int width = 256;
		int height = 256;

		int gridCols = width / widthPerLetter;
		int gridRows = (int)Math.ceil(SUPPORTED_CHARS.length / gridCols) + 1;

		gridInfo.cols = gridCols;
		gridInfo.rows = gridRows;

		charsetImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) charsetImage.getGraphics();


		for (int i = 0; i < SUPPORTED_CHARS.length; i++) {
			char chr = SUPPORTED_CHARS[i];
			BufferedImage charImage = writeLetterAsImage(chr);

			int row = (int) Math.floor(i / gridCols);
			int col = i - row * gridCols;

			g.drawImage(charImage, col * widthPerLetter, row * heightPerLetter, charImage.getWidth(), charImage.getHeight(), null);
		}

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
			throw new RendererExpception("The creation of a texture object failed. Error Code:" + GL11.glGetError());
		}

		glBindTexture(GL_TEXTURE_2D, id);
		/**
		 * Defines what should happen if the texture coordinates exceed the size of this
		 * texture. In this case the texture should be clamped to the edge.
		 */
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);


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
		// TODO implement me please :)

	}

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
