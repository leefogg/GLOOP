package gloop.graphics.rendering.texturing;

import gloop.graphics.data.Buffer;
import gloop.graphics.data.DataConversion;
import gloop.graphics.data.DataType;
import gloop.graphics.rendering.Renderer;
import org.lwjgl.opengl.*;

import static org.lwjgl.opengl.GL30.*;

import org.lwjgl.util.vector.Vector3f;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Hashtable;

import static org.lwjgl.opengl.GL11.*;

public class Texture extends Buffer {
	private static Texture CurrentTexture;
	private static long
		TotalBytes,
		BytesAdded,
		BytesUpdated;
	protected static final ColorModel COLOR_MODEL = new ComponentColorModel(
			ColorSpace.getInstance(ColorSpace.CS_sRGB), // TODO: Probably not the best colour space
			new int[] {8,8,8,0},
			false,
			false,
			ComponentColorModel.OPAQUE,
			DataBuffer.TYPE_BYTE
		);
	protected static final ColorModel ALPHA_COLOR_MODEL = new ComponentColorModel(
			ColorSpace.getInstance(ColorSpace.CS_sRGB), // TODO: Probably not the best colour space
			new int[] {8,8,8,8},
			true,
			false,
			ComponentColorModel.TRANSLUCENT,
			DataBuffer.TYPE_BYTE
		);

	public static Texture Blank;
	static {
		setMissingTextureColor(Color.magenta);
	}

	protected final int
		width,
		height,
		ID;
	protected TextureFilter filter;
	protected TextureWrapMode textureWrap;
	protected TextureType type;
	protected final PixelFormat internalFormat;
	protected final DataType dataType;
	protected Color borderColor;
	protected final String name;

	Texture(String name, BufferedImage image, PixelComponents externalformat, PixelFormat internalformat) {
		this(name, image, externalformat, internalformat, TextureType.Bitmap);
	}
	Texture(String name, BufferedImage image, PixelComponents externalformat, PixelFormat internalformat, TextureType type) {
		this(name, image, externalformat, internalformat, type, DataType.UByte);
	}
	Texture(String name, BufferedImage image, PixelComponents externalformat, PixelFormat internalformat, TextureType type, DataType datatype) {
		this(
			name,
			getPixelData(image, externalformat),
			TextureTarget.Bitmap,
			externalformat,
			internalformat,
			type,
			datatype,
			image.getWidth(),
			image.getHeight()
		);
	}
	Texture(String name,
	        ByteBuffer pixeldata,
	        TextureTarget target,
	        PixelComponents externalformat,
	        PixelFormat internalformat,
	        TextureType type,
	        DataType datatype,
	        int width,
	        int height) {
		super(internalformat.getSize() * width * height);
		this.name = name;
		this.width = width;
		this.height = height;
		this.type = type;
		this.dataType = datatype;
		this.internalFormat = internalformat;
		ID = generateTextureID(); // Get a new ID for this texture

		//TODO: Needs to be more extensive

		bind();

		//glPixelStorei(GL_UNPACK_ALIGNMENT, bytesperpixel);
		// Upload the data
		writeData(target, internalformat, externalformat, datatype, pixeldata);

		Renderer.checkErrors();

		// Set default attributes. Mandatory for texture completion.
		setWrapMode(TextureWrapMode.Repeat);
		setFilteringMode(TextureFilter.Linear);

		TextureManager.register(this);
	}

	protected void writeData(TextureTarget target,
	                         PixelFormat internalformat,
	                         PixelComponents externalformat,
	                         DataType datatype,
	                         ByteBuffer pixeldata) {
		writeData(target, internalformat, externalformat, datatype, pixeldata, 0, 0);
	}
	protected void writeData(TextureTarget target,
	                         PixelFormat internalformat,
	                         PixelComponents externalformat,
	                         DataType datatype,
	                         ByteBuffer pixeldata,
	                         int level,
	                         int border) {
		glTexImage2D(
				target.getGLEnum(),
				level,
				internalformat.getGLEnum(),
				width,
				height,
				border,
				externalformat.getGLEnum(),
				datatype.getGLEnum(),
				pixeldata
		);
	}

	public boolean bind() {
		if (isDisposed())
			return false;

		glBindTexture(type.getGLEnum(), getID());
		CurrentTexture = this;
		return true;
	}

	public final String getName() { return name; }

	public final TextureFilter getFilterMode() { return filter; }

	public void setFilteringMode(TextureFilter filter) {
		this.filter = filter;

		setAttribute(TextureAttribute.MinFilter, this.filter.getGLEnum());
		setAttribute(TextureAttribute.MagFilter, this.filter.getGLEnum());
	}

	public final TextureWrapMode getWrapMode() { return textureWrap; }

	public void setWrapMode(TextureWrapMode mode) {
		if (!bind())
			return;

		textureWrap = mode;
		setAttribute(TextureAttribute.WrapS, textureWrap.getGLEnum());
		setAttribute(TextureAttribute.WrapT, textureWrap.getGLEnum());
		setAttribute(TextureAttribute.WrapR, textureWrap.getGLEnum());
	}

	public final Color getBorderColor() { return borderColor; }

