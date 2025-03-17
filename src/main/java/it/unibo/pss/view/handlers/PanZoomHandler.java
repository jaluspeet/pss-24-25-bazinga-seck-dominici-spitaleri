package it.unibo.pss.view.handlers;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.canvas.Canvas;
import it.unibo.pss.common.SharedConstants;

/**
 *  Utility class to handle the pan and zoom of the canvas.
 */
public class PanZoomHandler {
	private double scale = 1.0, panX = 0, panY = 0;
	private double lastMouseX, lastMouseY;
	private final Canvas target;
	private final Runnable updateCallback;

	/**
	 * Constructor.
	 * @param target the canvas to apply the pan and zoom to.
	 * @param updateCallback the callback to call when the pan and zoom are updated.
	 */
	public PanZoomHandler(Canvas target, Runnable updateCallback) {
		this.target = target;
		this.updateCallback = updateCallback;
		attachEventHandlers();
	}

	/**
	 * Attach the event handlers to the canvas.
	 */
	private void attachEventHandlers() {
		target.setOnScroll(this::handleScrollAndZoom);
		target.setOnMousePressed(e -> {
			if (e.getButton() == MouseButton.MIDDLE) {
				lastMouseX = e.getX();
				lastMouseY = e.getY();
			}
		});
		target.setOnMouseDragged(e -> {
			if (e.getButton() == MouseButton.MIDDLE) {
				panX += (e.getX() - lastMouseX) / scale * SharedConstants.CAMERA_SENSITIVITY;
				panY += (e.getY() - lastMouseY) / scale * SharedConstants.CAMERA_SENSITIVITY;
				lastMouseX = e.getX();
				lastMouseY = e.getY();
				updateCallback.run();
			}
		});
	}


	/**
	 * Handle the scroll event to zoom and pan the canvas.
	 * @param e the scroll event.
	 */
	private void handleScrollAndZoom(ScrollEvent e) {
		
		// zooming
		if (e.isControlDown()) { 
			double zoomFactor = e.getDeltaY() > 0 ? SharedConstants.CAMERA_ZOOM_BASE : 1 / SharedConstants.CAMERA_ZOOM_BASE;
			scale = Math.max(SharedConstants.CAMERA_MIN_SCALE, Math.min(scale * zoomFactor, SharedConstants.CAMERA_MAX_SCALE));
		
		// panning
		} else {
			panX += e.getDeltaX() / scale * SharedConstants.CAMERA_SENSITIVITY;
			panY += e.getDeltaY() / scale * SharedConstants.CAMERA_SENSITIVITY;
		}
		
		updateCallback.run();
		e.consume();
	}

	/**
	 * Apply the camera transformation to the given point.
	 * @param baseOffset the point to apply the camera transformation to.
	 * @return the transformed point.
	 */
	public Point2D applyCamera(Point2D baseOffset) {
		return new Point2D(baseOffset.getX() + panX, baseOffset.getY() + panY);
	}

	/**
	 * Getters.
	 */
	public double getScale() { return scale; }
	public double getPanX() { return panX; }
	public double getPanY() { return panY; }
}
