package GLOOP.physics.data;

import GLOOP.general.math.Quaternion;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class AABB {
	private static final Vector3f[] ROTATED_VERTS = new Vector3f[8];
	private static final Matrix4f ROTATION_MATRIX = new Matrix4f();

	static {
		for (int i=0; i<ROTATED_VERTS.length; i++)
			ROTATED_VERTS[i] = new Vector3f();
	}

	public float x, y, z, width, height, depth;
	
	public AABB(Vector3f[] points) {
		create(points, this);
	}

	public static final void create(Vector3f[] points, AABB out) {
		float
			minx = Float.MAX_VALUE,
			maxx = Float.MIN_VALUE,
			miny = Float.MAX_VALUE,
			maxy = Float.MIN_VALUE,
			minz = Float.MAX_VALUE,
			maxz = Float.MIN_VALUE;
		for (Vector3f p : points) {
			if (p.x > maxx)
				maxx = p.x;
			if (p.x < minx)
				minx = p.x;

			if (p.y > maxy)
				maxy = p.y;
			if (p.y < miny)
				miny = p.y;

			if (p.z > maxz)
				maxz = p.z;
			if (p.z < minz)
				minz = p.z;
		}

		set(minx, miny, minz, maxx-minx, maxy-miny, maxz-minz, out);
	}

	public AABB(Vector3f position, Vector3f size) {
		this(position.x, position.y, position.z, size.x, size.y, size.z);
	}
	public AABB(float x, float y, float z, float width, float height, float depth) { set(x, y, z, width, height, depth, this);	}
	
	public static final void set(AABB out, Vector3f position, Vector3f size) { set(position.x, position.y, position.z, size.x, size.y, size.z, out); }
	public static final void set(float x, float y, float z, float width, float height, float depth,AABB out) {
		out.x = x;
		out.y = y;
		out.z = z;
		out.width = width;
		out.height = height;
		out.depth = depth;
	}

	public static final void createFromRotated(Quaternion rotation, AABB out) {
		out.getVertcies(ROTATED_VERTS);
		// Now rotate the verts

		for(int i=0; i<ROTATED_VERTS.length; i++)
			rotation.multiply(ROTATED_VERTS[i]);
		// Create BoundingBox of rotated verts
		create(ROTATED_VERTS, out);
	}

	public final void getVertcies(Vector3f[] out) {
		// TODO: Validate if correct length?
		out[0].set(x,y,z);//0,0,0
		out[1].set(x+width,y,z);//1,0,0
		out[2].set(x,y+height,z);//0,1,0
		out[3].set(x+width,y+height,z);//1,1,0
		out[4].set(x,y,z+height);//0,0,1
		out[5].set(x+width,y,z+depth);//1,0,1
		out[6].set(x,y+height,z+height);//0,1,1
		out[7].set(x+width,y+height,z+height);//1,1,1
	}
	
	public void translate(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void resize(float width, float height, float depth) {
		this.width = width;
		this.height = height;
		this.depth = depth;
	}

	public Vector3f getMax() { 
		return new Vector3f(x+width, y+height, z-depth);
	}
	
	public Vector3f getCentre() {
		return new Vector3f(x + (width / 2), y + (height / 2), z + (depth / 2));
	}

	public Vector3f getPosition() {
		return new Vector3f(x, y, z);
	}
	
	public Vector3f getSize() {
		return new Vector3f(width, height, depth);
	}
	
	public AABB expand(AABB box) {
		Vector3f
		boxmax = box.getMax(),
		thismax = getMax(),
		max = new Vector3f(Math.max(thismax.x, boxmax.x), Math.max(thismax.y, boxmax.y), Math.max(thismax.z, boxmax.z)),
		min = new Vector3f(Math.min(box.x, this.x), Math.min(box.y, this.y), Math.min(box.z, this.z)),
		size = new Vector3f(max.x-min.x, max.y-min.y, max.z-min.z);
		
		return new AABB(min, size);
	}

	public boolean intersects(AABB box) {
		Vector3f 
		thismax = getMax(),
		thatmax = box.getMax();
		if (box.x > thismax.x) return false;
		if (box.y > thismax.y) return false;
		if (box.z > thismax.z) return false;
		if (thatmax.x < x) return false;
		if (thatmax.y < y) return false;
		if (thatmax.z < z) return false;
		
		return true;
	}
	
	public AABB union(AABB box) {
		if (!intersects(box)) return null;
		
		Vector3f
		thismax = getMax(),
		boxmax = box.getMax(),
		base = new Vector3f(Math.max(box.x, this.x), Math.max(box.y, this.y), Math.max(box.z, this.z)),
		max = new Vector3f(Math.min(boxmax.x, thismax.x), Math.min(boxmax.y, thismax.y), Math.min(boxmax.z, thismax.z));
		
		return new AABB(new Vector3f(base.x, base.y, base.z), new Vector3f(max.x-base.x, max.y-base.y, max.z-base.z));
	}
	
	@Override
	public String toString() {
		return 	"X: " + x
			+	", Y: " + y
			+	", Z: " + z
			+	", Width: " + width
			+	", Height: " + height
			+	", Depth: " + depth;
	}
}