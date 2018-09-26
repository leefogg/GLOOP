package GLOOP.general;
import java.util.Timer;
import java.util.TimerTask;

public class FrameCounter extends TimerTask {
	// TODO: Make atomic
	public volatile int fps = 0;
	private volatile int fpstemp = 0;

	public void start() {
		new Timer().scheduleAtFixedRate(this, 0, 1000);
	}

	public void newFrame() {fpstemp++;}

	@Override
	public void run() {
		synchronized(this){
			fps = fpstemp;
			fpstemp = 0;
		}
	}
}