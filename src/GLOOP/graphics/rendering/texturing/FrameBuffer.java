package GLOOP.graphics.rendering.texturing;

import GLOOP.resources.Disposable;
import GLOOP.graphics.data.DataConversion;
import GLOOP.graphics.data.DataType;
import GLOOP.graphics.rendering.Renderer;
import GLOOP.graphics.rendering.Viewport;
import GLOOP.resources.ResourceManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.vector.Vector4f;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.glDrawBuffer;
import static org.lwjgl.opengl.GL11.glReadPixels;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.*;

public class FrameBuffer implements Disposable {
	static final DefaultFrameBuffer DEFAULT_FRAME_BUFFER = new DefaultFrameBuffer();
	private static final int MAX_COLOR_ATTACHMENTS = GL11.glGetInteger(GL30.GL_MAX_COLOR_ATTACHMENTS);
	private static final String MAX_COLOR_ATTACHMENTS_ERROR =  "FBOs only support 0-"+(MAX_COLOR_ATTACHMENTS-1)+" ("+MAX_COLOR_ATTACHMENTS+") color attachments.";
	private static final IntBuffer ALL_ATTCHMENTS;

	static {
		final int[] attachmentenums = new int[MAX_COLOR_ATTACHMENTS];
		for (int i=0; i<MAX_COLOR_ATTACHMENTS; i++)
			attachmentenums[i] = GL_COLOR_ATTACHMENT0 + i;
		ALL_ATTCHMENTS = DataConversion.toGLBuffer(attachmentenums);
	}

	// TODO: Find a way to make these final
	protected int ID;
	protected int width, height;

	protected ArrayList<FrameBufferColorTexture>   colorAttachments;
	protected FrameBufferDepthTexture 	            depthAttachment;
	protected FrameBufferDepthStencilTexture       depthstencilAttachment;
	private IntBuffer boundColorAttachments;
	private boolean disposed;

	protected FrameBuffer() {}
	public FrameBuffer(int colorbuffers) {
		this(Viewport.getWidth(), Viewport.getHeight(), colorbuffers);
	}
	public FrameBuffer(int width, int height, int colorbuffers) { this(width, height, createFormatList(colorbuffers, PixelFormat.RGB8), true, false); }
	public FrameBuffer(PixelFormat format) {
		this(Viewport.getWidth(), Viewport.getHeight(), format);
	}
	public FrameBuffer(PixelFormat[] formats) {	this(Viewport.getWidth(), Viewport.getHeight(), formats, true, false); }
	public FrameBuffer(int width, int height, PixelFormat format) {	this(width, height, new PixelFormat[] {format}, true, false); }
	public FrameBuffer(int width, int height, PixelFormat[] formats, boolean hasdepth, boolean hasstencil) {
		if (formats == null || formats.length == 0)
			throw new IllegalArgumentException("Formats parameter may not be null or empty");

		this.ID = glGenFramebuffers();
		this.width = width;
		this.height = height;
		colorAttachments = new ArrayList<>(formats.length);
		setCurrent(this);

		addColorAttachments(formats);
		enableRenderAttachments(colorAttachments.size());
		addDepthStencilAttachments(hasdepth, hasstencil);
		checkErrors();
	}

	private void checkErrors() {
		int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
		if (status == GL_FRAMEBUFFER_COMPLETE)
			return;

		String errorinfo;
		switch (status) {
			case GL_FRAMEBUFFER_UNDEFINED:
				errorinfo = "The framebuffer attachment points are framebuffer incomplete";
				break;
			case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
				errorinfo = "A framebuffer attachment is incomplete";
				break;
			case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
				errorinfo = "The framebuffer does not have at least one image attached to it";
				break;
			case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
				errorinfo = "The value of GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE is GL_NONE for a color attachment point named by GL_DRAW_BUFFERi";
				break;
			case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
				errorinfo = "GL_READ_BUFFER is not GL_NONE and the value of GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE is GL_NONE for the color attachment point named by GL_READ_BUFFER";
				break;
			case GL_FRAMEBUFFER_UNSUPPORTED:
				errorinfo = "The combination of internal formats of the attached images violates an implementation-dependent set of restrictions";
				break;
			case GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE:
				errorinfo = "GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE - Refer to Doc";
				break;
			case GL32.GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS:
				errorinfo = "GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS";
				break;
			default: errorinfo = "Unknown FBO Issue."; break;
		}

		//TODO: Throw on error with error code
		System.err.println(errorinfo);
		Renderer.checkErrors();
	}

	private void addDepthStencilAttachments(boolean hasdepth, boolean hasstencil) {
		if (hasdepth) {
			if (hasstencil) {
				createDepthStencilAttachment();
			} else {
				createDepthAttachment(); // TODO: Test
			}
		}
	}

