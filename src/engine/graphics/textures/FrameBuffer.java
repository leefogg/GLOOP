package engine.graphics.textures;

import engine.graphics.models.DataType;
import engine.graphics.rendering.Renderer;
import engine.graphics.rendering.Viewport;
import engine.graphics.data.DataConversion;
import org.lwjgl.util.vector.Vector4f;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.*;

public class FrameBuffer { // TODO: implements Disposable
	private static FrameBuffer boundFrameBuffer; // TODO: bind to the default FB by default

	private final int ID = glGenFramebuffers();
	private final int width, height;

	private ArrayList<FrameBufferColorTexture>  colorAttachments;
	private FrameBufferDepthTexture 	        depthAttachment;

	public FrameBuffer() {
		this(1);
	}
	public FrameBuffer(int colorbuffers) {
		this(Viewport.getWidth(), Viewport.getHeight(), colorbuffers);
	}
	public FrameBuffer(int width, int height, int colorbuffers) { this(width, height, createFormatList(colorbuffers, PixelFormat.RGB8)); }
	public FrameBuffer(PixelFormat format) {
		this(Viewport.getWidth(), Viewport.getHeight(), format);
	}
	public FrameBuffer(PixelFormat[] formats) {	this(Viewport.getWidth(), Viewport.getHeight(), formats); }
	public FrameBuffer(int width, int height, PixelFormat format) {	this(width, height, new PixelFormat[] {format}); }
	public FrameBuffer(int width, int height, PixelFormat[] formats) {
		//TODO: Throw exception if formats is null or empty
		this.width = width;
		this.height = height;
		colorAttachments = new ArrayList<>(formats.length);
		bind();

		createColorAttachments(formats);
		bindRenderAttachment(colorAttachments.size());
	}

	public void createDepthAttachment() {
		bind();
		depthAttachment = new FrameBufferDepthTexture("FBO" + ID + "DepthAttachment0", width, height);
	}

	public FrameBufferColorTexture[] addColorAttachment(PixelFormat format) {
		return createColorAttachments(colorAttachments.size()+1, format);
	}
	public FrameBufferColorTexture[] createColorAttachments(int totalattachments) {
		return createColorAttachments(totalattachments, PixelFormat.RGB8);
	}
	public FrameBufferColorTexture[] createColorAttachments(int totalattachments, PixelFormat format) {
		PixelFormat[] formats = new PixelFormat[totalattachments];
		for (int i=0; i<totalattachments; i++)
			formats[i] = format;

		return createColorAttachments(formats);
	}
	public FrameBufferColorTexture[] createColorAttachments(PixelFormat[] formats) {
		FrameBufferColorTexture[] newattachments = new FrameBufferColorTexture[formats.length];

		if (formats.length == 0)
			return newattachments;

		for (int i=0; i<formats.length; i++)
			newattachments[i] = new FrameBufferColorTexture("FBO" + ID + "ColorAttachment" + i, width, height, i, formats[i]);

		addColorAttachments(newattachments);

		return newattachments;
	}

	public void addColorAttachments(FrameBufferColorTexture[] attachments) {
		for (FrameBufferColorTexture attachment : attachments)
			colorAttachments.add(attachment);
	}

	public FrameBufferColorTexture getColorTexture(int index) {
		if (index >= colorAttachments.size())
			throw new IndexOutOfBoundsException("Cannot access unbound FBO color attachment. Please create " + index + " color attachments or use a different attachment.");

		return colorAttachments.get(index);
	}
	public Texture getDepthTexture() {
		return depthAttachment;
	}

	public FrameBufferColorTexture[] getAllColorAttachments() {
		return colorAttachments.toArray(new FrameBufferColorTexture[colorAttachments.size()]);
	}

	public int getNumberOfColorAttachments() {
		return colorAttachments.size();
	}

	// Set which attachment to draw to
	private static void bindRenderAttachments(int count) {
		int[] renderbuffers = new int[count];
		for (int i=0; i<count; i++)
			renderbuffers[i] = GL_COLOR_ATTACHMENT0 + i;
		glDrawBuffers(DataConversion.toGLBuffer(renderbuffers)); // TODO: Maintain list as IntBuffer
	}
	private static void bindRenderAttachment(int index) {
		if (index >= 16)// TODO: Get max number of color attachments. http://stackoverflow.com/questions/29707968/get-maximum-number-of-framebuffer-color-attachments
			throw new IndexOutOfBoundsException("Cannot bind FBO color attachment " + index + ". FBOs only support 0-15 (16) color attachments.");

		glDrawBuffer(GL_COLOR_ATTACHMENT0 + index);
	}