	public void setBorderColor(Vector3f color) {
		setBorderColor(color.x, color.y, color.z);
	}
	public void setBorderColor(float red, float green, float blue) {
		setBorderColor(1, red, green, blue);
	}
	public void setBorderColor(float alpha, float red, float green, float blue) {
		red = Math.min(red, 1);
		green = Math.min(green, 1);
		blue = Math.min(blue, 1);

		setAttribute(TextureAttribute.BorderColor, DataConversion.toGLBuffer(new float[] {alpha, red, green, blue}));

		borderColor = new Color(alpha, red, green, blue);
	}

	protected void setAttribute(TextureAttribute attribute, int value) {
		//TODO: Add all parameters as of https://www.khronos.org/registry/OpenGL-Refpages/gl4/html/glTexParameter.xhtml
		if (bind())
			glTexParameteri(
				type.getGLEnum(),
				attribute.getGLEnum(),
				value
			);
	}
	protected void setAttribute(TextureAttribute attribute, java.nio.FloatBuffer value) {
		if (bind())
			glTexParameter(
				type.getGLEnum(),
				attribute.getGLEnum(),
				value
			);
	}
	protected void setAttribute(TextureAttribute attribute, java.nio.IntBuffer value) {
		if (bind())
			glTexParameter(
				type.getGLEnum(),
				attribute.getGLEnum(),
				value
			);
	}

	@Override
	public final void dispose() {
		glDeleteTextures(getID());
		TextureManager.unregister(this);
		super.dispose();
	}

	public final int getWidth() { return width; }

	public final int getHeight() { return height; }

	public final int getID() { return ID; }

	public boolean isTransparent() { return internalFormat.getAlphaBits() > 0; }

	public void generateMipMaps() {
		if (bind())
			glGenerateMipmap(type.getGLEnum());
	}

	public void generateAnisotropicMipMaps(int desiredamount) {
		if (!isAnisotropicFilteringSupported())
			return;

		int supportedmipmaps = getMaxAnisotropicSamplesSupported();
		float amount = Math.min(desiredamount, supportedmipmaps);
		if (bind())
			glTexParameterf(type.getGLEnum(), EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
	}

	public static boolean isAnisotropicFilteringSupported() {
		return GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic;
	}

	public static int getMaxAnisotropicSamplesSupported() {
		return glGetInteger(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);
	}

	public static void setMissingTextureColor(Color color) {
		if (Blank != null)
			Blank.requestDisposal();

		Blank = new ColorTexture("blank", color);
	}

	protected int generateTextureID() {
		return glGenTextures();
	}

	protected static ByteBuffer getPixelData(BufferedImage image, PixelComponents pixelcomponents) {
		int numberofcomponents = pixelcomponents.getNumberOfComponents();
		byte[] data = new byte[image.getWidth() * image.getHeight() * numberofcomponents];
		int component = 0;
		for (int y=0; y<image.getHeight(); y++) {
			for (int x=0; x<image.getWidth(); x++) {
				int color = image.getRGB(x,y);
				byte b = (byte)((color >> 0) & 0xff);
				byte g = (byte)((color >> 8) & 0xff);
				byte r = (byte)((color >> 16)& 0xff);
				byte a = (byte)((color >> 24) & 0xff);
				data[component++] = r;

				if (numberofcomponents < 2)
					continue;
				data[component++] = g;

				if (numberofcomponents < 3)
					continue;
				data[component++] = b;

				if (numberofcomponents < 4)
					continue;
				data[component++] = a;
			}
		}

		ByteBuffer pixeldata = DataConversion.toGLBuffer(data);
		pixeldata.order(ByteOrder.nativeOrder());

		return pixeldata;
	}

	private static BufferedImage createByteBufferImage(BufferedImage image) {
		int bytesperpixel;
		ColorModel colormodel;
		if (image.getColorModel().hasAlpha()) {
			colormodel = ALPHA_COLOR_MODEL;
			bytesperpixel = 4;
		} else {
			colormodel = COLOR_MODEL;
			bytesperpixel = 3;
		}

		WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, image.getHeight(), image.getHeight(), bytesperpixel, null);
		BufferedImage newimage = new BufferedImage(colormodel, raster, false, new Hashtable<>());
		Graphics canvas = newimage.getGraphics();
		canvas.drawImage(image, 0, 0, null);
		canvas.dispose();

		return newimage;
	}

	public static Texture getCurrentBoundTexture() { return CurrentTexture; }

	@Override
	public String toString() {
		return name;
	}

	@Override
	protected void alloc(long size) {
		TotalBytes += size;
		BytesAdded += size;
	}

	@Override
	protected void update(long size) {
		BytesUpdated += size;
	}

	public static void clearStatistics() {
		BytesUpdated = 0;
		BytesAdded = 0;
	}

	public static long getTotalBytes() {
		return TotalBytes;
	}
	public static long getBytesAdded() {
		return BytesAdded;
	}
	public static long getBytesUpdated() {
		return BytesUpdated;
	}

	private static BufferedImage[] loadImages(String[] paths) throws IOException {
		BufferedImage[] images = new BufferedImage[paths.length];

		for (int i=0; i<paths.length; i++)
			images[i] = ImageIO.read(new File(paths[i]));
		return images;
	}
}
