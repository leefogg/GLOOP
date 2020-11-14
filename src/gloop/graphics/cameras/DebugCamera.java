package gloop.graphics.cameras;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public final class DebugCamera extends PerspectiveCamera {
	public static float WalkingSpeed = 0.04f;
	public static float MouseSpeed = 0.2f;
	public static final int MAX_LOOK_UP = 85;
	public static final int MAX_LOOK_DOWN = -85;
	private final Vector3f velocity = new Vector3f();

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
			mousedirection.scale(MouseSpeed);

            if (rotation.y + mousedirection.x >= 360) {
                rotation.y = rotation.y + mousedirection.x - 360;
            } else if (rotation.y + mousedirection.x < 0) {
                rotation.y = 360 - rotation.y + mousedirection.x;
            } else {
                rotation.y += mousedirection.x;
            }
            if (rotation.x - mousedirection.y >= MAX_LOOK_DOWN && rotation.x - mousedirection.y <= MAX_LOOK_UP) {
                rotation.x -= mousedirection.y;
            } else if (rotation.x - mousedirection.y < MAX_LOOK_DOWN) {
                rotation.x = MAX_LOOK_DOWN;
            } else if (rotation.x - mousedirection.y > MAX_LOOK_UP) {
                rotation.x = MAX_LOOK_UP;
            }
        }

        boolean forward = Keyboard.isKeyDown(Keyboard.KEY_W);
        boolean backward =  Keyboard.isKeyDown(Keyboard.KEY_S);
        boolean left =  Keyboard.isKeyDown(Keyboard.KEY_A);
        boolean right = Keyboard.isKeyDown(Keyboard.KEY_D);
        boolean up = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
        boolean down = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
        boolean movefaster = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
        boolean moveslower = Keyboard.isKeyDown(Keyboard.KEY_TAB);

        float walkingspeed = WalkingSpeed;
        if (movefaster && !moveslower) {
	        walkingspeed *= 4f;
        }
        if (moveslower && !movefaster) {
	        walkingspeed /= 4f;
        }

        Vector3f additionalvelcity = new Vector3f();
        if (forward && right && !left && !backward) {
            float angle = rotation.y + 45;
            float oblique = walkingspeed * timescaler;
            float adjacent = oblique * (float)Math.cos(Math.toRadians(angle));
            float opposite = (float)(Math.sin(Math.toRadians(angle)) * oblique);
	        additionalvelcity.z -= adjacent;
	        additionalvelcity.x += opposite;
        }
        if (forward && left && !right && !backward) {
            float angle = rotation.y - 45;
            float oblique = walkingspeed * timescaler;
            float adjacent = oblique * (float)Math.cos(Math.toRadians(angle));
            float opposite = (float)(Math.sin(Math.toRadians(angle)) * oblique);
	        additionalvelcity.z -= adjacent;
	        additionalvelcity.x += opposite;
        }
        if (forward && !left && !right && !backward) {
            float angle = rotation.y;
            float oblique = walkingspeed * timescaler;
            float adjacent = oblique * (float)Math.cos(Math.toRadians(angle));
            float opposite = (float)(Math.sin(Math.toRadians(angle)) * oblique);
	        additionalvelcity.x += opposite;
	        additionalvelcity.z -= adjacent;
        }
        if (backward && left && !right && !forward) {
            float angle = rotation.y - 135;
            float oblique = walkingspeed * timescaler;
            float adjacent = oblique * (float)Math.cos(Math.toRadians(angle));
            float opposite = (float)(Math.sin(Math.toRadians(angle)) * oblique);
	        additionalvelcity.z -= adjacent;
	        additionalvelcity.x += opposite;
        }
        if (backward && right && !left && !forward) {
            float angle = rotation.y + 135;
            float oblique = walkingspeed * timescaler;
            float adjacent = oblique * (float)Math.cos(Math.toRadians(angle));
            float opposite = (float)(Math.sin(Math.toRadians(angle)) * oblique);
	        additionalvelcity.z -= adjacent;
	        additionalvelcity.x += opposite;
        }
        if (backward && !forward && !left && !right) {
            float angle = rotation.y;
            float oblique = -walkingspeed * timescaler;
            float adjacent = oblique * (float)Math.cos(Math.toRadians(angle));
            float opposite = (float)(Math.sin(Math.toRadians(angle)) * oblique);
	        additionalvelcity.z -= adjacent;
	        additionalvelcity.x += opposite;
        }
        if (left && !right && !forward && !backward) {
            float angle = rotation.y - 90;
            float oblique = walkingspeed * timescaler;
            float adjacent = oblique * (float)Math.cos(Math.toRadians(angle));
            float opposite = (float)(Math.sin(Math.toRadians(angle)) * oblique);
	        additionalvelcity.z -= adjacent;
	        additionalvelcity.x += opposite;
        }
        if (right && !left && !forward && !backward) {
            float angle = rotation.y + 90;
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

        velocity.x += additionalvelcity.x;
        velocity.y += additionalvelcity.y;
        velocity.z += additionalvelcity.z;
        position.x += velocity.x;
        position.y += velocity.y;
        position.z += velocity.z;
        velocity.scale(0.8f);

        if (Mouse.isButtonDown(0))
            Mouse.setGrabbed(true);
        if (Mouse.isButtonDown(1))
            Mouse.setGrabbed(false);

        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
            Mouse.setGrabbed(false);

        if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
	        System.out.println("X: " + position.x + "\tY: " + position.y + "\tZ: " + position.z);
	        System.out.println("Pitch: " + rotation.x + " Yaw: " + rotation.y + " Roll: " + rotation.z);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
            position.set(0.1f, 0.1f, 0.1f);
            velocity.set(0,0,0);
        }

		viewMatrix.expire();
	}
}
