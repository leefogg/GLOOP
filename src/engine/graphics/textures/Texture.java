package engine.graphics.textures;

import engine.graphics.data.Buffer;
import engine.graphics.data.DataConversion;
import engine.graphics.models.DataType;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.opengl.GLContext;
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
	private static long
		TotalBytes,
		BytesAdded,
		BytesUpdated;

	protected static final ColorModel ColorModel = new ComponentColorModel(
			ColorSpace.getInstance(ColorSpace.CS_sRGB), // TODO: Probably not the best colour space
			new int[] {8,8,8,0},
			false,
			false,
			ComponentColorModel.OPAQUE,
			DataBuffer.TYPE_BYTE
		);
	protected static final ColorModel AlphaColorModel = new ComponentColorModel(
			ColorSpace.getInstance(ColorSpace.CS_sRGB), // TODO: Probably not the best colour space
			new int[] {8,8,8,8},
			true,
			false,
			ComponentColorModel.TRANSLUCENT,
			DataBuffer.TYPE_BYTE
		);

	public static Texture blank;
	static {
		setMissingTextureColor(Color.magenta);
	}

	protected final int
		width,
		height,
		ID;
	protected TextureFilter Filter;
	protected TextureWrapMode TextureWrap;
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
			DataType.UByte,
			image.getWidth(),
			image.getHeight()
		);
	}
	Texture(String name, ByteBuffer pixeldata, TextureTarget target, PixelComponents externalformat, PixelFormat internalformat, TextureType type, DataType datatype, int width, int height) {
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
		writeData(target, pixeldata, externalformat);

		// Set default attributes. Mandatory for texture completion.
		setWrapMode(TextureWrapMode.Repeat);
		setFilteringMode(TextureFilter.Linear);

		TextureManager.register(this);
	}

	protected void writeData(TextureTarget target, ByteBuffer pixeldata, PixelComponents externalformat) {
		writeData(target, pixeldata, externalformat, 0, 0);
	}
	protected void writeData(TextureTarget target, ByteBuffer pixeldata, PixelComponents externalformat, int level, int border) {
		glTexImage2D(
				target.getGLEnum(),
				level,
				internalFormat.getGLEnum(),
				width,
				height,
				border,
				externalformat.getGLEnum(),
				dataType.getGLEnum(),
				pixeldata
		);
	}

	public boolean bind() {
		if (isDisposed())
			return false;

		glBindTexture(type.getGLEnum(), getID());
		return true;
	}

	public final String getName() { return name; }

	public final TextureFilter getFilterMode() { return Filter; }

	public void setFilteringMode(TextureFilter filter) {
		this.Filter = filter;

		setAttribute(TextureAttribute.MinFilter, Filter.getGLEnum());
		setAttribute(TextureAttribute.MagFilter, Filter.getGLEnum());
	}

	public final TextureWrapMode getWrapMode() { return TextureWrap; }

	public void setWrapMode(TextureWrapMode mode) {
		if (!bind())
			return;

		TextureWrap = mode;
		setAttribute(TextureAttribute.WrapS, TextureWrap.getGLEnum());
		setAttribute(TextureAttribute.WrapT, TextureWrap.getGLEnum());
		setAttribute(TextureAttribute.WrapR, TextureWrap.getGLEnum());
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
		glTexParameteri(
				type.getGLEnum(),
				attribute.getGLEnum(),
				value
			);
	}
	protected void setAttribute(TextureAttribute attribute, java.nio.FloatBuffer value) {
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

	public static final boolean isAnisotropicFilteringSupported() {
		return GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic;
	}

	public static final int getMaxAnisotropicSamplesSupported() {
		return glGetInteger(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);
	}

	public static void setMissingTextureColor(Color color) {
		if (blank != null)
			blank.requestDisposal();

		blank = new ColorTexture("blank", color);
	}

	protected int generateTextureID() {
		return glGenTextures();
	}

	protected static final ByteBuffer getPixelData(BufferedImage image, PixelComponents pixelcomponents) {
		int numberofcomponents = pixelcomponents.getNumberOfBytes();
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

	private static final BufferedImage createByteBufferImage(BufferedImage image) {
		int bytesperpixel;
		ColorModel colormodel;
		if (image.getColorModel().hasAlpha()) {
			colormodel = AlphaColorModel;
			bytesperpixel = 4;
		} else {
			colormodel = ColorModel;
			bytesperpixel = 3;
		}

		WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, image.getHeight(), image.getHeight(), bytesperpixel, null);
		BufferedImage newimage = new BufferedImage(colormodel, raster, false, new Hashtable<>());
		Graphics canvas = newimage.getGraphics();
		canvas.drawImage(image, 0, 0, null);
		canvas.dispose();

		return newimage;
	}

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

	public static final long getTotalBytes() {
		return TotalBytes;
	}
	public static final long getBytesAdded() {
		return BytesAdded;
	}
	public static final long getBytesUpdated() {
		return BytesUpdated;
	}

	private static final BufferedImage[] loadImages(String[] paths) throws IOException {
		BufferedImage[] images = new BufferedImage[paths.length];

		for (int i=0; i<paths.length; i++)
			images[i] = ImageIO.read(new File(paths[i]));
		return images;
	}
}
