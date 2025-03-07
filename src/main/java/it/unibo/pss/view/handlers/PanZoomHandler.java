package it.unibo.pss.view.handlers;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.canvas.Canvas;
import it.unibo.pss.common.SharedConstants;

public class PanZoomHandler {
	private double scale = 1.0, panX = 0, panY = 0;
	private double lastMouseX, lastMouseY;
	private final Canvas target;
	private final Runnable updateCallback;

	public PanZoomHandler(Canvas target, Runnable updateCallback) {
		this.target = target;
		this.updateCallback = updateCallback;
		attachEventHandlers();
	}

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

	public Point2D applyCamera(Point2D baseOffset) {
		return new Point2D(baseOffset.getX() + panX, baseOffset.getY() + panY);
	}

	public double getScale() { return scale; }
	public double getPanX() { return panX; }
	public double getPanY() { return panY; }
}
