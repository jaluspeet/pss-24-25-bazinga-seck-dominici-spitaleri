package it.unibo.pss.view.geometry;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import it.unibo.pss.common.SharedConstants;

// render the world grid in an isometric perspective
public class IsometricRenderer implements GeometryRenderer {
	
	// helper method to compute the isometric coordinates from the grid coordinates
	private Point2D getIsometricCoordinates(int gridX, int gridY, double offsetX, double offsetY) {
		double isoX = (gridX - gridY) * (SharedConstants.TILE_WIDTH / 2.0) + offsetX;
		double isoY = (gridX + gridY) * (SharedConstants.TILE_HEIGHT / 2.0) + offsetY - SharedConstants.TILE_HEIGHT;
		return new Point2D(isoX, isoY);
	}
	
	// compute the tile rectangle
	@Override
	public Rectangle2D computeTileRect(int gridX, int gridY, double offsetX, double offsetY, double scale) {
		Point2D isoCoords = getIsometricCoordinates(gridX, gridY, offsetX, offsetY);
		double screenX = isoCoords.getX() * scale;
		double screenY = isoCoords.getY() * scale;
		double left = screenX - (SharedConstants.SPRITE_WIDTH * scale) / 2;
		double top = screenY - (SharedConstants.SPRITE_HEIGHT * scale) + (SharedConstants.FOOTPRINT_HEIGHT * scale);
		double width = SharedConstants.SPRITE_WIDTH * scale;
		double height = SharedConstants.SPRITE_HEIGHT * scale;
		return new Rectangle2D(left, top, width, height);
	}

	// compute the camera offset
	@Override
	public Point2D computeCenterOffset(double canvasWidth, double canvasHeight, int gridCols, int gridRows) {
		double centerTileX = (gridCols - 1) / 2.0;
		double centerTileY = (gridRows - 1) / 2.0;
		double centerIsoX = (centerTileX - centerTileY) * (SharedConstants.TILE_WIDTH / 2.0);
		double centerIsoY = (centerTileX + centerTileY) * (SharedConstants.TILE_HEIGHT / 2.0);
		double offsetX = canvasWidth / 2.0 - centerIsoX;
		double offsetY = canvasHeight / 2.0 - centerIsoY;
		return new Point2D(offsetX, offsetY);
	}
}
