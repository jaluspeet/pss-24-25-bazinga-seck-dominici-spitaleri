package it.unibo.pss.view.renderer;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

/** Isometric geometry calculations. */
public class IsometricRenderer implements GeometryRenderer {
	private static final double TILE_WIDTH = 64;      // example constant
	private static final double TILE_HEIGHT = 32;     // example constant
	private static final double IMAGE_WIDTH = 64;     // for rendering tiles
	private static final double IMAGE_HEIGHT = 64;
	private static final double FOOTPRINT_HEIGHT = 32; // adjustment
	
	private Point2D getIsometricCoordinates(int gridX, int gridY, double offsetX, double offsetY) {
		double isoX = (gridX - gridY) * (TILE_WIDTH / 2.0) + offsetX;
		double isoY = (gridX + gridY) * (TILE_HEIGHT / 2.0) + offsetY - TILE_HEIGHT;
		return new Point2D(isoX, isoY);
	}
	
	@Override
	public Rectangle2D computeTileRect(int gridX, int gridY, double offsetX, double offsetY, double scale) {
		Point2D isoCoords = getIsometricCoordinates(gridX, gridY, offsetX, offsetY);
		double screenX = isoCoords.getX() * scale;
		double screenY = isoCoords.getY() * scale;
		double left = screenX - (IMAGE_WIDTH * scale) / 2;
		double top = screenY - (IMAGE_HEIGHT * scale) + (FOOTPRINT_HEIGHT * scale);
		double width = IMAGE_WIDTH * scale;
		double height = IMAGE_HEIGHT * scale;
		return new Rectangle2D(left, top, width, height);
	}

	@Override
	public Point2D computeCenterOffset(double canvasWidth, double canvasHeight, int gridCols, int gridRows) {
		double centerTileX = (gridCols - 1) / 2.0;
		double centerTileY = (gridRows - 1) / 2.0;
		double centerIsoX = (centerTileX - centerTileY) * (TILE_WIDTH / 2.0);
		double centerIsoY = (centerTileX + centerTileY) * (TILE_HEIGHT / 2.0);
		double offsetX = canvasWidth / 2.0 - centerIsoX;
		double offsetY = canvasHeight / 2.0 - centerIsoY;
		return new Point2D(offsetX, offsetY);
	}
}
