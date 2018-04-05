package engine.graphics.cameras;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public final class DebugCamera extends PerspectiveCamera {
	public static float walkingSpeed = 0.04f;
	public static float mouseSpeed = 0.2f;
	public static final int maxLookUp = 85;
	public static final int maxLookDown = -85;
	private Vector3f Velocity = new Vector3f();

	public DebugCamera() {
		super();
	}
	public DebugCamera(int width, int height, float fov, float znear, float zfar) {
		super(width, height, fov, znear, zfar);
	}


	@Override
	public void update(float delta, float timescaler) {
		if (Mouse.isGrabbed()) {
			Vector2f mousedirection = new Vector2f(Mouse.getDX(), Mouse.getDY());
			mousedirection.scale(mouseSpeed);

            if (Rotation.y + mousedirection.x >= 360) {
                Rotation.y = Rotation.y + mousedirection.x - 360;
            } else if (Rotation.y + mousedirection.x < 0) {
                Rotation.y = 360 - Rotation.y + mousedirection.x;
            } else {
                Rotation.y += mousedirection.x;
            }
            if (Rotation.x - mousedirection.y >= maxLookDown && Rotation.x - mousedirection.y <= maxLookUp) {
                Rotation.x += -mousedirection.y;
            } else if (Rotation.x - mousedirection.y < maxLookDown) {
                Rotation.x = maxLookDown;
            } else if (Rotation.x - mousedirection.y > maxLookUp) {
                Rotation.x = maxLookUp;
            }
        }

        boolean forward = Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_W);
        boolean backward = Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_S);
        boolean left = Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A);
        boolean right = Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D);
        boolean up = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
        boolean down = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
        boolean movefaster = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
        boolean moveslower = Keyboard.isKeyDown(Keyboard.KEY_TAB);

        float walkingspeed = walkingSpeed;
        if (movefaster && !moveslower) {
	        walkingspeed *= 4f;
        }
        if (moveslower && !movefaster) {
	        walkingspeed /= 4f;
        }

        Vector3f additionalvelcity = new Vector3f();
        if (forward && right && !left && !backward) {
            float angle = Rotation.y + 45;
            float oblique = walkingspeed * timescaler;
            float adjacent = oblique * (float)Math.cos(Math.toRadians(angle));
            float opposite = (float)(Math.sin(Math.toRadians(angle)) * oblique);
	        additionalvelcity.z -= adjacent;
	        additionalvelcity.x += opposite;
        }
        if (forward && left && !right && !backward) {
            float angle = Rotation.y - 45;
            float oblique = walkingspeed * timescaler;
            float adjacent = oblique * (float)Math.cos(Math.toRadians(angle));
            float opposite = (float)(Math.sin(Math.toRadians(angle)) * oblique);
	        additionalvelcity.z -= adjacent;
	        additionalvelcity.x += opposite;
        }
        if (forward && !left && !right && !backward) {
            float angle = Rotation.y;
            float oblique = walkingspeed * timescaler;
            float adjacent = oblique * (float)Math.cos(Math.toRadians(angle));
            float opposite = (float)(Math.sin(Math.toRadians(angle)) * oblique);
	        additionalvelcity.x += opposite;
	        additionalvelcity.z -= adjacent;
        }
        if (backward && left && !right && !forward) {
            float angle = Rotation.y - 135;
            float oblique = walkingspeed * timescaler;
            float adjacent = oblique * (float)Math.cos(Math.toRadians(angle));
            float opposite = (float)(Math.sin(Math.toRadians(angle)) * oblique);
	        additionalvelcity.z -= adjacent;
	        additionalvelcity.x += opposite;
        }
        if (backward && right && !left && !forward) {
            float angle = Rotation.y + 135;
            float oblique = walkingspeed * timescaler;
            float adjacent = oblique * (float)Math.cos(Math.toRadians(angle));
            float opposite = (float)(Math.sin(Math.toRadians(angle)) * oblique);
	        additionalvelcity.z -= adjacent;
	        additionalvelcity.x += opposite;
        }
        if (backward && !forward && !left && !right) {
            float angle = Rotation.y;
            float oblique = -walkingspeed * timescaler;
            float adjacent = oblique * (float)Math.cos(Math.toRadians(angle));
            float opposite = (float)(Math.sin(Math.toRadians(angle)) * oblique);
	        additionalvelcity.z -= adjacent;
	        additionalvelcity.x += opposite;
        }
        if (left && !right && !forward && !backward) {
            float angle = Rotation.y - 90;
            float oblique = walkingspeed * timescaler;
            float adjacent = oblique * (float)Math.cos(Math.toRadians(angle));
            float opposite = (float)(Math.sin(Math.toRadians(angle)) * oblique);
	        additionalvelcity.z -= adjacent;
	        additionalvelcity.x += opposite;
        }
        if (right && !left && !forward && !backward) {
            float angle = Rotation.y + 90;
            float oblique = walkingspeed * timescaler;
            float adjacent = oblique * (float)Math.cos(Math.toRadians(angle));
            float opposite = (float)(Math.sin(Math.toRadians(angle)) * oblique);
	        additionalvelcity.z -= adjacent;
	        additionalvelcity.x += opposite;
        }
        if (up && !down) {
            double newPositionY = walkingspeed * timescaler;
	        additionalvelcity.y += newPositionY;
        }
        if (down && !up) {
            double newPositionY = walkingspeed * timescaler;
	        additionalvelcity.y -= newPositionY;
        }

        Velocity.x += additionalvelcity.x;
        Velocity.y += additionalvelcity.y;
        Velocity.z += additionalvelcity.z;
        Position.x += Velocity.x;
        Position.y += Velocity.y;
        Position.z += Velocity.z;
        Velocity.scale(0.8f);

        if (Mouse.isButtonDown(0))
            Mouse.setGrabbed(true);
        if (Mouse.isButtonDown(1))
            Mouse.setGrabbed(false);

        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
            Mouse.setGrabbed(false);

        if (Keyboard.isKeyDown(Keyboard.KEY_P))
            System.out.println("X: " + Position.x + "\tY: " + Position.y + "\tZ: " + Position.z);
        if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
            Position.set(0.1f, 0.1f, 0.1f);
            Velocity.set(0,0,0);
        }

        viewMatrixIsDirty = true;
	}
}