	protected void createDepthAttachment() {
		setCurrent(this);
		depthAttachment = new FrameBufferDepthTexture("FBO" + ID + "DepthAttachment", width, height);
	}
	protected void createDepthStencilAttachment() {
		setCurrent(this);
		depthstencilAttachment = new FrameBufferDepthStencilTexture("FBO" + ID + "DepthStencilAttachment", width, height);
	}

	public FrameBufferColorTexture[] addColorAttachment(PixelFormat format) {
		return addColorAttachments(1, format);
	}
	public FrameBufferColorTexture[] addColorAttachments(int totalattachments) {
		return addColorAttachments(totalattachments, PixelFormat.RGB8);
	}
	public FrameBufferColorTexture[] addColorAttachments(int totalattachments, PixelFormat format) {
		PixelFormat[] formats = new PixelFormat[totalattachments];
		for (int i=0; i<totalattachments; i++)
			formats[i] = format;

		return addColorAttachments(formats);
	}
	public FrameBufferColorTexture[] addColorAttachments(PixelFormat[] formats) {
		setCurrent(this);
		FrameBufferColorTexture[] newattachments = new FrameBufferColorTexture[formats.length];

		if (formats.length == 0)
			return newattachments;

		for (int i=0; i<formats.length; i++)
			newattachments[i] = new FrameBufferColorTexture("FBO" + ID + "ColorAttachment" + (colorAttachments.size() + i), width, height, (colorAttachments.size() + i), formats[i]);

		addColorAttachments(newattachments);

		FrameBufferManager.register(this);

		return newattachments;
	}

	private void addColorAttachments(FrameBufferColorTexture[] attachments) {
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
	public Texture getStencilTexture() { return depthstencilAttachment; }

	public FrameBufferColorTexture[] getAllColorAttachments() {
		return colorAttachments.toArray(new FrameBufferColorTexture[colorAttachments.size()]);
	}

	public int getNumberOfColorAttachments() {
		return colorAttachments.size();
	}

	public void bind() {
		setCurrent(this);

		FrameBufferManager.setCurrentFrameBuffer(this);
	}

	private static final void setCurrent(FrameBuffer buffer) {
		FrameBuffer boundframebuffer = FrameBufferManager.getCurrentFrameBuffer();
		if (boundframebuffer != null && boundframebuffer.ID == buffer.ID)
			return;

		glBindFramebuffer(GL_FRAMEBUFFER, buffer.ID);
		Viewport.setDimensions(buffer.width, buffer.height);
	}

	public void enableAllRenderAttachments() {
		enableRenderAttachments(ALL_ATTCHMENTS);
	}
	// Set which attachment to draw to
	public void enableRenderAttachments(int count) {
		int[] renderbuffers = new int[count];
		for (int i=0; i<count; i++)
			renderbuffers[i] = GL_COLOR_ATTACHMENT0 + i;

		enableRenderAttachments(renderbuffers);
	}
	public void enableRenderAttachments(int[] attachments) {
		enableRenderAttachments(DataConversion.toGLBuffer(attachments));
	}
	public void enableRenderAttachments(IntBuffer attachments) {
		bind();
		boundColorAttachments = attachments;
		glDrawBuffers(attachments);
	}
	private void enableRenderAttachment(int index) {
		bind();
		if (index >= MAX_COLOR_ATTACHMENTS)
			throw new IndexOutOfBoundsException("Cannot bind FBO color attachment " + index + "." + MAX_COLOR_ATTACHMENTS_ERROR);

		glDrawBuffer(GL_COLOR_ATTACHMENT0 + index);
	}

	public IntBuffer getEnabledColorAttachments() { return boundColorAttachments; }

	public static void bindDefault() {
		DEFAULT_FRAME_BUFFER.bind();
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

	@Override
	public void requestDisposal() {
		ResourceManager.queueDisposal(this);
	}

	@Override
	public boolean isDisposed() { return disposed; }

	@Override
	public void dispose() {
		for (Texture colorattachment : colorAttachments)
			colorattachment.requestDisposal();
		if (depthAttachment != null)
			depthAttachment.requestDisposal();
		glDeleteFramebuffers(ID);
		disposed = true;
	}

	public static final FrameBuffer getCurrent() { return FrameBufferManager.getCurrentFrameBuffer(); }

	// Reads the currently bound framebuffer's pixel's state
	// NOTE: Ensure that all required geometry has been drawn (if nessersary) with glFlush and glFinish
	public static final BufferedImage read(int startX, int startY, int width, int height) {
		int numpixels = width * height;
		int numbytes = 4 * numpixels;

		FrameBuffer boundframebuffer = getCurrent();
		int boundframebufferheight = (boundframebuffer == null) ? Viewport.getHeight() : boundframebuffer.height;

		ByteBuffer pixeldata = ByteBuffer.allocateDirect(numbytes);
		glReadPixels(startX, boundframebufferheight-height-startY, width, height, PixelComponents.RGBA.getGLEnum(), DataType.UByte.getGLEnum(), pixeldata);
		pixeldata.rewind();

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
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
