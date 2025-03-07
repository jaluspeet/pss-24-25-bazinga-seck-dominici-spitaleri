package it.unibo.pss.view.handlers;

import it.unibo.pss.view.geometry.GeometryRenderer;
import javafx.geometry.Point2D;

public final class CameraOffsetHandler {
	private CameraOffsetHandler() { }

	public static Point2D computeCameraOffset(GeometryRenderer renderer, PanZoomHandler camera, double canvasWidth, double canvasHeight, int gridCols, int gridRows) {
		Point2D baseOffset = renderer.computeCenterOffset(canvasWidth, canvasHeight, gridCols, gridRows, camera.getScale());
		return camera.applyCamera(baseOffset);
	}
}
