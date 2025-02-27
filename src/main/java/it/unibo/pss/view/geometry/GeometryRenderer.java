package it.unibo.pss.view.geometry;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

// interface for world grid geometry handlers
public interface GeometryRenderer {

	// compute the tile rectangle
	Rectangle2D computeTileRect(int gridX, int gridY, double offsetX, double offsetY, double scale);
	
	// compute the camera offset
	Point2D computeCenterOffset(double canvasWidth, double canvasHeight, int gridCols, int gridRows);
}