	public void bind() {
		if (boundFrameBuffer != null && boundFrameBuffer.ID == this.ID)
			return;

		glBindFramebuffer(GL_FRAMEBUFFER, ID);
		if (colorAttachments != null)
			bindRenderAttachments(colorAttachments.size());

		boundFrameBuffer = this;

		Viewport.setDimensions(width, height);
	}
	public static void bindDefault() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		boundFrameBuffer = null;
	}

	private static final PixelFormat[] createFormatList(int count, PixelFormat format) {
		PixelFormat[] formats = new PixelFormat[count];
		for (int i=0; i<count; i++)
			formats[i] = format;
		return formats;
	}

	public void blitTo(FrameBuffer fbo, boolean color, boolean depth, boolean stencil) {
		blitTo(fbo, color, depth, stencil, TextureFilter.Nearest);
	}
	public void blitTo(FrameBuffer fbo, boolean color, boolean depth, boolean stencil, TextureFilter filter) {
		int mask = Renderer.getBufferBits(color, depth, stencil);
		blitTo(this, fbo, mask, filter);
	}

	public static void blitTo(FrameBuffer source, FrameBuffer destination, int bufferbits, TextureFilter filter) {
		glBindFramebuffer(GL_READ_FRAMEBUFFER, source.ID);
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, destination.ID);
		glBlitFramebuffer(
				0, 0, source.width, source.height,
				0, 0, destination.width, destination.height,
				bufferbits,
				filter.getGLEnum()
		);
	}

	public void dispose() {
		for (Texture colorattachment : colorAttachments)
			colorattachment.requestDisposal();
		if (depthAttachment != null)
			depthAttachment.requestDisposal();
		glDeleteFramebuffers(ID);
	}

	public static final FrameBuffer getCurrent() {
		return boundFrameBuffer;
	}

	// Reads the currently bound framebuffer's pixel's state
	// NOTE: Ensure that all required geometry has been drawn (if nessersary) with glFlush and glFinish
	public static final BufferedImage read(int startX, int startY, int width, int height) {
		int numpixels = width * height;
		int numbytes = 4 * numpixels;

		int boundframebufferheight = (boundFrameBuffer == null) ? Viewport.getHeight() : boundFrameBuffer.height;

		ByteBuffer pixeldata = ByteBuffer.allocateDirect(numbytes);
		glReadPixels(startX, boundframebufferheight-height-startY, width, height, PixelComponents.RGBA.getGLEnum(), DataType.UByte.getGLEnum(), pixeldata);
		pixeldata.rewind();

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int i = 0;
		for(int y=height-1; y>=0 && pixeldata.hasRemaining(); y--) {
			for (int x = 0; x < width && pixeldata.hasRemaining(); x++) {
				int blue = pixeldata.get();
				int green = pixeldata.get();
				int red = pixeldata.get();
				int alpha = pixeldata.get();

				int rgb =((alpha & 0xFF) << 24) |
						 ((blue  & 0xFF) << 16) |
						 ((green & 0xFF) << 8)  |
						 ((red   & 0xFF) << 0);
				image.setRGB(x, y, rgb);
			}
		}

		return image;
	}
	// Reads the currently bound framebuffer's pixel's state
	// NOTE: Ensure that all required geometry has been drawn (if nessersary) with glFlush and glFinish
	public static final Vector4f read(int x, int y) {
		FloatBuffer pixeldata = ByteBuffer.allocateDirect(4 * 4).asFloatBuffer();
		glReadPixels(x, y, 1, 1, PixelComponents.RGBA.getGLEnum(), DataType.Float.getGLEnum(), pixeldata);
		pixeldata.rewind();

		float blue = pixeldata.get();
		float green = pixeldata.get();
		float red = pixeldata.get();
		float alpha = pixeldata.get();
		Vector4f pixelcolor = new Vector4f(red, green, blue, alpha);
		return pixelcolor;
	}
}
