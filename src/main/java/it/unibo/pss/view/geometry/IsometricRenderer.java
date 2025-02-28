package it.unibo.pss.view.geometry;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import it.unibo.pss.common.SharedConstants;

public class IsometricRenderer implements GeometryRenderer {

	private Point2D getIsometricCoordinates(int gridX, int gridY, double offsetX, double offsetY) {
		return new Point2D(
			(gridX - gridY) * (SharedConstants.TILE_WIDTH / 2.0) + offsetX,
			(gridX + gridY) * (SharedConstants.TILE_HEIGHT / 2.0) + offsetY - SharedConstants.TILE_HEIGHT
		);
	}

	@Override
	public Rectangle2D computeTileRect(int gridX, int gridY, double offsetX, double offsetY, double scale) {
		Point2D isoCoords = getIsometricCoordinates(gridX, gridY, offsetX, offsetY);
		double width = SharedConstants.SPRITE_WIDTH * scale;
		double height = SharedConstants.SPRITE_HEIGHT * scale;
		return new Rectangle2D(
			isoCoords.getX() * scale - width / 2,
			isoCoords.getY() * scale - height + (SharedConstants.SPRITE_HEIGHT / 2 * scale),
			width,
			height
		);
	}

	@Override
	public Point2D computeCenterOffset(double canvasWidth, double canvasHeight, int gridCols, int gridRows) {
		double centerTileX = (gridCols - 1) / 2.0;
		double centerTileY = (gridRows - 1) / 2.0;
		return new Point2D(
			canvasWidth / 2.0 - (centerTileX - centerTileY) * (SharedConstants.TILE_WIDTH / 2.0),
			canvasHeight / 2.0 - (centerTileX + centerTileY) * (SharedConstants.TILE_HEIGHT / 2.0)
		);
	}
}
