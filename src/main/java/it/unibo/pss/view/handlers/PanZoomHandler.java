package it.unibo.pss.view.handlers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.canvas.Canvas;
import javafx.util.Duration;
import it.unibo.pss.common.SharedConstants;

public class PanZoomHandler {
	private double scale = 1.0, targetScale = 1.0;
	private double panX = 0, panY = 0, velocityX = 0, velocityY = 0;
	private double lastMouseX, lastMouseY;
	private final Canvas target;
	private final Runnable updateCallback;
	private final Timeline inertiaTimer;

	public PanZoomHandler(Canvas target, Runnable updateCallback) {
		this.target = target;
		this.updateCallback = updateCallback;
		attachEventHandlers();
		inertiaTimer = new Timeline(new KeyFrame(Duration.millis(1000 / SharedConstants.CAMERA_FRAMERATE), e -> updateInertia()));
		inertiaTimer.setCycleCount(Timeline.INDEFINITE);
		inertiaTimer.play();
	}

	private void attachEventHandlers() {
		target.setOnScroll(this::handleScroll);
		target.setOnMousePressed(e -> {
			if (e.getButton() == MouseButton.MIDDLE) {
				lastMouseX = e.getX();
				lastMouseY = e.getY();
				velocityX = velocityY = 0;
			}
		});
		target.setOnMouseDragged(e -> {
			if (e.getButton() == MouseButton.MIDDLE) {
				double dx = (e.getX() - lastMouseX) / scale * SharedConstants.CAMERA_SENSITIVITY;
				double dy = (e.getY() - lastMouseY) / scale * SharedConstants.CAMERA_SENSITIVITY;
				panX += dx;
				panY += dy;
				velocityX = dx;
				velocityY = dy;
				lastMouseX = e.getX();
				lastMouseY = e.getY();
				updateCallback.run();
			}
		});
	}

	private void handleScroll(ScrollEvent e) {
		if (e.isControlDown()) {
			targetScale *= e.getDeltaY() > 0 ? SharedConstants.CAMERA_ZOOM_BASE : 1 / SharedConstants.CAMERA_ZOOM_BASE;
			targetScale = Math.max(SharedConstants.CAMERA_MIN_SCALE, Math.min(targetScale, SharedConstants.CAMERA_MAX_SCALE));
		} else {
			panX += e.getDeltaX() / scale * SharedConstants.CAMERA_SENSITIVITY;
			panY += e.getDeltaY() / scale * SharedConstants.CAMERA_SENSITIVITY;
		}
		updateCallback.run();
		e.consume();
	}

	private void updateInertia() {
		boolean updated = false;
		if (Math.abs(velocityX) > SharedConstants.CAMERA_INERTIA_THRESHOLD || Math.abs(velocityY) > SharedConstants.CAMERA_INERTIA_THRESHOLD) {
			panX += velocityX;
			panY += velocityY;
			velocityX *= SharedConstants.CAMERA_FRICTION;
			velocityY *= SharedConstants.CAMERA_FRICTION;
			updated = true;
		}
		if (Math.abs(targetScale - scale) > 0.001) {
			scale += (targetScale - scale) * SharedConstants.CAMERA_ZOOM_SMOOTHING;
			updated = true;
		}
		if (updated) updateCallback.run();
	}

	public Point2D applyCamera(Point2D baseOffset) {
		return new Point2D(baseOffset.getX() + panX, baseOffset.getY() + panY);
	}

	public double getScale() { return scale; }
	public double getPanX() { return panX; }
	public double getPanY() { return panY; }
}
