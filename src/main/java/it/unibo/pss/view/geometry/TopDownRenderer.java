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
	public Point2D computeCenterOffset(double canvasWidth, double canvasHeight, int gridCols, int gridRows, double scale) {
		double gridWidth = gridCols * SharedConstants.TILE_WIDTH * scale;
		double gridHeight = gridRows * SharedConstants.TILE_HEIGHT * 2 * scale;
		return new Point2D(
				(canvasWidth - gridWidth) / 2.0,
				(canvasHeight - gridHeight) / 2.0
				);
	}

	@Override
	public Point2D screenToGrid(Point2D screenPoint, double scale, Point2D cameraOffset) {
		double gridX = (screenPoint.getX() - cameraOffset.getX()) / (SharedConstants.TILE_WIDTH * scale);
		double gridY = (screenPoint.getY() - cameraOffset.getY()) / (SharedConstants.TILE_HEIGHT * 2 * scale);
		return new Point2D(Math.floor(gridX), Math.floor(gridY));
	}

	@Override
	public double[][] computeTileOutline(int gridX, int gridY, double offsetX, double offsetY, double scale) {
		Rectangle2D rect = computeTileRect(gridX, gridY, offsetX, offsetY, scale);
		double inset = 2 * scale;
		double[] xs = { 
			rect.getMinX() + inset, 
			rect.getMinX() + rect.getWidth() - inset, 
			rect.getMinX() + rect.getWidth() - inset, 
			rect.getMinX() + inset 
		};
		double[] ys = { 
			rect.getMinY() + inset, 
			rect.getMinY() + inset, 
			rect.getMinY() + rect.getHeight() - inset, 
			rect.getMinY() + rect.getHeight() - inset 
		};
		return new double[][] { xs, ys };
	}
}
