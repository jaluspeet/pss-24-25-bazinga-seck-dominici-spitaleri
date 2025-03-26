package it.unibo.bazinga.view.geometry;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import it.unibo.bazinga.common.SharedConstants;

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
		return new Rectangle2D(offsetX + gridX * SharedConstants.TILE_WIDTH * scale, offsetY + gridY * SharedConstants.TILE_HEIGHT * 2 * scale, SharedConstants.TILE_WIDTH * scale, SharedConstants.TILE_HEIGHT * 2 * scale);
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
		return new Point2D((canvasWidth - gridWidth) / 2.0, (canvasHeight - gridHeight) / 2.0);
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

	@Override
	public double[][] computeTileOutline(int gridX, int gridY, double offsetX, double offsetY, double scale) {
		Rectangle2D rect = computeTileRect(gridX, gridY, offsetX, offsetY, scale);
		double[] xs = { rect.getMinX(), rect.getMinX() + rect.getWidth(), rect.getMinX() + rect.getWidth(), rect.getMinX() };
		double[] ys = { rect.getMinY(), rect.getMinY(), rect.getMinY() + rect.getHeight(), rect.getMinY() + rect.getHeight() };
		return new double[][] { xs, ys };
	}


}
