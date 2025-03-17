package it.unibo.pss.view.geometry;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import it.unibo.pss.common.SharedConstants;

/**
 * Implementation of the GeometryRenderer that renders the grid in a top-down perspective.
 */
public class TopDownRenderer implements GeometryRenderer {

	/**
	 * Computes the rectangle that represents the tile at the given grid position.
	 *
	 * @param gridX the x-coordinate of the tile in the grid
	 * @param gridY the y-coordinate of the tile in the grid
	 * @param offsetX the x-coordinate of the grid's top-left corner
	 * @param offsetY the y-coordinate of the grid's top-left corner
	 * @param scale the scale factor
	 */
	@Override
	public Rectangle2D computeTileRect(int gridX, int gridY, double offsetX, double offsetY, double scale) {
		return new Rectangle2D(
				offsetX + gridX * SharedConstants.TILE_WIDTH * scale,
				offsetY + gridY * SharedConstants.TILE_HEIGHT * 2 * scale,
				SharedConstants.TILE_WIDTH * scale,
				SharedConstants.TILE_HEIGHT * 2 * scale
				);
	}

	/**
	 * Computes the offset that centers the grid in the canvas.
	 *
	 * @param canvasWidth the width of the canvas
	 * @param canvasHeight the height of the canvas
	 * @param gridCols the number of columns in the grid
	 * @param gridRows the number of rows in the grid
	 * @param scale the scale factor
	 */
	@Override
	public Point2D computeCenterOffset(double canvasWidth, double canvasHeight, int gridCols, int gridRows, double scale) {
		double gridWidth = gridCols * SharedConstants.TILE_WIDTH * scale;
		double gridHeight = gridRows * SharedConstants.TILE_HEIGHT * 2 * scale;
		return new Point2D(
				(canvasWidth - gridWidth) / 2.0,
				(canvasHeight - gridHeight) / 2.0
				);
	}

	/**
	 * Converts a grid position to a screen position.
	 *
	 * @param gridPoint the grid position
	 * @param scale the scale factor
	 * @param cameraOffset the offset of the camera
	 */
	@Override
	public Point2D screenToGrid(Point2D screenPoint, double scale, Point2D cameraOffset) {
		double gridX = (screenPoint.getX() - cameraOffset.getX()) / (SharedConstants.TILE_WIDTH * scale);
		double gridY = (screenPoint.getY() - cameraOffset.getY()) / (SharedConstants.TILE_HEIGHT * 2 * scale);
		return new Point2D(Math.floor(gridX), Math.floor(gridY));
	}

	/**
	 * Converts a screen position to a grid position.
	 *
	 * @param screenPoint the screen position
	 * @param scale the scale factor
	 * @param cameraOffset the offset of the camera
	 */
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
