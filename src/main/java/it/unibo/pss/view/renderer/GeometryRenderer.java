package it.unibo.pss.view.renderer;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

/** Interface for geometry calculations. */
public interface GeometryRenderer {
	/** Computes the drawing rectangle for a tile at (gridX,gridY). */
	Rectangle2D computeTileRect(int gridX, int gridY, double offsetX, double offsetY, double scale);
	
	/** Computes the center offset to center the grid on the canvas. */
	Point2D computeCenterOffset(double canvasWidth, double canvasHeight, int gridCols, int gridRows);
}
