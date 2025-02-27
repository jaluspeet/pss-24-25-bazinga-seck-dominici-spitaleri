package it.unibo.pss.view.components;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.canvas.Canvas;
import javafx.util.Duration;
import it.unibo.pss.common.SharedConstants;

// camera (handles zoom and pan)
public class Camera {
	private double scale = 1.0;
	private double targetScale = 1.0;
	private double panX = 0, panY = 0;
	private double lastMouseX, lastMouseY;
	private double velocityX = 0, velocityY = 0;
	private double viewportWidth, viewportHeight;
	private final Canvas target;
	private final Runnable updateCallback;
	private final Timeline inertiaTimer;

	// constructor for Camera
	public Camera(Canvas target, Runnable updateCallback) {
		this.target = target;
		this.updateCallback = updateCallback;
		this.viewportWidth = target.getWidth();
		this.viewportHeight = target.getHeight();
		attachEventHandlers();
		inertiaTimer = new Timeline(new KeyFrame(Duration.millis(1000 / SharedConstants.CAMERA_FRAMERATE), e -> updateInertia()));
		inertiaTimer.setCycleCount(Timeline.INDEFINITE);
		inertiaTimer.play();
	}

	// attach event handlers
	private void attachEventHandlers() {
		target.setOnScroll(this::handleScroll);
		target.setOnMousePressed(this::handleMousePressed);
		target.setOnMouseDragged(this::handleMouseDragged);
	}

	// handle scroll event
	private void handleScroll(ScrollEvent event) {
		if (event.isControlDown()) {
			double zoomFactor = event.getDeltaY() > 0 ? SharedConstants.CAMERA_ZOOM_BASE : 1 / SharedConstants.CAMERA_ZOOM_BASE;
			targetScale *= zoomFactor;
			if (targetScale < SharedConstants.CAMERA_MIN_SCALE) {
				targetScale = SharedConstants.CAMERA_MIN_SCALE;
			} else if (targetScale > SharedConstants.CAMERA_MAX_SCALE) {
				targetScale = SharedConstants.CAMERA_MAX_SCALE;
			}
			updateCallback.run();
		} else {
			double deltaX = event.getDeltaX() / scale * SharedConstants.CAMERA_SENSITIVITY;
			double deltaY = event.getDeltaY() / scale * SharedConstants.CAMERA_SENSITIVITY;
			panX += deltaX;
			panY += deltaY;
			updateCallback.run();
		}
		event.consume();
	}

	// handle mouse pressed event
	private void handleMousePressed(MouseEvent event) {
		if (event.getButton() == MouseButton.MIDDLE) {
			lastMouseX = event.getX();
			lastMouseY = event.getY();
			velocityX = 0;
			velocityY = 0;
		}
	}

	// handle mouse dragged event
	private void handleMouseDragged(MouseEvent event) {
		if (event.getButton() == MouseButton.MIDDLE) {
			double dx = event.getX() - lastMouseX;
			double dy = event.getY() - lastMouseY;
			double worldDeltaX = (dx / scale) * SharedConstants.CAMERA_SENSITIVITY;
			double worldDeltaY = (dy / scale) * SharedConstants.CAMERA_SENSITIVITY;
			panX += worldDeltaX;
			panY += worldDeltaY;
			velocityX = worldDeltaX;
			velocityY = worldDeltaY;
			lastMouseX = event.getX();
			lastMouseY = event.getY();
			updateCallback.run();
		}
	}

	// update inertia for smooth camera movement
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
		if (updated) {
			updateCallback.run();
		}
	}

	// apply camera transformation
	public Point2D applyCamera(Point2D baseOffset) {
		return new Point2D(baseOffset.getX() + panX, baseOffset.getY() + panY);
	}


	// getter for scale
	public double getScale() {
		return scale;
	}

	// getter for horizontal pan
	public double getPanX() {
		return panX;
	}

	// getter for vertical pan
	public double getPanY() {
		return panY;
	}

	// setter for viewport size
	public void setViewportSize(double width, double height) {
		this.viewportWidth = width;
		this.viewportHeight = height;
	}

	// getter for viewport width
	public double getViewportWidth() {
		return viewportWidth;
	}

	// getter for viewport height
	public double getViewportHeight() {
		return viewportHeight;
	}
}
