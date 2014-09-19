package aac.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.media.Buffer;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Manager;
import javax.media.Player;
import javax.media.PrefetchCompleteEvent;
import javax.media.RealizeCompleteEvent;
import javax.media.control.FrameGrabbingControl;
import javax.media.control.FramePositioningControl;
import javax.media.format.VideoFormat;
import javax.media.util.BufferToImage;

/**
 * Source: http://popscan.blogspot.com.br/2012/08/reading-and-processing-video-frames.html
 * @author Jussi
 */
public class VideoSource implements ControllerListener {
	public static final int NOT_READY = 1;
	public static final int READY = 2;
	public static final int ERROR = 3;
	Player _player;
	String _videoFilename;
	FramePositioningControl _framePositioningControl;
	FrameGrabbingControl _frameGrabbingControl;
	private int _state;

	public VideoSource(String videoFilename) {
		this(videoFilename, false);
	}

	// the filename must contain protocol,
	// for example file://c:\\test.avi
	public VideoSource(String videoFilename, boolean initialize) {
		_videoFilename = videoFilename;
		_state = NOT_READY;
		if (initialize) initialize();
	}

	/*
	 * Create Player object and start realizing it
	 */
	public void initialize() {
		try {
			_player = Manager.createPlayer(new URL(_videoFilename));
			_player.addControllerListener(this);
			// realize call will launch a chain of events,
			// see controllerUpdate()
			_player.realize();
		} catch (Exception e) {
			System.out.println("Could not create VideoSource!");
			e.printStackTrace();
			setState(ERROR);
			return;
		}
	}

	/*
	 * Returns the current state
	 */
	public int getState() {
		return _state;
	}

	/*
	 * Returns the number of frames for current video if the VideoSource is
	 * ready, in any other case returns -1.
	 */
	public int getFrameCount() {
		if (getState() != READY) {
			return -1;
		}
		return _framePositioningControl.mapTimeToFrame(_player.getDuration());
	}

	/*
	 * Returns the video frame from given index as BufferedImage. If VideoSource
	 * is not ready or index is out of bounds, returns null.
	 */
	public BufferedImage getFrame(int index) {
		if (getState() != READY || index < 0 || index > getFrameCount()) {
			return null;
		}
		_framePositioningControl.seek(index);
		Buffer buffer = _frameGrabbingControl.grabFrame();
		Image img = new BufferToImage((VideoFormat) buffer.getFormat())
				.createImage(buffer);
		// image creation may also fail!
		if (img != null) {
			BufferedImage bi = new BufferedImage(img.getWidth(null),
					img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bi.createGraphics();
			g.drawImage(img, 0, 0, null);
			return bi;
		}
		return null;
	}

	// callback for ControllerListener
	public void controllerUpdate(ControllerEvent event) {
		if (event instanceof RealizeCompleteEvent) {
			_player.prefetch();
		} else if (event instanceof PrefetchCompleteEvent) {
			// get controls
			_framePositioningControl = (FramePositioningControl) _player
					.getControl("javax.media.control.FramePositioningControl");
			if (_framePositioningControl == null) {
				System.out.println("Error: FramePositioningControl!");
				setState(ERROR);
				return;
			}
			_frameGrabbingControl = (FrameGrabbingControl) _player
					.getControl("javax.media.control.FrameGrabbingControl");
			if (_frameGrabbingControl == null) {
				System.out.println("Error: FrameGrabbingControl!");
				setState(ERROR);
				return;
			}
			setState(READY);
		}
	}

	// for setting the state internally
	private void setState(int nextState) {
		_state = nextState;
	}

}