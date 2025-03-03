package it.unibo.pss.view.geometry;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import it.unibo.pss.common.SharedConstants;

public class TopDownRenderer implements GeometryRenderer {

	@Override
	public Rectangle2D computeTileRect(int gridX, int gridY, double offsetX, double offsetY, double scale) {
		return new Rectangle2D(
			offsetX + gridX * SharedConstants.TILE_WIDTH * scale,
			offsetY + gridY * SharedConstants.TILE_HEIGHT * 2 * scale,
			SharedConstants.TILE_WIDTH * scale,
			SharedConstants.TILE_HEIGHT * 2 * scale
		);
	}

	@Override
	public Point2D computeCenterOffset(double canvasWidth, double canvasHeight, int gridCols, int gridRows) {
		return new Point2D(
			(canvasWidth - gridCols * SharedConstants.TILE_WIDTH) / 2.0,
			(canvasHeight - gridRows * SharedConstants.TILE_HEIGHT * 2) / 2.0
		);
	}
}
